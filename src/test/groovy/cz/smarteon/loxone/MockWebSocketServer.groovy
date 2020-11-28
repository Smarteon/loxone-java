package cz.smarteon.loxone

import com.fasterxml.jackson.databind.node.NullNode
import cz.smarteon.loxone.message.JsonValue
import cz.smarteon.loxone.message.LoxoneMessage
import cz.smarteon.loxone.message.LoxoneValue
import cz.smarteon.loxone.message.SerializationSupport
import groovy.transform.TupleConstructor
import org.hamcrest.Matcher
import org.hamcrest.StringDescription
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

import static cz.smarteon.loxone.Codec.hexToBytes
import static cz.smarteon.loxone.CryptoSupport.SERVER_PRIVATE_KEY
import static cz.smarteon.loxone.CryptoSupport.TOKEN
import static cz.smarteon.loxone.CryptoSupport.USER
import static cz.smarteon.loxone.CryptoSupport.USER_HASH
import static cz.smarteon.loxone.CryptoSupport.USER_KEY
import static cz.smarteon.loxone.CryptoSupport.USER_VISUSALT
import static cz.smarteon.loxone.CryptoSupport.VISU_HASH

class MockWebSocketServer extends WebSocketServer implements SerializationSupport {

    private SecretKey sharedKey
    private byte[] sharedKeyIv

    private final MockWebSocketServerListener listener
    private final Stubbing stubbing
    private final int processingDelayMs


    MockWebSocketServer(MockWebSocketServerListener listener, int processingDelayMs) {
        super(new InetSocketAddress(0))
        setReuseAddr(true)
        this.listener = listener
        stubbing = new Stubbing()
        this.processingDelayMs = processingDelayMs
    }

    MockWebSocketServer(MockWebSocketServer toCopy) {
        super(new InetSocketAddress(toCopy.port))
        setReuseAddr(true)
        stubbing = toCopy.stubbing
        listener = toCopy.listener
    }

    @Override
    void onOpen(final WebSocket conn, final ClientHandshake handshake) {

    }

    @Override
    void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
        listener.stopped()
    }

    @Override
    void onMessage(final WebSocket conn, final String message) {
        sleep(processingDelayMs) // simulates the real miniserver processing delay
        def exchange = message =~ /jdev\/sys\/keyexchange\/(.*)/
        if (exchange.find()) {
            def sharedKeyEnc = exchange.group(1).decodeBase64()
            Cipher.getInstance("RSA/ECB/PKCS1Padding").with {
                init(DECRYPT_MODE, SERVER_PRIVATE_KEY)
                def decryptedKey = new String(doFinal(sharedKeyEnc))
                sharedKey = new SecretKeySpec(hexToBytes(decryptedKey[0..-34]), "AES")
                sharedKeyIv = hexToBytes(decryptedKey[-32..-1])
            }
            broadcast("dev/sys/keyexchange/${exchange.group(1)}", 200)
            return
        }

        if (message == "jdev/sys/getkey2/$USER") {
            broadcast(writeValue(USER_KEY))
            return
        }

        def encryptedMsg = message =~ /jdev\/sys\/enc\/(.*)/
        if (encryptedMsg.find()) {
            def encrypted = URLDecoder.decode(encryptedMsg.group(1), 'UTF-8').decodeBase64()

            Cipher.getInstance("AES/CBC/ZeroBytePadding").with {
                init(DECRYPT_MODE, sharedKey, new IvParameterSpec(sharedKeyIv))
                def decrypted = new String(doFinal(encrypted)) =~ /^(.+)\/jdev\/sys\/(.*)$/

                if (decrypted.find()) {
                    onMessage(conn, decrypted.group(2))
                }
            }
            return
        }

        def gettoken = message =~ /gettoken\/(?<auth>[^\/]+)\/(?<user>[^\/]+)\/(?<tail>[24]\/[^\/]+\/loxoneJava)/
        if (gettoken.find()) {
            def control = "dev/sys/gettoken/${gettoken.group('auth')}/${gettoken.group('user')}/${gettoken.group('tail')}"
            if (0 < badCredentials) {
                badCredentials--
                broadcast(control, 401)
            } else {
                if (gettoken.group('auth') == USER_HASH) {
                    broadcast(control, 200, TOKEN)
                } else {
                    broadcast(control, 400)
                }
            }
            return
        }

        if (message == "jdev/sys/getvisusalt/$USER") {
            broadcast(writeValue(USER_VISUSALT))
            return
        }

        def securedMsg = message =~ /jdev\/sps\/ios\/(?<visuhash>[^\/]+)\/(?<cmd>.*)/
        if (securedMsg.find()) {
            if (securedMsg.group('visuhash') == VISU_HASH) {
                onMessage(conn, securedMsg.group('cmd'))
            } else {
                // TODO send failure
            }
            return
        }

        if (message == 'jdev/sps/enablebinstatusupdate') {
            broadcast('dev/sps/enablebinstatusupdate', 200)
            return
        }

        stubbing.messages.each {
            if (it.request.matches(message)) {
                broadcast(message, 200, it.response)
                it.called = true
            }
        }
    }

    @Override
    void onError(final WebSocket conn, final Exception ex) {

    }

    @Override
    void onStart() {
        listener.started()
    }

    interface MockWebSocketServerListener {
        void started()
        void stopped()
    }

    private void broadcast(String control, int code) {
        broadcast(control, code, new JsonValue(NullNode.getInstance()))
    }

    private void broadcast(String control, int code, LoxoneValue val) {
        broadcast(writeValue(new LoxoneMessage(control, code, val)))
    }

    // stubbing methods an stuff vvvvvvvvvvvv
    int badCredentials = 0


    ResponseStubbing expect(Matcher<String> req) {
        return stubbing.expect(req)
    }

    void verifyExpectations() {
        stubbing.messages.each {
            def desc = new StringDescription()
            it.request.describeTo(desc)
            assert it.called : "$desc has not been called"
        }
    }

    @TupleConstructor
    static class ResponseStubbing {
        Matcher<String> request
        LoxoneValue response
        boolean called = false

        Stubbing andReturn(LoxoneValue response) {
            this.response = response
            return stubbing
        }

        ResponseStubbing expect(Matcher<String> req) {
            return stubbing.expect(req)
        }
    }

    static class Stubbing {
        private List<ResponseStubbing> messages = []

        ResponseStubbing expect(Matcher<String> req) {
            def resp = new ResponseStubbing(req, new JsonValue(NullNode.getInstance()))
            messages.add(resp)
            return resp
        }
    }
}

package cz.smarteon.loxone

import com.fasterxml.jackson.databind.node.NullNode
import cz.smarteon.loxone.message.*
import groovy.transform.TupleConstructor
import org.hamcrest.Matcher
import org.hamcrest.StringDescription
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer

import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec

class MockWebSocketServer extends WebSocketServer implements SerializationSupport {

    private static final PrivateKey SERVER_PRIVATE_KEY =
            KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec((
                    'MIIBUgIBADANBgkqhkiG9w0BAQEFAASCATwwggE4AgEAAkBvWMsdnPTw50V/rV2l\n' +
                    'A8UpYmO3yN1vWjdxtg5PldOMb4F1x4t1KuUQwbeIUv01D53vEpjAsueg9U0wSv9m\n' +
                    'cDX/AgMBAAECQA6DRkYf1RUpL7fKgvAlI6eXOWQU/DetTJi3n/njj2U4X2L49qZN\n' +
                    'l6UyTDZsAsBFKAzQlWy0adq7WY3LFaWfICECIQCtAwykjRGaSyQnWesk08co9TAJ\n' +
                    'uU+ea47QuIY20+mXqQIhAKTBmXefJUgNEItN/KOQspl62uEbWjfpa99F8Kz6rUln\n' +
                    'AiBwVryM1DSL1SKikpZGkWSOSbZpefQiz4AqMsajLzJMEQIgcU0GCgJys/rwDqyh\n' +
                    '+aXPfMbE8RtLTroCSfgiDAMT2i8CIAUj9Pw09bHYITj3GY1x9G2wm1/C3GpwE+HL\n' +
                    'AFlnnuvp').decodeBase64()))

    static byte[] PUBLIC_KEY = ('MFswDQYJKoZIhvcNAQEBBQADSgAwRwJAb1jLHZz08OdFf61dpQPFKWJjt8jdb1o3' +
            'cbYOT5XTjG+BdceLdSrlEMG3iFL9NQ+d7xKYwLLnoPVNMEr/ZnA1/wIDAQAB').decodeBase64()

    static String USER = 'mocker'
    static String PASS = 'pass'
    static String VISU_PASS = 'visupass'

    private static final Hashing HASHING = new Hashing(
            '41434633443134324337383441373035453333424344364133373431333430413642333442334244'.decodeHex(),
            '31306137336533622D303163352D313732662D66666666616362383139643462636139')
    private static final LoxoneMessage USER_KEY = new LoxoneMessage("jdev/sys/getkey2/$USER", 200, HASHING)
    private static final LoxoneMessage USER_VISUSALT = new LoxoneMessage("dev/sys/getvisusalt/$USER", 200, HASHING)

    private static final String USER_HASH = computeUserHash(PASS, USER)
    private static final String VISU_HASH = computeUserHash(VISU_PASS)

    private static String computeUserHash(String pass, String user = null) {
        def pwHash = MessageDigest.getInstance("SHA-1")
                .digest("$pass:$HASHING.salt".bytes).encodeHex().toString().toUpperCase()
        def mac = Mac.getInstance("HmacSHA1")
        mac.init(new SecretKeySpec(HASHING.key, "HmacSHA1"))
        def toHash = user != null ? "$USER:$pwHash" : pwHash
        mac.doFinal(toHash.bytes).encodeHex().toString()
    }

    private SecretKey sharedKey
    private byte[] sharedKeyIv

    private final MockWebSocketServerListener listener
    private final Stubbing stubbing


    MockWebSocketServer(MockWebSocketServerListener listener) {
        super(new InetSocketAddress(0))
        this.listener = listener
        stubbing = new Stubbing()
    }

    MockWebSocketServer(MockWebSocketServer toCopy) {
        super(new InetSocketAddress(toCopy.port))
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
        def exchange = message =~ /jdev\/sys\/keyexchange\/(.*)/
        if (exchange.find()) {
            def sharedKeyEnc =  exchange.group(1).decodeBase64()
            Cipher.getInstance("RSA/ECB/PKCS1Padding").with {
                init(DECRYPT_MODE, SERVER_PRIVATE_KEY)
                byte[] decryptedbytes = doFinal(sharedKeyEnc)
                sharedKey = new SecretKeySpec(decryptedbytes, 0, decryptedbytes.length - 17, "AES")
                sharedKeyIv = decryptedbytes[-16..-1]
            }
            return
        }

        if (message == "jdev/sys/getkey2/$USER") {
            broadcast(MAPPER.writeValueAsString(USER_KEY))
            return
        }

        def encryptedMsg = message =~ /jdev\/sys\/enc\/(.*)/
        if (encryptedMsg.find()) {
            def encrypted = URLDecoder.decode(encryptedMsg.group(1), 'UTF-8').decodeBase64()

            Cipher.getInstance("AES/CBC/PKCS5Padding").with {
                init(DECRYPT_MODE, sharedKey, new IvParameterSpec(sharedKeyIv))
                def decrypted = new String(doFinal(encrypted)) =~ /^(.+)\/jdev\/sys\/(.*)$/

                if (decrypted.find()) {
                    onMessage(conn, decrypted.group(2))
                }
            }
            return
        }

        def gettoken = message =~ /gettoken\/(?<auth>[^\/]+)\/(?<user>[^\/]+)\/[24]\/.*/
        if (gettoken.find()) {
            def control = "jdev/sys/gettoken/${gettoken.group('user')}/"
            if (0 < badCredentials) {
                badCredentials--
                broadcast(control, 401)
            } else {
                if (gettoken.group('auth') == USER_HASH) {
                    broadcast(control, 200) // TODO gen real token
                } else {
                    // TODO send failure
                }
            }
            return
        }

        if (message == "jdev/sys/getvisusalt/$USER") {
            broadcast(MAPPER.writeValueAsString(USER_VISUSALT))
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
        broadcast(MAPPER.writeValueAsString(new LoxoneMessage(control, code, val)))
    }

    // stubbing methods an stuff vvvvvvvvvvvv
    int badCredentials = 0


    ResponseStubbing expect(Matcher<String> req) {
        return stubbing.expect(req)
    }

    void verifyExpectations(int sleep) {
        Thread.sleep(sleep)
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

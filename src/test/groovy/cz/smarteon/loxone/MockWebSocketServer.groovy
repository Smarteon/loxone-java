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

import static cz.smarteon.loxone.Codec.hexToBytes

class MockWebSocketServer extends WebSocketServer implements SerializationSupport {

    private static int LOXONE_EPOCH_BEGIN = 1230768000

    private static final PrivateKey SERVER_PRIVATE_KEY =
            KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec((
                    'MIICWgIBAAKBgF/vs3xVxg0T7WO8jVKl8RwrswkGAj+RsVHK49IEb+YA4kPXGx4f\n' +
                    'LnCC7XfN+F8MFTOTulSsoCVXp0zXwdm1TwoxtDLKx6FN2dTbMHiydPmTKPFMMQ+Y\n' +
                    '8is62sAKiQ6aBxM2U4jsTrQXY3mUUUsGbDX2w0iXzWdjFRcLV0bk2yXXAgMBAAEC\n' +
                    'gYAzadB0x7r180H7e2b5bfkDMeAm69N0oe23edYSDVKynrKjzLm5sNhAb8o3tGhw\n' +
                    '95a9J2RqUIEawhjks5Qtyl7q3Q/nXBNk9tcY7PIs+qT7mgy4t+9qKrfgsneOWZQv\n' +
                    'UJ1G0YAn7YMFlpLxEyU+p5Znssf3+p0dGuBxDT+Ryjtd0QJBAKr5dTLL0IfO8GXH\n' +
                    'F15l7RuVrh3SEEeFIAFsz4FqyyMYOcqEehLE8hsc+eXJ4/Nqro92pgETGJfvPPGm\n' +
                    'ZjkhiesCQQCPpTw+0hJi6dEze6WL2R+wCFSvKiM8mBErZ1+nUcZaOZ9s1jSM3eR+\n' +
                    'c24SOU+gjFutGZ87TXykAofTZHfQfwzFAkAe1BYuz5NNOaIdJ/XtvoEvbSDVHbBz\n' +
                    'xOxNdXpBAqmYLWEWRCbixYJGI0ZoCaxBkuXg1mr+XJwdoTSi+fcKrCJ7AkANusVf\n' +
                    'W8TWH3MXcKIKE96rfKBbfbOQfxhlBaRm4bILvaY3SOIM9Mh6LZ4/r6qktcWtbd2C\n' +
                    'VY2sP3GsCtZI31vhAkA9s3YnXfXmlFrLZJpFFQi81JZWuJJEHfHHDwqRo3xFPzat\n' +
                    'YXiOKj/osQWJR9AtxN+10y/MYusRgbMd35BtKWAh').decodeBase64()))

    static byte[] PUBLIC_KEY = (
            'MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgF/vs3xVxg0T7WO8jVKl8RwrswkG\n' +
            'Aj+RsVHK49IEb+YA4kPXGx4fLnCC7XfN+F8MFTOTulSsoCVXp0zXwdm1TwoxtDLK\n' +
            'x6FN2dTbMHiydPmTKPFMMQ+Y8is62sAKiQ6aBxM2U4jsTrQXY3mUUUsGbDX2w0iX\n' +
            'zWdjFRcLV0bk2yXXAgMBAAE=').decodeBase64()

    static String USER = 'mocker'
    static String PASS = 'pass'
    static String VISU_PASS = 'visupass'

    private static final Hashing HASHING = new Hashing(
            '41434633443134324337383441373035453333424344364133373431333430413642333442334244'.decodeHex(),
            '31306137336533622D303163352D313732662D66666666616362383139643462636139')
    private static final Token TOKEN = new Token(
            '1C368AB5FFB88B964A9F6BE71F27F16E0E42170B',
            '42444546453033423136354538374539393133433435314331333934394244363642364446353633'.decodeHex(),
            ((System.currentTimeMillis() / 1000) - LOXONE_EPOCH_BEGIN + 3600).toInteger(), 1666, false)
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

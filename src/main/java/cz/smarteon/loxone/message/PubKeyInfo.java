package cz.smarteon.loxone.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.smarteon.loxone.LoxoneException;
import org.java_websocket.util.Base64;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@JsonDeserialize(using = PubKeyInfo.Deserializer.class)
public class PubKeyInfo implements LoxoneValue {

    private final byte[] pubKey;

    PubKeyInfo(byte[] pubKey) {
        this.pubKey = pubKey;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public PublicKey asPublicKey() {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKey));
        } catch (InvalidKeySpecException e) {
            throw new LoxoneException("Unsupported type of public key", e);
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("No RSA provider present", e);
        }
    }

    public static class Deserializer extends JsonDeserializer<PubKeyInfo> {

        private static final String PUBLIC_KEY_PARSER_PATTERN = "-+BEGIN CERTIFICATE-+([^-]+)-+END CERTIFICATE-+";

        @Override
        public PubKeyInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            final String keyValue = p.readValueAs(String.class).replaceAll(PUBLIC_KEY_PARSER_PATTERN, "$1");
            return new PubKeyInfo(Base64.decode(keyValue));
        }
    }
}

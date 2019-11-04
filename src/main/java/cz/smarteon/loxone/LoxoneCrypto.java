package cz.smarteon.loxone;

import cz.smarteon.loxone.message.Hashing;
import org.java_websocket.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Codec.concatToBytes;

abstract class LoxoneCrypto {

    private static final Logger log = LoggerFactory.getLogger(LoxoneCrypto.class);

    static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("No SHA1PRNG provider present", e);
        }
    }

    static SecretKey createSharedKey() {
        try {
            final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            final SecretKey secretKey = keyGen.generateKey();
            log.trace("Created sharedKey: {}", bytesToHex(secretKey.getEncoded()));
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("No AES provider present", e);
        }
    }

    static byte[] createSharedKeyIv(final SecureRandom secureRandom) {
        final byte[] sharedKeyIv = new byte[16];
        secureRandom.nextBytes(sharedKeyIv);
        log.trace("Created sharedKeyIv: {}", bytesToHex(sharedKeyIv));
        return sharedKeyIv;
    }

    static String createSessionKey(final SecretKey sharedKey, final byte[] sharedKeyIv, final PublicKey publicKey) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedbytes = cipher.doFinal(concatToBytes(bytesToHex(sharedKey.getEncoded()), bytesToHex(sharedKeyIv)));
            log.trace("Created session key (in hex): {}", bytesToHex(encryptedbytes));
            return Base64.encodeBytes(encryptedbytes);
        } catch (NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            throw new LoxoneException("Can't encrypt sharedKey to obtain sessionKey", e);
        }
    }

    static String loxoneHashing(final String secret, final String loxoneUser, final Hashing hashing, final String operation) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] toSha1 = concatToBytes(secret, hashing.getSalt());
            final String secretHash = bytesToHex(md.digest(toSha1)).toUpperCase();
            log.trace("{} hash: {}", operation, secretHash);

            final Mac mac = Mac.getInstance("HmacSHA1");
            final SecretKeySpec secretKeySpec = new SecretKeySpec(hashing.getKey(), "HmacSHA1");
            mac.init(secretKeySpec);
            final byte[] hash = mac.doFinal(concatToBytes(loxoneUser, secretHash));
            final String finalHash = bytesToHex(hash);
            log.trace("{} final hash: {}", operation, finalHash);

            return finalHash;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new LoxoneException("Can't perform hashing to prepare " + operation, e);
        }
    }

    /**
     * Generates a random shared salt used for command encryption.
     * Hex​ ​string​ ​(length​ ​may​ ​vary,​ ​e.g.​ ​2​ ​bytes)​ ​->​ ​{salt}
     * */
    static String generateSalt(final SecureRandom secureRandom) {
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        final String salt = bytesToHex(iv);
        log.trace("new command encryption salt generated: {}", salt);
        return salt;
    }

    static String encrypt(final String data, final SecretKey sharedKey, final byte[] sharedKeyIv) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding");
            final IvParameterSpec ivspec = new IvParameterSpec(sharedKeyIv);
            cipher.init(Cipher.ENCRYPT_MODE, sharedKey, ivspec);
            return Base64.encodeBytes(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new LoxoneException("Can't perform AES encryption", e);
        }
    }

    static String decrypt(final String data, final SecretKey sharedKey, final byte[] sharedKeyIv) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec ivspec = new IvParameterSpec(sharedKeyIv);
            cipher.init(Cipher.DECRYPT_MODE, sharedKey, ivspec);
            return new String(cipher.doFinal(Base64.decode(data)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | InvalidKeyException | IOException e) {
            throw new LoxoneException("Can't perform AES decryption", e);
        }
    }

}

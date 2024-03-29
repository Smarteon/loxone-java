package cz.smarteon.loxone;

import cz.smarteon.loxone.message.Hashing;
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

import static cz.smarteon.loxone.Codec.base64ToBytes;
import static cz.smarteon.loxone.Codec.bytesToBase64;
import static cz.smarteon.loxone.Codec.bytesToHex;
import static cz.smarteon.loxone.Codec.concat;
import static cz.smarteon.loxone.Codec.concatToBytes;

/**
 * Loxone cryptography utilities.
 */
abstract class LoxoneCrypto {

    private static final Logger LOG = LoggerFactory.getLogger(LoxoneCrypto.class);

    private static final String SHA1 = "SHA1";
    private static final String SHA_1 = "SHA-1";
    private static final String SHA256 = "SHA256";
    private static final String SHA_256 = "SHA-256";

    /**
     * Get instance of cryptographically secure random generator.
     * @return secure random generator.
     */
    static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("No SHA1PRNG provider present", e);
        }
    }

    /**
     * Create shared secret key according to loxone requirements.
     * @return shared secret key
     */
    static SecretKey createSharedKey() {
        try {
            final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            final SecretKey secretKey = keyGen.generateKey();
            LOG.trace("Created sharedKey: {}", bytesToHex(secretKey.getEncoded()));
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("No AES provider present", e);
        }
    }

    /**
     * Create initial vector to be used with shared secret key.
     * @param secureRandom secure random generator to use for vector filling
     * @return initial vector
     */
    static byte[] createSharedKeyIv(final SecureRandom secureRandom) {
        final byte[] sharedKeyIv = new byte[16];
        secureRandom.nextBytes(sharedKeyIv);
        LOG.trace("Created sharedKeyIv: {}", bytesToHex(sharedKeyIv));
        return sharedKeyIv;
    }

    /**
     * Create loxone session key, ready to exchange with miniserver. Uses given public key to encrypt sharedKey.
     * Returns the encrypted sharedKey along with initial vector encoded to BASE64.
     * @param sharedKey shared key to encrypt
     * @param sharedKeyIv initial vector to be sent along the shared key
     * @param publicKey public key to use for encryption
     * @return BASE64 encoded session key
     */
    static String createSessionKey(final SecretKey sharedKey, final byte[] sharedKeyIv, final PublicKey publicKey) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            final byte[] encryptedbytes = cipher
                    .doFinal(concatToBytes(bytesToHex(sharedKey.getEncoded()), bytesToHex(sharedKeyIv)));
            LOG.trace("Created session key (in hex): {}", bytesToHex(encryptedbytes));
            return bytesToBase64(encryptedbytes);
        } catch (NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException
                 | IllegalBlockSizeException | InvalidKeyException e) {
            throw new LoxoneException("Can't encrypt sharedKey to obtain sessionKey", e);
        }
    }

    /**
     * Performs hashing algorithm as required by loxone specification.
     * Use this function when the secret is plain text (i.e. password or visualization password).
     *
     * @param secret to be hashed
     * @param loxoneUser to be hashed, can be null
     * @param hashing hashing specification
     * @param operation description of the operation the hashing is needed for - just for logging purposes
     * @return loxone hash of given parameters
     */
    static String loxoneHashing(
            final String secret,
            final String loxoneUser,
            final Hashing hashing,
            final String operation) {
        try {
            final MessageDigest md = MessageDigest.getInstance(getDigestAlg(hashing.getHashAlg()));
            final byte[] toSha1 = concatToBytes(secret, hashing.getSalt());
            final String secretHash = bytesToHex(md.digest(toSha1)).toUpperCase();
            LOG.trace("{} hash: {}", operation, secretHash);

            final String toFinalHash = loxoneUser != null ? concat(loxoneUser, secretHash) : secretHash;
            return loxoneHashing(toFinalHash, hashing, operation);
        } catch (NoSuchAlgorithmException e) {
            throw new LoxoneException("Can't perform hashing to prepare " + operation, e);
        }
    }

    /**
     * Performs hashing algorithm as required by loxone specification.
     * Use this function when only the given secret is already HEX encoded SHA-1 hash (i.e. token).
     *
     * @param secret to be hashed
     * @param hashing hashing specification
     * @param operation description of the operation the hashing is needed for - just for logging purposes
     * @return loxone hash of given parameters
     */
    static String loxoneHashing(final String secret, final Hashing hashing, final String operation) {
        try {
            final String hmacAlg = "Hmac" + getHmacAlg(hashing.getHashAlg());
            final Mac mac = Mac.getInstance(hmacAlg);
            final SecretKeySpec secretKeySpec = new SecretKeySpec(hashing.getKey(), hmacAlg);
            mac.init(secretKeySpec);
            final byte[] hash = mac.doFinal(secret.getBytes());
            final String finalHash = bytesToHex(hash);
            LOG.trace("{} final hash: {}", operation, finalHash);

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
        final byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        final String salt = bytesToHex(iv);
        LOG.trace("new command encryption salt generated: {}", salt);
        return salt;
    }

    /**
     * Encrypts given data using given shared key and initial vector as required by loxone specification.
     * @param data to encrypt
     * @param sharedKey shared secret key
     * @param sharedKeyIv initial vector
     * @return BASE64 encoded encrypted data
     */
    static String encrypt(final String data, final SecretKey sharedKey, final byte[] sharedKeyIv) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding");
            final IvParameterSpec ivspec = new IvParameterSpec(sharedKeyIv);
            cipher.init(Cipher.ENCRYPT_MODE, sharedKey, ivspec);
            return bytesToBase64(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new LoxoneException("Can't perform AES encryption", e);
        }
    }

    /**
     * Descrypts given data using given shared key and initial vector as required by loxone specification.
     * @param data BASE64 encoded and encrypted (using {@link #encrypt(String, SecretKey, byte[])}
     * @param sharedKey shared secret key
     * @param sharedKeyIv initial vector
     * @return decrypted data
     */
    static String decrypt(final String data, final SecretKey sharedKey, final byte[] sharedKeyIv) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec ivspec = new IvParameterSpec(sharedKeyIv);
            cipher.init(Cipher.DECRYPT_MODE, sharedKey, ivspec);
            return new String(cipher.doFinal(base64ToBytes(data)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | InvalidKeyException | IllegalArgumentException e) {
            throw new LoxoneException("Can't perform AES decryption", e);
        }
    }

    private static String getDigestAlg(final String hashAlg) {
        if (hashAlg == null || SHA1.equals(hashAlg)) {
            return SHA_1;
        } else if (SHA256.equals(hashAlg)) {
            return SHA_256;
        } else {
            throw new LoxoneException("Unsupported hashing algorithm " + hashAlg);
        }
    }

    private static String getHmacAlg(final String hashAlg) {
        return hashAlg != null ? hashAlg : SHA1;
    }

}

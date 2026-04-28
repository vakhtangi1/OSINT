package com.osint.backend.security;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtil {

    private static final String AES = "AES";
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final String HMAC_SHA256 = "HmacSHA256";

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private static final String DEFAULT_DEV_KEY =
            "ChangeThisDevKeyForRealProduction123";

    private static byte[] getKeyBytes() {
        try {
            String key = System.getenv("OSINT_CRYPTO_KEY");

            if (key == null || key.isBlank()) {
                key = System.getProperty(
                        "OSINT_CRYPTO_KEY",
                        DEFAULT_DEV_KEY
                );
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return digest.digest(
                    key.getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception e) {
            throw new RuntimeException("Key generation failed", e);
        }
    }

    public static String encrypt(String plainText) {
        try {
            if (plainText == null || plainText.isBlank()) {
                return plainText;
            }

            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM);

            SecretKeySpec keySpec =
                    new SecretKeySpec(getKeyBytes(), AES);

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] encrypted =
                    cipher.doFinal(
                            plainText.getBytes(StandardCharsets.UTF_8)
                    );

            return "v1:"
                    + Base64.getEncoder().encodeToString(iv)
                    + ":"
                    + Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.isBlank()) {
                return encryptedText;
            }

            if (!encryptedText.startsWith("v1:")) {
                return encryptedText;
            }

            String[] parts = encryptedText.split(":");

            byte[] iv =
                    Base64.getDecoder().decode(parts[1]);

            byte[] encrypted =
                    Base64.getDecoder().decode(parts[2]);

            Cipher cipher = Cipher.getInstance(AES_GCM);

            SecretKeySpec keySpec =
                    new SecretKeySpec(getKeyBytes(), AES);

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public static String hmacHash(String value) {
        try {
            if (value == null || value.isBlank()) {
                return null;
            }

            Mac mac = Mac.getInstance(HMAC_SHA256);

            SecretKeySpec keySpec =
                    new SecretKeySpec(
                            getKeyBytes(),
                            HMAC_SHA256
                    );

            mac.init(keySpec);

            byte[] hash =
                    mac.doFinal(
                            value.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Hash failed", e);
        }
    }
}
package com.osint.backend.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-GCM encryption + HMAC-SHA256 hashing utility.
 * Key is injected from application.properties (osint.crypto.key).
 * Registered as a Spring @Component so the EncryptedStringConverter can use it.
 */
@Component
public class CryptoUtil {

    private static final String AES         = "AES";
    private static final String AES_GCM     = "AES/GCM/NoPadding";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final int    IV_LENGTH   = 12;
    private static final int    TAG_LENGTH  = 128;

    // Injected from application.properties
    @Value("${osint.crypto.key}")
    private String cryptoKeyRaw;

    // Singleton accessible to the JPA converter (which is not Spring-managed)
    private static byte[] keyBytes;

    @PostConstruct
    private void init() {
        try {
            String key = cryptoKeyRaw;

            // Allow override via OS environment variable at runtime
            String envKey = System.getenv("OSINT_CRYPTO_KEY");
            if (envKey != null && !envKey.isBlank()) {
                key = envKey;
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            keyBytes = digest.digest(key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("CryptoUtil key initialisation failed", e);
        }
    }

    // ── Static helpers (used by EncryptedStringConverter) ────────────────────

    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isBlank()) return plainText;
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM);
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(keyBytes, AES),
                    new GCMParameterSpec(TAG_LENGTH, iv));

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return "v1:"
                    + Base64.getEncoder().encodeToString(iv)
                    + ":"
                    + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isBlank()) return encryptedText;
        if (!encryptedText.startsWith("v1:")) return encryptedText; // legacy plaintext pass-through
        try {
            String[] parts    = encryptedText.split(":");
            byte[]   iv       = Base64.getDecoder().decode(parts[1]);
            byte[]   encrypted = Base64.getDecoder().decode(parts[2]);

            Cipher cipher = Cipher.getInstance(AES_GCM);
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(keyBytes, AES),
                    new GCMParameterSpec(TAG_LENGTH, iv));

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public static String hmacHash(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(keyBytes, HMAC_SHA256));
            return Base64.getEncoder().encodeToString(
                    mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Hash failed", e);
        }
    }
}
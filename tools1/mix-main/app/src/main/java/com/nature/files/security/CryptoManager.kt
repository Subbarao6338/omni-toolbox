package com.nature.files.security

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Manages encryption and decryption for the secure Vault.
 * Uses AES-256-GCM with PBKDF2 key derivation.
 * Stream-based to avoid OutOfMemoryError on large files.
 */
class CryptoManager(private val context: Context) {
    @Suppress("DEPRECATION")
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val ALGORITHM = "AES/GCM/NoPadding"
    private val TAG_LENGTH_BIT = 128
    private val IV_LENGTH_BYTE = 12
    private val SALT_LENGTH_BYTE = 16
    private val ITERATIONS = 10000
    private val KEY_LENGTH_BIT = 256

    fun encryptFile(file: File, encryptedFile: File, password: CharArray? = null) {
        if (password == null) {
            @Suppress("DEPRECATION")
            val encrypted = EncryptedFile.Builder(
                encryptedFile,
                context,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            file.inputStream().use { input ->
                encrypted.openFileOutput().use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            val salt = ByteArray(SALT_LENGTH_BYTE)
            SecureRandom().nextBytes(salt)
            val iv = ByteArray(IV_LENGTH_BYTE)
            SecureRandom().nextBytes(iv)

            val secretKey = deriveKey(password, salt)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH_BIT, iv))

            FileOutputStream(encryptedFile).use { fos ->
                fos.write(salt)
                fos.write(iv)
                CipherOutputStream(fos, cipher).use { cos ->
                    file.inputStream().use { fis ->
                        fis.copyTo(cos)
                    }
                }
            }
        }
    }

    fun decryptFile(encryptedFile: File, decryptedFile: File, password: CharArray? = null) {
        if (password == null) {
            @Suppress("DEPRECATION")
            val encrypted = EncryptedFile.Builder(
                encryptedFile,
                context,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            encrypted.openFileInput().use { input ->
                decryptedFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            FileInputStream(encryptedFile).use { fis ->
                val salt = ByteArray(SALT_LENGTH_BYTE)
                if (fis.read(salt) != SALT_LENGTH_BYTE) return
                val iv = ByteArray(IV_LENGTH_BYTE)
                if (fis.read(iv) != IV_LENGTH_BYTE) return

                val secretKey = deriveKey(password, salt)
                val cipher = Cipher.getInstance(ALGORITHM)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH_BIT, iv))

                CipherInputStream(fis, cipher).use { cis ->
                    decryptedFile.outputStream().use { fos ->
                        cis.copyTo(fos)
                    }
                }
            }
        }
    }

    private fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BIT)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}

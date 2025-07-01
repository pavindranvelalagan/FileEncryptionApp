import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.function.IntConsumer;

public class FileEncryptor {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 128;
    private static final int ITERATIONS = 65536;
    private static final int SALT_SIZE = 16;
    private static final int IV_SIZE = 16;

    public static void encrypt(File inputFile, String password, IntConsumer progressCallback) throws Exception {
        byte[] salt = generateRandomBytes(SALT_SIZE);
        byte[] iv = generateRandomBytes(IV_SIZE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKey key = getKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        File outFile = new File(inputFile.getAbsolutePath() + ".enc");

        try (
            FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(outFile);
            CipherOutputStream cos = new CipherOutputStream(fos, cipher)
        ) {
            fos.write(salt);
            fos.write(iv);

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytes = inputFile.length();
            long processedBytes = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
                processedBytes += bytesRead;
                int percent = (int) ((processedBytes * 100) / totalBytes);
                progressCallback.accept(percent);
            }

            progressCallback.accept(100);
        }
    }

    public static void decrypt(File encryptedFile, String password, IntConsumer progressCallback) throws Exception {
        try (
            FileInputStream fis = new FileInputStream(encryptedFile)
        ) {
            byte[] salt = fis.readNBytes(SALT_SIZE);
            byte[] iv = fis.readNBytes(IV_SIZE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKey key = getKeyFromPassword(password, salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            String originalPath = encryptedFile.getAbsolutePath().replaceFirst("\\.enc$", "");
            File outFile = new File(originalPath);

            try (
                FileOutputStream fos = new FileOutputStream(outFile);
                CipherInputStream cis = new CipherInputStream(fis, cipher)
            ) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytes = encryptedFile.length();
                long processedBytes = SALT_SIZE + IV_SIZE;

                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    processedBytes += bytesRead;
                    int percent = (int) ((processedBytes * 100) / totalBytes);
                    progressCallback.accept(percent);
                }

                progressCallback.accept(100);
            }
        }
    }

    private static SecretKey getKeyFromPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}

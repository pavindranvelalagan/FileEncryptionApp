import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class FileEncryptor {

    // Constants for algorithm settings
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";  // AES in CBC mode with padding
    private static final int KEY_SIZE = 128;                          // Key size: 128 bits
    private static final int ITERATIONS = 65536;                      // PBKDF2 iteration count
    private static final int SALT_SIZE = 16;                          // Salt size in bytes
    private static final int IV_SIZE = 16;                            // AES IV size (128 bits)

    /**
     * Encrypts the input file using the given password
     */
    public static void encrypt(File inputFile, String password) throws Exception {
        // Generate a random salt for this encryption session
        byte[] salt = generateRandomBytes(SALT_SIZE);

        // Generate a random Initialization Vector (IV)
        byte[] iv = generateRandomBytes(IV_SIZE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Generate secret key from password and salt
        SecretKey key = getKeyFromPassword(password, salt);

        // Set up cipher for encryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // Define output file name by appending ".enc"
        File outFile = new File(inputFile.getAbsolutePath() + ".enc");

        // Open file streams
        try (
            FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(outFile)
        ) {
            // Write salt and IV at the beginning of the file
            fos.write(salt);
            fos.write(iv);

            // Create a CipherOutputStream that encrypts the data as it's written
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            // Read file and write encrypted data
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }

            cos.flush();
            cos.close();
        }
    }

    /**
     * Decrypts the encrypted input file using the given password
     */
    public static void decrypt(File encryptedFile, String password) throws Exception {
        // Open input stream
        try (
            FileInputStream fis = new FileInputStream(encryptedFile)
        ) {
            // Read salt and IV from the beginning of the file
            byte[] salt = fis.readNBytes(SALT_SIZE);
            byte[] iv = fis.readNBytes(IV_SIZE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Reconstruct key from password and salt
            SecretKey key = getKeyFromPassword(password, salt);

            // Set up cipher for decryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            // Create output file name by removing ".enc"
            String originalPath = encryptedFile.getAbsolutePath().replaceFirst("\\.enc$", "");
            File outFile = new File(originalPath);

            // Write decrypted content to file
            try (
                FileOutputStream fos = new FileOutputStream(outFile);
                CipherInputStream cis = new CipherInputStream(fis, cipher)
            ) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    /**
     * Generates a secure AES key from the password and salt
     */
    private static SecretKey getKeyFromPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Use PBKDF2 to derive a key from password and salt
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * Generates secure random bytes (used for salt and IV)
     */
    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}

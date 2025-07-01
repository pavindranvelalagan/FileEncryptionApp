# FileEncryptionApp

A simple, secure desktop app to encrypt and decrypt your files using strong AES encryption. Protect your sensitive data with just a password—no technical expertise required!

---

## 🚀 Introduction

**FileEncryptionApp** lets you easily encrypt and decrypt files on your computer. It uses industry-standard cryptography to keep your data safe, even if someone gets access to your files.

- **No cloud required**: Everything happens on your device.
- **Open source**: Transparent and free for anyone to use or improve.
- **Beginner-friendly**: Simple interface—just pick a file and set a password.

---

## ✨ Features

- AES-128 encryption (used by banks and governments)
- Password-based encryption (no need to remember complex keys)
- Automatic salt and IV generation for extra security
- Simple, intuitive desktop interface
- Cross-platform (runs anywhere Java is supported)

---

## 🛠️ How It Works

When you encrypt a file, the app:
1. **Generates a random salt** (16 bytes)
2. **Generates a random IV** (Initialization Vector, 16 bytes)
3. **Derives a secure key** from your password + salt using PBKDF2 (65,536 rounds)
4. **Encrypts your file** using AES in CBC mode with PKCS5 padding
5. **Saves the encrypted file** as:

   `[16 bytes salt] + [16 bytes IV] + [encrypted file content]`

When you decrypt:
- The app reads the salt and IV from the file
- Rebuilds the key from your password + salt
- Decrypts the file using AES

**Visual Overview:**

```
+-------------------+-------------------+--------------------------+
|   16 bytes salt   |   16 bytes IV     |   Encrypted file data    |
+-------------------+-------------------+--------------------------+
```

---

## 📦 Installation

1. **Download or clone this repository:**
   ```
   git clone https://github.com/pavindranvelalagan/FileEncryptionApp.git
   ```
2. **Build the project:**
   - Open the project in your favorite Java IDE (e.g., IntelliJ, Eclipse)
   - Or compile from terminal:
     ```
     cd FileEncryptionApp/src
     javac *.java
     ```
3. **Run the app:**
   ```
   java MainFrame
   ```

---

## 📝 Usage

1. **Open the app**
2. **Choose a file** you want to encrypt or decrypt
3. **Enter a password** (make it strong and memorable!)
4. **Click Encrypt or Decrypt**
5. **Done!**

- Encrypted files will have a `.enc` extension
- To decrypt, just select the `.enc` file and enter the same password

---

## 🔒 Security Details

- **AES-128**: Strong, trusted encryption
- **PBKDF2**: Turns your password + salt into a secure key, making brute-force attacks much harder
- **Salt**: Random data added to your password to prevent precomputed attacks
- **IV**: Ensures each encryption is unique, even with the same file and password

**Note:**
- If you forget your password, your data cannot be recovered!
- The app never uploads your files or passwords anywhere

---

## 📁 File Structure

```
FileEncryptionApp/
  README.md
  src/
    FileEncryptor.java      # Core encryption/decryption logic
    MainFrame.java          # User interface
```

---

## 🤝 Contributing

Contributions are welcome! To contribute:
1. Fork this repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Push to your fork and open a Pull Request

---

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

## 📬 Contact

Questions, suggestions, or issues? Open an issue on GitHub or contact the maintainer at [e21283@eng.pdn.ac.lk].

---

**Enjoy safe and simple file encryption!**
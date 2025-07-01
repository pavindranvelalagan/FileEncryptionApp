// Import basic Swing and AWT components
import javax.swing.*;              // For GUI components like JFrame, JButton, etc.
import java.awt.*;                 // For layout managers and styling
import java.awt.event.*;           // For button click actions
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.List;
import java.io.File;               // To handle file selection

// Main class that creates the GUI window
public class MainFrame extends JFrame {

    // GUI components that need to be accessed globally within the class
    private JTextField fileField;        // Shows selected file path (not editable)
    private JPasswordField passwordField; // Field for user to enter password
    private File selectedFile;           // Stores the selected file from FileChooser
    private JProgressBar progressBar;   //Shows the progress of the encryption or decryption


    // Constructor that builds the UI
    public MainFrame() {
        // Set the window title
        setTitle("File Encryption Tool");

        // Set the window size (width x height)
        setSize(500, 200);

        // Close the program when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Create a panel with 4 rows and 1 column, and 10px spacing
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Create a button to select a file
        JButton selectFileButton = new JButton("Select File");

        // Create a text field to show the selected file path
        fileField = new JTextField();
        // Allow drag and drop into the text field
        new DropTarget(fileField, new DropTargetListener() {
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {}

        @Override
        public void dragOver(DropTargetDragEvent dtde) {}

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {}

        @Override
        public void dragExit(DropTargetEvent dte) {}

        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                // Accept the drop
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                
                // Get dropped files
                Transferable t = dtde.getTransferable();
                java.util.List<File> droppedFiles = (java.util.List<File>)
                        t.getTransferData(DataFlavor.javaFileListFlavor);

                // Only handle the first file
                if (!droppedFiles.isEmpty()) {
                    selectedFile = droppedFiles.get(0);
                    fileField.setText(selectedFile.getAbsolutePath());
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing dropped file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        });



        fileField.setEditable(false);  // User cannot type here

        // When the "Select File" button is clicked, call chooseFile() method
        selectFileButton.addActionListener(e -> chooseFile());

        // Create a password input field
        passwordField = new JPasswordField();



        // Create progress bar
        progressBar = new JProgressBar();        // Create progress bar component
        progressBar.setStringPainted(true);      // Show percentage as text
        progressBar.setValue(0);                 // Start at 0%



        // Create Encrypt and Decrypt buttons
        JButton encryptButton = new JButton("Encrypt");
        JButton decryptButton = new JButton("Decrypt");

        // When "Encrypt" is clicked
        encryptButton.addActionListener(e -> {
            // Get password from the field
            String password = new String(passwordField.getPassword());

            // Check if file and password are valid
            if (selectedFile == null || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a file and enter a password.");
                return;
            }

            try {
                // Call the encrypt method from FileEncryptor
                FileEncryptor.encrypt(selectedFile, password, progress -> progressBar.setValue(progress));
                JOptionPane.showMessageDialog(this, "Encryption successful! Encrypted file saved as: " + selectedFile.getName() + ".enc");
            } catch (Exception ex) {
                // Show error if encryption fails
                JOptionPane.showMessageDialog(this, "Encryption failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // When "Decrypt" is clicked
        decryptButton.addActionListener(e -> {
            // Get password from the field
            String password = new String(passwordField.getPassword());

            // Check if file and password are valid
            if (selectedFile == null || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an encrypted file and enter the password.");
                return;
            }

            try {
                // Call the decrypt method from FileEncryptor
                FileEncryptor.decrypt(selectedFile, password, progress -> progressBar.setValue(progress));
                JOptionPane.showMessageDialog(this, "Decryption successful! File restored.");
            } catch (Exception ex) {
                // Show error if decryption fails
                JOptionPane.showMessageDialog(this, "Decryption failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });


        // Add components to the vertical panel (top to bottom)
        panel.add(selectFileButton);             // Row 1: Select file button
        panel.add(fileField);                    // Row 2: Shows selected file path
        panel.add(new JLabel("Enter Password:"));// Row 3: Label above password
        panel.add(passwordField);                // Row 4: Password field
        panel.add(progressBar);                  // Row 5: Progress bar

        // Create a new panel to place Encrypt and Decrypt buttons side-by-side
        JPanel buttonPanel = new JPanel();       // Default is FlowLayout (left to right)
        buttonPanel.add(encryptButton);          // Add Encrypt button
        buttonPanel.add(decryptButton);          // Add Decrypt button

        // Add both panels to the main window (JFrame)
        add(panel, BorderLayout.CENTER);         // Form in the center
        add(buttonPanel, BorderLayout.SOUTH);    // Buttons at the bottom
    }

    // Method to open file chooser dialog and store selected file
    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();      // Create file chooser dialog
        int result = chooser.showOpenDialog(this);      // Show dialog and wait for user

        if (result == JFileChooser.APPROVE_OPTION) {    // If user selects a file
            selectedFile = chooser.getSelectedFile();   // Store the selected file
            fileField.setText(selectedFile.getAbsolutePath()); // Show path in text field
        }
    }

    // Main method that launches the GUI application
    public static void main(String[] args) {
        // This ensures that Swing GUI is created safely in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create an instance of MainFrame and make it visible
            new MainFrame().setVisible(true);
        });
    }
}

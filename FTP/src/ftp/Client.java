package ftp;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static final int port = 7777;
    private static Socket socket = null;

    public static void main(String[] args) throws IOException {
        String folderPath = "";
        InetAddress ipAddress = InetAddress.getLocalHost();

        while (true) {
            int choice = showMenu();
            switch (choice) {
                case 1:
                    folderPath = getFolderPath();
                    break;
                case 2:
                    if (socket == null || socket.isClosed()) {
                        socket = new Socket(ipAddress, port);
                        System.out.println("Connected to server.");
                    } else {
                        System.out.println("Already connected to server.");
                    }
                    break;
                case 3:
                    if (socket != null && !socket.isClosed()) {
                        receiveFile(folderPath);
                    } else if(socket == null) {
                        System.out.println("Not connected to server. Please connect first.");
                    }else if(socket.isClosed()){
                        socket = new Socket(ipAddress, port);
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    public static void receiveFile(String folderPath) throws IOException {
        InputStream inputStream = socket.getInputStream();

        // Read the filename length
        int fileNameLength = inputStream.read();

        // Read the filename bytes
        byte[] fileNameBytes = new byte[fileNameLength];
        inputStream.read(fileNameBytes);
        String fileName = new String(fileNameBytes);

        // Create FileOutputStream using the received filename
        FileOutputStream fileOutputStream = new FileOutputStream(folderPath + "\\" + fileName);

        // Create a buffer to read the file in chunks
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        // Close streams
        fileOutputStream.close();
        inputStream.close();
    }

    public static String getFolderPath() {
        String filePath = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(fileChooser);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();
        }
        return filePath;
    }

    public static int showMenu() {
        String[] options = {"Select destination folder", "Connect with server", "Receive file", "Exit"};
        int choice = JOptionPane.showOptionDialog(null, "Select an option", "FTP Client", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return choice + 1; // Adjust choice to match menu options (1-based)
    }
}

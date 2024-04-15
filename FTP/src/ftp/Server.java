package ftp;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class Server {
    public static final int port = 7777;
    public static String filePath = "";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = null;

        while (true) {
            int choice = showMenu();
            switch (choice) {
                case 1:
                    filePath = getFilePath();
                    break;
                case 2:
                    System.out.println("Waiting for client to connect....");
                    socket = serverSocket.accept();
                    System.out.println("Client is connected....\n\n");
                    break;
                case 3:
                    if(socket != null && !socket.isClosed()){
                        sendFile(filePath, socket);
                    }else if(socket == null){
                        System.out.println("Not connected to server. Please connect first.");
                    }else if(socket.isClosed()){
                        socket = serverSocket.accept();
                        sendFile(filePath, socket);
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    if (socket != null) {
                        socket.close();
                    }
                    serverSocket.close();
                    System.exit(0); // Exit the program
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    public static int showMenu() {
        String[] options = {"Select file to send", "Connect with client", "Send file", "Exit"};
        int choice = JOptionPane.showOptionDialog(null, "Select an option", "FTP Server", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return choice + 1; // Adjust choice to match menu options (1-based)
    }

    public static String getFilePath() {
        String filePath = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();
        }
        return filePath;
    }

    public static void sendFile(String filePath, Socket socket) throws IOException {
        // Get the file size
        File file = new File(filePath);
        long fileSize = file.length();

        // Create a buffer to read the file
        byte[] buffer = new byte[1024];
        FileInputStream fileInputStream = new FileInputStream(file);

        // Get the file name
        String fileName = file.getName();

        // Get the output stream to send data to the client
        OutputStream outputStream = socket.getOutputStream();

        // Write the filename length and filename to the output stream
        byte[] fileNameBytes = fileName.getBytes();
        outputStream.write(fileNameBytes.length); // Write the length of the filename
        outputStream.write(fileNameBytes); // Write the filename

        // Read the file into the buffer and send to the client
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // Close streams and sockets
        fileInputStream.close();
        outputStream.close();
    }
}

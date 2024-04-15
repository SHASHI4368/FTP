package ftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileThread implements Runnable {
    private String filePath;
    private Socket socket;
    private ServerSocket serverSocket;

    public FileThread(String path, Socket s, ServerSocket ss) {
        filePath = path;
        socket = s;
        serverSocket = ss;
    }

    @Override
    public void run() {
        try {
            sendFile(filePath, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

package ftp;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class FileSender implements Runnable {
    private List<String> filePaths;
    ServerThread serverThread;

    public FileSender(List<String> filePaths, ServerThread serverThread) throws IOException {
        this.filePaths = filePaths;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        try {
            // Send the size of the filePaths ArrayList to the client

                if (!filePaths.isEmpty()) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverThread.socket.getOutputStream());
                    objectOutputStream.writeInt(filePaths.size());
                    objectOutputStream.flush();
                }
                for (Object filePath : filePaths) {
                    if (serverThread.socket != null && !serverThread.socket.isClosed()) {
                        sendFile((String) filePath, serverThread.socket);
                    } else if (serverThread.socket == null) {
                        System.out.println("Not connected to server. Please connect first.");
                    } else if (serverThread.socket.isClosed()) {
                        synchronized (serverThread.serverSocket) {
                            serverThread.socket = serverThread.serverSocket.accept();
                            sendFile((String) filePath, serverThread.socket);
                        }
                    }
                }
                serverThread.socket = serverThread.serverSocket.accept();
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
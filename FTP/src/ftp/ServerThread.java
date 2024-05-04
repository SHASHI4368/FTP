package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread {
    public ServerSocket serverSocket;
    public Socket socket;

    public ServerThread(ServerSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        socket = serverSocket.accept();
    }
}

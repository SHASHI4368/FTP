package ftp;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    public static final int port = 7777;
    public static ArrayList filePaths = new ArrayList();
    public static ArrayList clients = new ArrayList();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = null;
        while (true) {
            int choice = showMenu();
            switch (choice) {
                case 1:
                    filePaths = getFilePath();
                    break;
                case 2:
                    System.out.println("Waiting for client to connect....");
                    ServerThread s = new ServerThread(serverSocket);
                    clients.add(s);
                    break;
                case 3:
                    System.out.println(clients.size());
                    for(int i=0; i<clients.size(); i++){
                        Thread t = new Thread(new FileSender(filePaths, ((ServerThread)clients.get(i))));
                        t.start();
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

    public static ArrayList getFilePath(){
        ArrayList fileNames = new ArrayList();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(fileChooser);

        if(result == JFileChooser.APPROVE_OPTION){
            File [] selectedFiles = fileChooser.getSelectedFiles();

            for(File file: selectedFiles){
                fileNames.add(file.getAbsolutePath());
            }
        }
        return fileNames;
    }

}

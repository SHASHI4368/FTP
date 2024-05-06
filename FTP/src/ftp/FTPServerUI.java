package ftp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FTPServerUI extends JFrame {
    private JButton searchClientsButton, selectFilesButton, sendButton;
    private JList<String> clientList, fileList;
    private DefaultListModel<String> clientListModel, fileListModel;
    private JFileChooser fileChooser;
    private String ethernetIp = "";
    private String wlanIp = "";
    public static final int port = 7777;
    public static ArrayList filePaths = new ArrayList();
    public static ArrayList clients = new ArrayList();
    public static ServerSocket serverSocket = null;

    public FTPServerUI() {
        super("FTP Server");
        wlanIp = getWifiIpAddress();
        ethernetIp = getEthernetIPAddress();
        createUI();
    }

    public static String getWifiIpAddress() {
        String wifiIpAddress = null; // Variable to store the Wi-Fi IP address
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address.getHostAddress().contains("192.168.")) {
                            // Assuming 192.168. is the prefix of your Wi-Fi IP address
                            wifiIpAddress = address.getHostAddress(); // Update the variable with the new IP address
                        }
                    }
                }
            }
            if (wifiIpAddress != null) {
                return wifiIpAddress; // Return the last Wi-Fi IP address found
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public static String getEthernetIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && iface.getName().startsWith("eth")) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("error");
            e.printStackTrace();
        }
        return null;
    }

    private void createUI() {
        try {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 500);
            setLayout(new BorderLayout(5, 5));
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JPanel topPanel = new JPanel(new BorderLayout());

            searchClientsButton = new JButton("Search Clients");

            searchClientsButton.addActionListener(e -> searchClients());
            searchClientsButton.setPreferredSize(new Dimension(150, 30));

            JLabel ethernetIpLabel = new JLabel("Ethernet IP: " + ethernetIp);
            JLabel wlanIpLabel = new JLabel("WLAN IP: " + wlanIp);

            ethernetIpLabel.setPreferredSize(new Dimension(180, 30));
            wlanIpLabel.setPreferredSize(new Dimension(180, 30)); // Set the IP address dynamically as needed
            JPanel ipPanel = new JPanel(new FlowLayout());
            ipPanel.add(ethernetIpLabel);
            ipPanel.add(wlanIpLabel);

            JPanel searchButtonPanel = new JPanel(new BorderLayout());
            searchButtonPanel.add(searchClientsButton, BorderLayout.WEST);
            searchButtonPanel.add(ipPanel, BorderLayout.EAST);
            topPanel.add(searchButtonPanel, BorderLayout.NORTH);

            JLabel recentClientsLabel = new JLabel("Recent clients");
            clientListModel = new DefaultListModel<>();
            clientList = new JList<>(clientListModel);
            JScrollPane clientScrollPane = new JScrollPane(clientList);

            topPanel.add(recentClientsLabel, BorderLayout.CENTER);
            topPanel.add(clientScrollPane, BorderLayout.SOUTH);

            // Panel that will hold everything
            JPanel filePanel = new JPanel(new BorderLayout());

            // Button to select files
            selectFilesButton = new JButton("Select files");
            selectFilesButton.setPreferredSize(new Dimension(150, 30)); // Set preferred size
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Alignment can be LEFT or CENTER
            buttonPanel.add(selectFilesButton);
            filePanel.add(buttonPanel, BorderLayout.NORTH);

            // Listener for button
            selectFilesButton.addActionListener(e -> selectFiles());

            sendButton = new JButton("Send");
            sendButton.addActionListener(e -> sendFiles());
            sendButton.setPreferredSize(new Dimension(150, 30));
            JPanel sendButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            sendButtonPanel.add(sendButton);
            filePanel.add(sendButtonPanel, BorderLayout.SOUTH);

            // List to display files
            fileListModel = new DefaultListModel<>();
            fileList = new JList<>(fileListModel);
            JScrollPane fileScrollPane = new JScrollPane(fileList);
            filePanel.add(fileScrollPane, BorderLayout.CENTER);

            add(topPanel, BorderLayout.NORTH);
            add(filePanel, BorderLayout.CENTER);
            // add(sendButton, BorderLayout.SOUTH);

            setLocationRelativeTo(null);

            setVisible(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FTPServerUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(FTPServerUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FTPServerUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(FTPServerUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void searchClients() {
        try {
            ServerThread s = new ServerThread(serverSocket);
            clients.add(s);
            int lastClient = clients.size();
            clientListModel.addElement("Client " + lastClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectFiles() {
        filePaths.removeAll(filePaths);
        fileListModel.removeAllElements();
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                fileListModel.addElement(file.getName());
                filePaths.add(file.getAbsolutePath());
            }
        }
        System.out.println(filePaths.size());
    }

    private void sendFiles() {
        try {
            for(int i=0; i<clients.size(); i++){
                Thread t = new Thread(new FileSender(filePaths, ((ServerThread)clients.get(i))));
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FTPServerUI::new);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

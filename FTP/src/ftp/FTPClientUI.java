package ftp;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FTPClientUI extends JFrame {
    private JLabel connectionStatus;
    private JTextField folderPathField;
    private JButton destinationButton;
    private DefaultListModel<String> fileListModel;
    private String folderPath;
    public static final int port = 7777;
    private static Socket socket = null;
    public static InetAddress ipAddress = null;

    public FTPClientUI() {
        super("FTP Client");
        createUI();
    }
    
    public static void main(String[] args) throws UnknownHostException {
        SwingUtilities.invokeLater(FTPClientUI::new);
//        ipAddress = InetAddress.getLocalHost();
//        ipAddress = InetAddress.getByName("192.168.1.6");
    }

    private void createUI() {
        try {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 400);
            setLayout(new BorderLayout(5, 5));
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

            // Connection Status Label
            connectionStatus = new JLabel("Not connected");
            connectionStatus.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center align the label

            // Horizontal Panel for IP Field and Connect Button
            JPanel connectPanel = new JPanel();
            connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.X_AXIS));

            // Server IP Text Field
            JTextField serverIPField = new JTextField(10);  // Adjust size as needed
            serverIPField.setMaximumSize(new Dimension(Integer.MAX_VALUE, serverIPField.getPreferredSize().height));
            //==============================================================================================
            String serverIp = serverIPField.getText();
            // Connect Button
            JButton connectButton = new JButton("Connect");
            connectButton.addActionListener(e -> {
                try {
                    ipAddress = InetAddress.getByName(serverIPField.getText());
                    connectToServer();
                } catch (UnknownHostException ex) {
                    Logger.getLogger(FTPClientUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            //=====================================================================================================



            // Add components to connect panel
            connectPanel.add(connectButton);
            connectPanel.add(Box.createHorizontalStrut(5));  // Space between the text field and the button
            connectPanel.add(serverIPField);

            // Add components to the panel
            topPanel.add(Box.createVerticalGlue());  // Add space above or stretch
            topPanel.add(connectionStatus);
            topPanel.add(Box.createVerticalStrut(10));  // Space between the label and the button
            topPanel.add(connectPanel);
            topPanel.add(Box.createVerticalGlue());

            JPanel destinationPanel = new JPanel();
            destinationPanel.setLayout(new BorderLayout());
            JButton destinationButton = new JButton("Select Destination");
            destinationButton.setPreferredSize(new Dimension(150, 30));

            JButton acceptButton = new JButton("Accept");
            acceptButton.setPreferredSize(new Dimension(100, 30));

            // Create panel for buttons with FlowLayout
            JPanel buttonsPanel = new JPanel(new BorderLayout());
            buttonsPanel.add(destinationButton, BorderLayout.WEST);
            buttonsPanel.add(acceptButton, BorderLayout.EAST);

            // Adding the buttons panel to the main panel
            destinationPanel.add(buttonsPanel, BorderLayout.NORTH);

            // Adding the main panel to the frame's content pane
            getContentPane().add(destinationPanel, BorderLayout.CENTER);

            folderPathField = new JTextField("No destination selected");
            folderPathField.setEditable(false);
            destinationButton.addActionListener(e -> chooseDestination());
            destinationPanel.add(folderPathField, BorderLayout.CENTER);

            acceptButton.addActionListener(e -> saveFile());

            fileListModel = new DefaultListModel<>();
            JList<String> fileList = new JList<>(fileListModel);
            fileList.setCellRenderer(new FileListCellRenderer());
            JScrollPane listScrollPane = new JScrollPane(fileList);

            JButton openFolderButton = new JButton("Open Folder");
            openFolderButton.addActionListener(e -> openFolder());
            openFolderButton.setPreferredSize(new Dimension(150, 30));
            JPanel openButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            openButtonPanel.add(openFolderButton);

            JPanel northPanel = new JPanel();
            northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
            northPanel.add(topPanel);
            northPanel.add(destinationPanel);

            // add(topPanel, BorderLayout.NORTH);
            // add(destinationPanel, BorderLayout.NORTH);
            add(northPanel, BorderLayout.NORTH);
            add(listScrollPane, BorderLayout.CENTER);
            add(openButtonPanel, BorderLayout.SOUTH);

            setLocationRelativeTo(null);

            setVisible(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            Logger.getLogger(FTPClientUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void connectToServer() {

        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket(ipAddress, port);
                if(socket.isConnected()){
                    connectionStatus.setText("Connected to the server");
                }else{
                    connectionStatus.setText("No server detected");
                }
            } catch (IOException e) {
                connectionStatus.setText("No server detected");
            }
        } else {
            System.out.println("Already connected to server.");
        }
    }

    private void chooseDestination() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            folderPath = file.getAbsolutePath();
            folderPathField.setText(folderPath);
        }
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(new File(folderPath));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to open folder.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    

    private void saveFile() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            int size = objectInputStream.readInt();
            System.out.println("Receiving " + size + " files");
            for(int i=0; i<size; i++){
                if (socket != null && !socket.isClosed()) {
                    receiveFile(folderPath);
                } else if(socket == null) {
                    System.out.println("Not connected to server. Please connect first.");
                }else if(socket.isClosed()){
                    socket = new Socket(ipAddress, port);
                    receiveFile(folderPath);
                }
            }
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void receiveFile(String folderPath){
        try {
            InputStream inputStream = socket.getInputStream();

            // Read the filename length
            int fileNameLength = inputStream.read();

            // Read the filename bytes
            byte[] fileNameBytes = new byte[fileNameLength];
            inputStream.read(fileNameBytes);
            String fileName = new String(fileNameBytes);
            fileListModel.addElement(fileName);

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static class FileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(value.toString());
            panel.add(label, BorderLayout.CENTER);
            if (isSelected) {
                panel.setBackground(list.getSelectionBackground());
                label.setBackground(list.getSelectionBackground());
            } else {
                panel.setBackground(list.getBackground());
                label.setBackground(list.getBackground());
            }
            return panel;
        }


    }
}



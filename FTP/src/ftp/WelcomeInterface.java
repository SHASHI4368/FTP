package ftp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeInterface extends JFrame {
    public WelcomeInterface() {
        super("Welcome");
        createUI();
    }

    private void createUI() {
        try {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 300); // Adjusted size
            setLayout(new BorderLayout(5, 5));
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setLocationRelativeTo(null);

            // Set up the container with BoxLayout
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20)); // Add padding

            // Server Button
            JButton serverButton = new JButton("Server");
            serverButton.setFont(serverButton.getFont().deriveFont(20.0f)); // Adjusted font size
            serverButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
            serverButton.setMaximumSize(new Dimension(200, 50)); // Adjusted button size
            serverButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openServerUI();
                }
            });

            // Client Button
            JButton clientButton = new JButton("Client");
            clientButton.setFont(clientButton.getFont().deriveFont(20.0f)); // Adjusted font size
            clientButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
            clientButton.setMaximumSize(new Dimension(200, 50)); // Adjusted button size
            clientButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openClientUI();
                }
            });

            // Add buttons to the button panel
            buttonPanel.add(serverButton);
            buttonPanel.add(Box.createVerticalStrut(10)); // Add some vertical space between buttons
            buttonPanel.add(clientButton);

            // Add button panel to the center of the frame
            add(buttonPanel, BorderLayout.CENTER);

            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void openServerUI() {
        // Placeholder for opening the server interface
        FTPServerUI serverUI = new FTPServerUI(); // Adjust according to your server UI class
        this.setVisible(false); // Hides the welcome window
        serverUI.setVisible(true);
    }

    private void openClientUI() {
        // Placeholder for opening the client interface
        FTPClientUI clientUI = new FTPClientUI(); // Adjust according to your client UI class
        this.setVisible(false); // Hides the welcome window
        clientUI.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomeInterface::new);
    }
}


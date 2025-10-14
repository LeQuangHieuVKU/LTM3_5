
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.*;
import java.io.*;

public class MailClient {
    private static final int PORT = 12345;
    private static JFrame frame;
    private static JTextArea outputArea;
    private static JTextField serverField;
    private static JTextField userField;
    private static JTextField passwordField;
    private static JTextField recipientField;
    private static JTextArea contentArea;
    private static JList<String> emailList;
    private static DefaultListModel<String> emailListModel;

    public static void main(String[] args) {
        // Create GUI
        frame = new JFrame("Mail Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Input panel (Settings and Compose Email)
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(new Color(240, 240, 240));

        // Settings panel (Server IP, Username, Password)
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "Settings", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(50, 50, 50)));
        settingsPanel.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel serverLabel = new JLabel("Server IP:");
        serverLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        serverField = new JTextField("localhost", 15);
        serverField.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userField = new JTextField(15);
        userField.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField = new JTextField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));

        gbc.gridx = 0;
        gbc.gridy = 0;
        settingsPanel.add(serverLabel, gbc);
        gbc.gridx = 1;
        settingsPanel.add(serverField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        settingsPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        settingsPanel.add(userField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        settingsPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        settingsPanel.add(passwordField, gbc);

        // Email composition panel (Recipient, Content)
        JPanel emailPanel = new JPanel(new GridBagLayout());
        emailPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "Compose Email", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(50, 50, 50)));
        emailPanel.setBackground(new Color(255, 255, 255));

        JLabel recipientLabel = new JLabel("Recipient:");
        recipientLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        recipientField = new JTextField(15);
        recipientField.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel contentLabel = new JLabel("Email Content:");
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        contentArea = new JTextArea(5, 20);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 12));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);

        gbc.gridx = 0;
        gbc.gridy = 0;
        emailPanel.add(recipientLabel, gbc);
        gbc.gridx = 1;
        emailPanel.add(recipientField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        emailPanel.add(contentLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        emailPanel.add(contentScroll, gbc);

        inputPanel.add(settingsPanel, BorderLayout.NORTH);
        inputPanel.add(emailPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Split pane for email list and output
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setBackground(new Color(240, 240, 240));

        // Email list panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "Email List", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(50, 50, 50)));
        listPanel.setBackground(new Color(255, 255, 255));
        emailListModel = new DefaultListModel<>();
        emailList = new JList<>(emailListModel);
        emailList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane listScroll = new JScrollPane(emailList);
        listPanel.add(listScroll, BorderLayout.CENTER);
        splitPane.setLeftComponent(listPanel);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        outputArea.setBackground(new Color(245, 245, 245));
        outputArea.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        JScrollPane outputScroll = new JScrollPane(outputArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "Output Log", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(50, 50, 50)));
        splitPane.setRightComponent(outputScroll);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(240, 240, 240));
        JButton createButton = new JButton("Create Account");
        JButton sendButton = new JButton("Send Email");
        JButton loginButton = new JButton("View Emails");
        JButton viewContentButton = new JButton("View Email Content");
        JButton clearButton = new JButton("Clear Form");
        styleButton(createButton);
        styleButton(sendButton);
        styleButton(loginButton);
        styleButton(viewContentButton);
        styleButton(clearButton);
        buttonPanel.add(createButton);
        buttonPanel.add(sendButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(viewContentButton);
        buttonPanel.add(clearButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Button actions
        createButton.addActionListener(e -> {
            String user = userField.getText();
            String password = passwordField.getText();
            if (user.isEmpty() || password.isEmpty()) {
                appendOutput("ERROR|Username and password cannot be empty", false);
                return;
            }
            String request = "CREATE|" + user + "|" + password;
            String response = sendRequest(request);
            appendOutput(response, response.startsWith("OK"));
            if (response.startsWith("OK")) {
                userField.setText("");
                passwordField.setText("");
            }
        });

        sendButton.addActionListener(e -> {
            String recipient = recipientField.getText();
            String content = contentArea.getText();
            if (recipient.isEmpty() || content.isEmpty()) {
                appendOutput("ERROR|Recipient and content cannot be empty", false);
                return;
            }
            String request = "SEND|" + recipient + "|" + content;
            String response = sendRequest(request);
            appendOutput(response, response.startsWith("OK"));
            if (response.startsWith("OK")) {
                recipientField.setText("");
                contentArea.setText("");
            }
        });

        loginButton.addActionListener(e -> {
            String user = userField.getText();
            if (user.isEmpty()) {
                appendOutput("ERROR|Username cannot be empty", false);
                return;
            }
            String request = "LOGIN|" + user;
            String response = sendRequest(request);
            appendOutput(response, response.startsWith("OK"));
            String[] parts = response.split("\\|", -1);
            emailListModel.clear();
            if (parts[0].equals("OK") && parts.length > 1) {
                appendOutput("Email files:", true);
                for (int i = 1; i < parts.length; i++) {
                    if (!parts[i].isEmpty()) {
                        appendOutput("- " + parts[i], true);
                        emailListModel.addElement(parts[i]);
                    }
                }
            }
        });

        viewContentButton.addActionListener(e -> {
            String user = userField.getText();
            String selectedFile = emailList.getSelectedValue();
            if (user.isEmpty()) {
                appendOutput("ERROR|Username cannot be empty", false);
                return;
            }
            if (selectedFile == null) {
                appendOutput("ERROR|Please select an email from the list", false);
                return;
            }
            String request = "GET_EMAIL|" + user + "|" + selectedFile;
            String response = sendRequest(request);
            String[] parts = response.split("\\|", -1);
            if (parts[0].equals("OK") && parts.length > 1) {
                String content = parts[1].replace("\\|", "|"); // Unescape |
                showEmailContent(selectedFile, content);
                appendOutput("Displayed content for " + selectedFile, true);
            } else {
                appendOutput(response, false);
            }
        });

        clearButton.addActionListener(e -> {
            userField.setText("");
            passwordField.setText("");
            recipientField.setText("");
            contentArea.setText("");
            outputArea.setText("");
            emailListModel.clear();
        });

        frame.setVisible(true);
    }

    private static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(100, 150, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(120, 170, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 150, 200));
            }
        });
    }

    private static void appendOutput(String message, boolean isSuccess) {
        if (isSuccess) {
            outputArea.append("[SUCCESS] " + message + "\n");
            outputArea.setForeground(new Color(0, 150, 0));
        } else {
            outputArea.append("[ERROR] " + message + "\n");
            outputArea.setForeground(new Color(200, 0, 0));
        }
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private static void showEmailContent(String filename, String content) {
        JDialog dialog = new JDialog(frame, "Email: " + filename, true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(frame);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(contentScroll, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private static String sendRequest(String request) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] sendData = request.getBytes("UTF-8");
            InetAddress address = InetAddress.getByName(serverField.getText());
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);
            socket.send(sendPacket);

            byte[] receiveData = new byte[65535];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.setSoTimeout(5000);
            socket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
        } catch (Exception e) {
            return "ERROR|" + e.getMessage();
        }
    }
}

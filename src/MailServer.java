
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MailServer {
    private static final int PORT = 12345;
    private static final String MAILBOX_DIR = "mailboxes";

    public static void main(String[] args) {
        // Create mailboxes directory if it doesn't exist
        File dir = new File(MAILBOX_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Mail Server started on port " + PORT);
            byte[] buffer = new byte[65535];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Process request in a new thread
                new Thread(() -> handleRequest(socket, packet)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(DatagramSocket socket, DatagramPacket packet) {
        try {
            String request = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
            String[] parts = request.split("\\|", -1); // Split by |, allow empty fields
            String command = parts[0];
            String response;

            if (command.equals("CREATE")) {
                if (parts.length < 3) {
                    response = "ERROR|Missing user or password";
                } else {
                    String user = parts[1];
                    String password = parts[2];
                    File userDir = new File(MAILBOX_DIR, user);

                    if (userDir.exists()) {
                        response = "ERROR|User already exists";
                    } else {
                        userDir.mkdirs();
                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String content = user + "+" + password + "+" + date + "+Thank you for using this service. we hope that you will feel comfortable.";
                        try (FileWriter writer = new FileWriter(new File(userDir, "new_email.txt"))) {
                            writer.write(content);
                        }
                        response = "OK|Account created";
                    }
                }
            } else if (command.equals("SEND")) {
                if (parts.length < 3) {
                    response = "ERROR|Missing recipient or content";
                } else {
                    String recipient = parts[1];
                    String content = parts[2];
                    File recipientDir = new File(MAILBOX_DIR, recipient);

                    if (!recipientDir.exists()) {
                        response = "ERROR|Recipient does not exist";
                    } else {
                        String filename = "email_" + System.currentTimeMillis() + ".txt";
                        try (FileWriter writer = new FileWriter(new File(recipientDir, filename))) {
                            writer.write(content);
                        }
                        response = "OK|Email sent";
                    }
                }
            } else if (command.equals("LOGIN")) {
                if (parts.length < 2) {
                    response = "ERROR|Missing user";
                } else {
                    String user = parts[1];
                    File userDir = new File(MAILBOX_DIR, user);

                    if (!userDir.exists()) {
                        response = "ERROR|User does not exist";
                    } else {
                        String[] files = userDir.list();
                        if (files == null || files.length == 0) {
                            response = "OK|No emails";
                        } else {
                            response = "OK|" + String.join("|", files);
                        }
                    }
                }
            } else if (command.equals("GET_EMAIL")) {
                if (parts.length < 3) {
                    response = "ERROR|Missing user or filename";
                } else {
                    String user = parts[1];
                    String filename = parts[2];
                    File emailFile = new File(MAILBOX_DIR, user + "/" + filename);

                    if (!emailFile.exists()) {
                        response = "ERROR|Email file does not exist";
                    } else {
                        StringBuilder content = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new FileReader(emailFile))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                content.append(line).append("\n");
                            }
                        }
                        response = "OK|" + content.toString().replace("|", "\\|"); // Escape | in content
                    }
                }
            } else {
                response = "ERROR|Unknown command";
            }

            // Send response
            byte[] responseBytes = response.getBytes("UTF-8");
            DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
            socket.send(responsePacket);
        } catch (Exception e) {
            try {
                String response = "ERROR|" + e.getMessage();
                byte[] responseBytes = response.getBytes("UTF-8");
                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                socket.send(responsePacket);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

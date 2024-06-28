import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Rent {

    private JFrame frame;
    private JTextArea textArea;
    private JTextField txtID;
    private JTextField txtItemName;
    private JTextField txtStatus;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private static final String SERVER_IP = "10.200.64.222";
    private static final int SERVER_PORT = 8081;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Rent window = new Rent();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Rent() {
        initialize();
//        connectToServer();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 631, 453);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 69, 574, 209);
        frame.getContentPane().add(scrollPane);

        JButton btnGet = new JButton("Get Item Data From Table");
        btnGet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchDataFromTable();
            }
        });
        btnGet.setBounds(199, 10, 217, 30);
        frame.getContentPane().add(btnGet);

        JLabel lblNewLabel = new JLabel("Data From Table :");
        lblNewLabel.setBounds(10, 50, 200, 14);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_Update = new JLabel("Update Item Status");
        lblNewLabel_Update.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel_Update.setBounds(90, 290, 140, 14);
        frame.getContentPane().add(lblNewLabel_Update);

        JLabel lblNewLabel_1 = new JLabel("ID :");
        lblNewLabel_1.setBounds(31, 326, 100, 14);
        frame.getContentPane().add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Item Name :");
        lblNewLabel_2.setBounds(31, 360, 80, 14);
        frame.getContentPane().add(lblNewLabel_2);

        JLabel lblNewLabel_3 = new JLabel("Status :");
        lblNewLabel_3.setBounds(31, 389, 80, 14);
        frame.getContentPane().add(lblNewLabel_3);

        txtID = new JTextField();
        txtID.setBounds(120, 326, 120, 19);
        frame.getContentPane().add(txtID);
        txtID.setColumns(10);

        txtItemName = new JTextField();
        txtItemName.setColumns(10);
        txtItemName.setBounds(120, 358, 120, 19);
        frame.getContentPane().add(txtItemName);

        txtStatus = new JTextField();
        txtStatus.setColumns(10);
        txtStatus.setBounds(120, 387, 120, 19);
        frame.getContentPane().add(txtStatus);
        
        txtItemName.setEditable(false);
        txtStatus.setEditable(false);

        JButton btnSearch = new JButton("Search Item");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchItemById();
            }
        });
        btnSearch.setBounds(250, 326, 150, 19);
        frame.getContentPane().add(btnSearch);

        JButton btnSave = new JButton("Save to Database");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveStatus();
            }
        });
        btnSave.setBounds(250, 386, 150, 19);
        frame.getContentPane().add(btnSave);
    }

//    private void connectToServer() {
//        try {
//            socket = new Socket(SERVER_IP, SERVER_PORT);
//            out = new DataOutputStream(socket.getOutputStream());
//            in = new DataInputStream(socket.getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(frame, "Unable to connect to the server.");
//        }
//    }

    private void fetchDataFromTable() {
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                out.writeUTF("GET_DATA");
                out.flush();

                String response = in.readUTF();
                displayData(response);
         
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error fetching data from the server.");
            }
        }).start();
    }

    private void searchItemById() {
        String idToSearch = txtID.getText().trim(); // Trim to remove any leading/trailing whitespace

        System.out.println("Searching for ID: " + idToSearch); // Debug statement

        String[] lines = textArea.getText().split("\n");

        AtomicReference<String> itemNameRef = new AtomicReference<>();
        AtomicReference<String> statusRef = new AtomicReference<>();

        for (String line : lines) {
            System.out.println("Line: " + line); // Debug statement

            // Assuming each line has the format "ID: X Name: Y, Status: Z"
            String[] parts = line.split("\\s+");

            if (parts.length >= 5 && parts[0].equals("ID:") && parts[1].equals(idToSearch)) {
                // Extract item name and status correctly based on their positions
                String itemName = parts[3]; // Assuming "Name: Y," extracts Y
                String status = parts[6];   // Assuming "Status: Z" extracts Z
                System.out.println("status=" + status); // Debug statement
                
                // Remove comma from item name if present
                if (itemName.endsWith(",")) {
                    itemName = itemName.substring(0, itemName.length() - 1);
                }

                System.out.println("Found: itemName=" + itemName + ", status=" + status); // Debug statement

                itemNameRef.set(itemName.trim());
                statusRef.set(status.trim());

                break; // Exit the loop if item is found
            }
        }
        txtStatus.setEditable(true);

        // Update UI using EventQueue.invokeLater()
        EventQueue.invokeLater(() -> {
            txtItemName.setText(itemNameRef.get()); // Accessing mutable variable
            txtStatus.setText(statusRef.get()); // Accessing mutable variable
        });

        if (itemNameRef.get() == null || statusRef.get() == null) {
            // If the ID was not found, show an error message
            JOptionPane.showMessageDialog(frame, "Item with ID " + idToSearch + " not found.");
        }
    }

    private void saveStatus() {
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                String id = txtID.getText().trim();
                String status = txtStatus.getText().trim();

                out.writeUTF("SAVE_STATUS");
                out.writeUTF(id);
                out.writeUTF(status);
                out.flush();

                String response = in.readUTF();

                EventQueue.invokeLater(() -> {
                    if (response.equals("SUCCESS")) {
                        JOptionPane.showMessageDialog(frame, "Status saved successfully.");
                        txtID.setText("");
                        txtItemName.setText(""); // Accessing mutable variable
                        txtStatus.setText(""); // Accessing mutable variable
                        txtStatus.setEditable(false);
                        fetchDataFromTable();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to save status.");
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving the status.");
            }
        }).start();
    }

    private void displayData(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String name = obj.getString("name");
                String status = obj.getString("status");
                sb.append("ID: ").append(id).append(" Name: ").append(name).append(", Status: ").append(status).append("\n");
            }
            EventQueue.invokeLater(() -> textArea.setText(sb.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error parsing server response.");
        }
    }
}
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class addInventory {
    private JFrame frame;
    private JTextField nameField;
    private JTextField typeField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new addInventory().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Multimedia Inventory Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);
        frame.getContentPane().setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 48, 100, 25);

        nameField = new JTextField();
        nameField.setBounds(120, 48, 272, 25);

        JLabel typeLabel = new JLabel("Type: ");
        typeLabel.setBounds(10, 83, 100, 25);

        typeField = new JTextField();
        typeField.setBounds(120, 83, 272, 25);

        JButton saveToDatabaseButton = new JButton("Add To Database");
        saveToDatabaseButton.setBounds(120, 118, 350, 25);

        saveToDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String type = typeField.getText();
                saveToDatabase(name, type);
            }
        });

        frame.getContentPane().add(nameLabel);
        frame.getContentPane().add(nameField);
        frame.getContentPane().add(typeLabel);
        frame.getContentPane().add(typeField);
        frame.getContentPane().add(saveToDatabaseButton);
        
        JLabel lblNewLabel = new JLabel("Enter equipment details:");
        lblNewLabel.setBounds(10, 10, 173, 13);
        frame.getContentPane().add(lblNewLabel);

        frame.setVisible(true);
    }

    private void saveToDatabase(String name, String type) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String urlString = "http://10.200.64.222/ProjectDAD/saveDataInventory.php"; // URL of your PHP script
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    // Construct data to be sent
                    String data = "name=" + name + "&type=" + type;

                    OutputStream os = connection.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Data saved successfully
                        JOptionPane.showMessageDialog(frame, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Error in saving data
                        JOptionPane.showMessageDialog(frame, "Failed to save data. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }
        };

        worker.execute();
    }
}

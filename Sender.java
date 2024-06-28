import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Sender {
    private static final int PORT = 8081;
    private static final String PHP_SERVER_URL = "http://10.200.64.222/ProjectDAD/DbInsert.php"; // Adjust the URL to your PHP file location

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                String command = in.readUTF();
                Map<String, String> params = new HashMap<>();

                switch (command) {
                    case "GET_DATA":
                        params.put("action", "get_data");
                        break;

                    case "SAVE_STATUS":
                        String saveId = in.readUTF();
                        System.out.println("saveiD: "+saveId);
                        String status = in.readUTF();
                        System.out.println("stat: "+status);
                        params.put("action", "save");
                        params.put("id", saveId);
                        params.put("status", status);
                        String response = sendPostRequest(PHP_SERVER_URL, params);
                        
                        if  (response != null ) {
                        	out.writeUTF("SUCCESS");
                        }
                     
                        break;
                    default:
                        out.writeUTF("Unknown command");
                        return;
                }

                String response = sendPostRequest(PHP_SERVER_URL, params);
                if (response == null) {
                    response = "{\"error\": \"Received null response from server\"}";
                }
                out.writeUTF(response);

            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        private String sendPostRequest(String url, Map<String, String> params) {
            try {
                HttpClient client = HttpClient.newHttpClient();
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
                    postData.append('=');
                    postData.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.ofString(postData.toString()))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
package apg;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class APGServer{

    private HttpServer server;
    public APGServer(int port) throws IOException {
        start(port);
    }

    public void start(int port) throws IOException {
        // Create an HTTP server on port 80
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Create a context for the /weather path and set the handler
        server.createContext("/apg", new APGServerHandler());

        // Start the server
        server.start();

        System.out.println("Server is running on port " + port);
    }

    public void stop(){
        server.stop(0);
    }

    static class APGServerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if (exchange.getRequestMethod().equals("POST")){
                String jsonData = "";

                // Convert exchange request body to string
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                    // Using Java 8 Streams to read lines from the BufferedReader and concatenate them into a single String
                    jsonData = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
                }

                // Handle exchange request body as json
                try (JsonReader jsonReader = Json.createReader(new StringReader(jsonData))) {
                    JsonObject jsonObject = jsonReader.readObject();

                    // Accessing values from JsonObject
                    String userIP = jsonObject.getString("UserIP");
                    int team = jsonObject.getInt("Team");
                    String displayName = jsonObject.getString("DisplayName");

                    APGPlayer player = APGPlayer.getOrCreatePlayer(userIP, team, displayName);

                    int addInitHP = jsonObject.getInt("AddInitHP");
                    player.addHPBoost(addInitHP);


                }
            }

            // Send response

            // Process the request
            String response = "{\"Response\": \"Success\"}";

            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());

            // Write the response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}

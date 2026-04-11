import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;

public class app {

    static String message = "No message yet";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // HOME PAGE
        server.createContext("/", exchange -> {

            String response =
                    "<html>" +
                    "<body style='text-align:center;font-family:Arial'>" +
                    "<h2>Chat App - V1</h2>" +

                    "<p>Message: " + message + "</p>" +

                    "<form method='POST' action='/send'>" +
                    "<input name='msg' placeholder='Enter message'/>" +
                    "<br><br>" +
                    "<button>Send</button>" +
                    "</form>" +

                    "</body></html>";

            exchange.getResponseHeaders().add("Content-Type", "text/html");

            byte[] bytes = response.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        });

        // SEND MESSAGE
        server.createContext("/send", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String input = br.readLine();

                if (input != null && input.contains("=")) {
                    message = input.split("=")[1].replace("+", " ");
                }

                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
            }
        });

        server.start();
        System.out.println("Server running on port 8080...");
    }
}

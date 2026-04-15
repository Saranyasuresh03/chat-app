import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
public class App {
static List<String> tickets = new ArrayList<>();
public static void main(String[] args) throws Exception {
HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
// VIEW TICKETS PAGE
server.createContext("/tickets", exchange -> {
StringBuilder list = new StringBuilder();
for (String t : tickets) {
list.append("<div style='background:white;color:black;padding:10px;margin:10px;border-radius:5px;'>")
.append(t)
.append("</div>");
}
String response =
"<html><body style='margin:0;font-family:Arial;background:#0f172a;color:white;" +
"display:flex;justify-content:center;align-items:center;height:100vh;'>" +
"<div style='width:450px;background:#1e293b;padding:25px;border-radius:12px;text-align:center;'>" +
"<h2>Help Desk - View Tickets</h2>" +
list.toString() +
"<form method='POST' action='/add'>" +
"<input name='ticket' placeholder='Enter Issue' style='padding:10px;width:90%;'/><br><br>" +
"<button>Add Ticket</button>" +
"</form>" +
"</div></body></html>";
exchange.getResponseHeaders().add("Content-Type", "text/html");
exchange.sendResponseHeaders(200, response.getBytes().length);
OutputStream os = exchange.getResponseBody();
os.write(response.getBytes());
os.close();
});
// ADD TICKET
server.createContext("/add", exchange -> {
BufferedReader br = new BufferedReader(
new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
String data = br.readLine();
if (data != null) {
String value = URLDecoder.decode(data.split("=")[1], "UTF-8");
tickets.add(value);
}
exchange.getResponseHeaders().add("Location", "/tickets");
exchange.sendResponseHeaders(302, -1);
});
server.start();
System.out.println("V2 running on port 8082...");
}
}

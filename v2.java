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
// HOME PAGE (OPTIONS)
server.createContext("/", exchange -> {
String response =
"<html>" +
"<head>" +
"<style>" +
"body {margin:0;font-family:Arial;background:#0f172a;color:white;" +
"display:flex;justify-content:center;align-items:center;height:100vh;}" +
".container {width:400px;background:#1e293b;padding:30px;border-radius:12px;" +
"text-align:center;box-shadow:0 4px 20px rgba(0,0,0,0.5);}" +
"h2 {font-size:28px;margin-bottom:20px;}" +
"button {width:80%;padding:12px;font-size:18px;margin:10px;" +
"border:none;border-radius:6px;cursor:pointer;}" +
".add {background:#22c55e;color:white;}" +
".view {background:#3b82f6;color:white;}" +
"</style>" +
"</head>" +
"<body>" +
"<div class='container'>" +
"<h2>Help Desk - V2</h2>" +
"<form action='/addPage'>" +
"<button class='add'>Add Ticket</button>" +
"</form>" +
"<form action='/tickets'>" +
"<button class='view'>View Tickets</button>" +
"</form>" +
"</div>" +
"</body>" +
"</html>";
exchange.getResponseHeaders().add("Content-Type", "text/html");
exchange.sendResponseHeaders(200, response.getBytes().length);
OutputStream os = exchange.getResponseBody();
os.write(response.getBytes());
os.close();
});
// ADD PAGE
server.createContext("/addPage", exchange -> {
String response =
"<html><body style='margin:0;font-family:Arial;background:#0f172a;color:white;" +
"display:flex;justify-content:center;align-items:center;height:100vh;'>" +
"<div style='background:#1e293b;padding:30px;border-radius:12px;text-align:center;'>" +
"<h2>Add Ticket</h2>" +
"<form method='POST' action='/add'>" +
"<input name='ticket' placeholder='Enter Issue' " +
"style='padding:12px;width:250px;font-size:16px;'/><br><br>" +
"<button style='background:#22c55e;color:white;padding:10px;'>Submit</button>" +
"</form>" +
"<br><a href='/'>Back</a>" +
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
// VIEW TICKETS
server.createContext("/tickets", exchange -> {
StringBuilder list = new StringBuilder();
for (String t : tickets) {
list.append("<div style='background:white;color:black;padding:12px;margin:10px;border-radius:6px;font-size:18px;'>")
.append(t)
.append("</div>");
}
String response =
"<html><body style='margin:0;font-family:Arial;background:#0f172a;color:white;" +
"display:flex;justify-content:center;align-items:center;height:100vh;'>" +
"<div style='width:400px;background:#1e293b;padding:25px;border-radius:12px;text-align:center;'>" +
"<h2>All Tickets</h2>" +
list.toString() +
"<br><a href='/' style='color:#22c55e;'>Back</a>" +
"</div></body></html>";
exchange.getResponseHeaders().add("Content-Type", "text/html");
exchange.sendResponseHeaders(200, response.getBytes().length)
OutputStream os = exchange.getResponseBody();
os.write(response.getBytes());
os.close();
});
server.start();
System.out.println("V2 running on port 8082...");
}
}


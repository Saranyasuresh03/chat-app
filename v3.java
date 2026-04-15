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
HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);
// HOME PAGE
server.createContext("/", exchange -> {
String response =
"<html><body style='margin:0;font-family:Arial;background:#0f172a;color:white;" +
"display:flex;justify-content:center;align-items:center;height:100vh;'>" +
"<div style='width:420px;background:#1e293b;padding:30px;border-radius:12px;text-align:center;'>" +
"<h2>Help Desk - V3</h2>" +
"<form action='/addPage'><button style='width:80%;padding:12px;font-size:18px;background:#22c55e;color:white;'>Add Ticket</button></form>" +
"<form action='/tickets'><button style='width:80%;padding:12px;font-size:18px;background:#3b82f6;color:white;margin-top:10px;'>Manage Tickets</button></form>" +
"</div></body></html>";
exchange.getResponseHeaders().add("Content-Type", "text/html");
exchange.sendResponseHeaders(200, response.getBytes().length);
exchange.getResponseBody().write(response.getBytes());
exchange.getResponseBody().close();
});
// ADD PAGE
server.createContext("/addPage", exchange -> {
String response =
"<html><body style='display:flex;justify-content:center;align-items:center;height:100vh;background:#0f172a;color:white;font-family:Arial;'>" +
"<div style='background:#1e293b;padding:30px;border-radius:12px;text-align:center;'>" +
"<h2>Add Ticket</h2>" +
"<form method='POST' action='/add'>" +
"<input name='ticket' placeholder='Enter Issue' style='padding:10px;width:250px;'/><br><br>" +
"<button style='background:#22c55e;color:white;'>Submit</button>" +
"</form>" +
"<br><a href='/' style='color:#22c55e;'>Back</a>" +
"</div></body></html>";
exchange.getResponseHeaders().add("Content-Type", "text/html");
exchange.sendResponseHeaders(200, response.getBytes().length);
exchange.getResponseBody().write(response.getBytes());
exchange.getResponseBody().close();
});
// ADD
server.createContext("/add", exchange -> {
BufferedReader br = new BufferedReader(
new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
String data = br.readLine();
if (data != null) {
String value = URLDecoder.decode(data.split("=")[1], "UTF-8");
tickets.add(value + " (OPEN)");
}
exchange.getResponseHeaders().add("Location", "/tickets");
exchange.sendResponseHeaders(302, -1);
});
// VIEW + SOLVE + DELETE
server.createContext("/tickets", exchange -> {
StringBuilder list = new StringBuilder();
for (String t : tickets) {
list.append("<div style='background:white;color:black;padding:12px;margin:10px;border-radius:6px;font-size:18px;'>")
.append(t);
// SOLVE BUTTON
if (!t.contains("RESOLVED")) {
list.append("<form method='POST' action='/solve' style='display:inline;'>")
.append("<input type='hidden' name='ticket' value='" + t + "'/>")
.append("<button style='margin-left:10px;background:green;color:white;'>Solve</button>")
.append("</form>");
}
// DELETE BUTTON
list.append("<form method='POST' action='/delete' style='display:inline;'>")
.append("<input type='hidden' name='ticket' value='" + t + "'/>")
.append("<button style='margin-left:10px;background:red;color:white;'>Delete</button>")
.append("</form>");
list.append("</div>");
}
String response =
"<html><body style='display:flex;justify-content:center;align-items:center;height:100vh;background:#0f172a;color:white;font-family:Arial;'>" +
"<div style='width:420px;background:#1e293b;padding:25px;border-radius:12px;text-align:center;'>" +
"<h2>Manage Tickets</h2>" +
list.toString() +
"<br><a href='/' style='color:#22c55e;'>Back</a>" +
"</div></body></html>";
exchange.getResponseHeaders().add("Content-Type", "text/html");
exchange.sendResponseHeaders(200, response.getBytes().length);
exchange.getResponseBody().write(response.getBytes());
exchange.getResponseBody().close();
});
// SOLVE
server.createContext("/solve", exchange -> {
BufferedReader br = new BufferedReader(
new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
String data = br.readLine();
if (data != null) {
String value = URLDecoder.decode(data.split("=")[1], "UTF-8");
tickets.remove(value);
tickets.add(value.replace("(OPEN)", "(RESOLVED)"));
}
exchange.getResponseHeaders().add("Location", "/tickets");
exchange.sendResponseHeaders(302, -1);
});
// DELETE
server.createContext("/delete", exchange -> {
BufferedReader br = new BufferedReader(
new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
String data = br.readLine();
if (data != null) {
String value = URLDecoder.decode(data.split("=")[1], "UTF-8");
tickets.remove(value);
}
exchange.getResponseHeaders().add("Location", "/tickets");
exchange.sendResponseHeaders(302, -1);
});
server.start();
System.out.println("V3 running on port 8083...");
}
}

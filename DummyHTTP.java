import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class DummyHTTP {
    static final int PORT = 8080;

    static final String STATUS_OK = "HTTP/1.1 200 OK\n";
    static String ROOT_RESPONSE =
        "HTTP/1.1 200 OK\n" +
        "Content-Type: text/html\n" +
        "\n" +
        "<html>\n" +
        "<body>\n" +
        "<p>Welcome Stranger!</p>\n" +
        "</body>\n" +
        "</html>\n";

    static String NOT_FOUND_PAGE =
        "HTTP/1.1 404 NOT FOUND\n" +
        "Content-Type: text/html\n" +
        "\n" +
        "Page not found!";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        while(true){
            String response = "";
            String html;
            String request = "";
            Socket socket = serverSocket.accept();
            System.out.println("Client connected:" + socket.getInetAddress() + " " + socket.getPort());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String line = reader.readLine();
            if(line == null){
                writer.println(NOT_FOUND_PAGE);
                socket.close();
                continue;
            }
            System.out.println("request received:");
            String str = line;
            System.out.println(str);
            while(!str.isEmpty()){
                str = reader.readLine();
                System.out.println(str);
            }
            System.out.println("Received request from client:\n" + request);
            String uri = line.split("\\s+")[1];

            if(uri.equals("/")){
                response = ROOT_RESPONSE;
            }else{
                uri = uri.substring(1);
                try {
                    html = Files.readString(Path.of(uri));
                }catch (IOException e){
                    html = null;
                }
                if(html != null){
                    response += STATUS_OK;
                    response += "Content-Type: text/html\n\n";
                    response += html;
                }else{
                    response += NOT_FOUND_PAGE;
                }
            }
            writer.println(response);
            socket.close();
        }
    }
}

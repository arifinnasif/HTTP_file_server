import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
    static final int PORT = 5097;


    public static void main(String[] args) throws IOException {
        File rootDir = new File("root");
        File logsDir = new File("logs");
        if(!rootDir.exists()) {
            rootDir.mkdir();
        }

        if(!logsDir.exists()) {
            logsDir.mkdir();
        }

        ServerSocket serverConnect = new ServerSocket(PORT);
        System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

        while(true)
        {
            Socket s = serverConnect.accept();
            System.out.println("=======START=======");
            new ServerThread(s);
        }

    }



}

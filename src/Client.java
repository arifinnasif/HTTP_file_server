import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void upload(String filepath, File file) throws IOException {
//        if(!file.exists()) {
//            System.out.println("file does not exist");
//            return;
//        }
        Socket socket = new Socket("127.0.0.1",5097);
        Thread reqThread = new Thread(new ClientUploadReqThread(socket, filepath, file));
//        Thread resThread = new Thread(new ClientUploadResThread(reqThread, socket, filepath, file));
        reqThread.start();
//        resThread.start();
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String line;
        while (true) {
//            System.out.print("> ");
            line = sc.nextLine();
            String[] cmd = line.strip().split(" ");
            if(cmd[0].equalsIgnoreCase("UPLOAD")) {
                for(int i = 1; i < cmd.length; i++) {
                    upload(cmd[i], new File(cmd[i]));
                }
            } else {
                System.out.println("Unknown command");
            }
        }
//        upload("socket.mp4",new File("socket.mp4"));
    }
}

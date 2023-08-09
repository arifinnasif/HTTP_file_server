import java.io.*;
import java.net.Socket;

public class ClientUploadReqThread implements Runnable{
    private String filepath;
    private File file;
    private InputStream inputStream;
    private OutputStream outputStream;

    private Socket socket;
//    public boolean IS_FINISHED = false;
    public ClientUploadReqThread(Socket socket, String filepath, File file) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.filepath = filepath;
        this.file = file;
    }
    @Override
    public void run() {
        PrintWriter pr = new PrintWriter(outputStream);
        pr.write("UPLOAD "+filepath+"\r\n");
        pr.flush();

        if(!file.exists()) {
            pr.write("-1\r\n\r\n");
//            pr.write(null);
            pr.flush();
            try {
                System.out.println(filepath+": File upload error");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        BufferedInputStream file_bis = null;
        byte[] buffer = new byte[Utils.CHUNK_SIZE];
        int buffer_size;
        try {
            pr.write(""+file.length()+"\r\n\r\n");
            pr.flush();

            BufferedReader soc_br = new BufferedReader(new InputStreamReader(inputStream));

            if(soc_br.readLine().equals("invalid")) {
                System.out.println("invalid file");
                return;
            }
            System.out.println(filepath+": upload started");

            file_bis = new BufferedInputStream(new FileInputStream(file), 1);
//            for(long i = 0; i < Math.ceil((double)file.length()/Utils.CHUNK_SIZE); i++) {
            while((buffer_size = file_bis.read(buffer)) != -1) {

                outputStream.write(buffer,0,buffer_size);
                outputStream.flush();
//                System.out.println(new String(buffer));
            }


            System.out.println(filepath+": Successfully Uploaded");

            socket.close();
            return;

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

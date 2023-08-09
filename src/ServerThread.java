import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ServerThread implements Runnable {
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    Thread thread;
    ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
//        InputStreamReader in = new InputStreamReader(inputStream);
        PrintWriter pr = new PrintWriter(outputStream);
        ArrayList<String> request_headers = new ArrayList<>();
        StringBuilder request_header_str = new StringBuilder();
        String content = "";
        Logger logger2 = new Logger();


        try {
            while(true) {
                String temp = in.readLine();
                System.out.println(temp);
                if(temp == null || temp.length() == 0) break;
                request_headers.add(temp);
                request_header_str.append(temp+"\n");

            }
            logger2.log_req(request_header_str.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Request Received : \n");
        System.out.println(request_header_str);

        if (request_headers.size() <= 0) {
            kill();
            return;
        }

        if (request_headers.get(0).startsWith("GET")) {
            String req_uri = request_headers.get(0).split(" ")[1];
            String req_path = req_uri.replaceAll("/+", "/").replaceAll("/$","");
            String req_local_path = ("root/"+req_uri.replace("%20", " ")).replaceAll("/+","/");
            File req_file = new File(req_local_path);
            if(!req_file.exists()) {
                content+="<html><head>\n" +
                        "<title>404 Not Found</title>\n" +
                        "</head><body>\n" +
                        "<h1>Not Found</h1>\n" +
                        "<p>The requested URL was not found on this server.</p>\n" +
                        "</body></html>";

                String response =   "HTTP/1.1 404 Not Found\r\n" +
                                    "Date: " + new Date() + "\r\n" +
                                    "Server: Java HTTP Server: 1.0\r\n" +
                                    "Content-Type: text/html\r\n" +
                                    "Content-Length: " + content.length() + "\r\n" +
                                    "\r\n" +
                                    content;

                pr.write(response);
                pr.flush();
                logger2.log_res(response);

                kill();
                return;
            }
            if(req_file.isDirectory()) {
                content = "<html>\n" +
                        " <head>\n" +
                        "  <title>Index of "+ req_local_path.substring(4) +"</title>\n" +
                        " </head>\n" +
                        " <body>\n" +
                        "<h1>Index of "+ req_local_path.substring(4) +"</h1>\n" +
                        "  <table>";

                File[] filelist = req_file.listFiles();
                content+="<tr><th colspan=\"5\"><hr></th></tr>";

//                if(req_file.getParent()!=null) {
//                    content+="<tr><td valign=\"top\">&nbsp;</td><td><a href=\""+ req_uri + "\"><b><i>"+ "." +"</i></b></a></td></tr>\n";
//                    System.out.println(req_uri);
//                    content+="<tr><td valign=\"top\">&nbsp;</td><td><a href=\""+ req_uri.substring(0, req_uri.lastIndexOf("/")+1) + "\"><b><i>"+ ".." +"</i></b></a></td></tr>\n";
//                }
                for(File child: filelist) {
                    String child_path = (req_path + "/" + child.getName().replace(" ","%20")).replaceAll("/+","/");
                    if(child.isDirectory()) {
                        content+="<tr><td valign=\"top\">&nbsp;</td><td><a href=\""+ child_path + "\"><b><i>"+child.getName()+"</i></b></a></td></tr>\n";
                    } else {
                        content+="<tr><td valign=\"top\">&nbsp;</td><td><a target=\"_blank\" href=\""+ child_path + "\">"+child.getName()+"</a></td></tr>\n";
                    }
                }
                content+="<tr><th colspan=\"5\"><hr></th></tr>";
                content+="</table>\n" +
                        "</body></html>";

                String response = "HTTP/1.1 200 OK\r\n" +
                                "Server: Java HTTP Server: 1.0\r\n" +
                                "Date: " + new Date() + "\r\n" +
                                "Content-Type: text/html\r\n" +
                                "Content-Length: " + content.length() + "\r\n" +
                                "\r\n" +
                                content;

                pr.write(response);
                pr.flush();
                logger2.log_res(response);
            } else {
                String res_content_type = "application/force-download";
                if(".jpeg".equalsIgnoreCase(Utils.getExt(req_file)) || ".jpg".equalsIgnoreCase(Utils.getExt(req_file))) {
                    res_content_type = "image/jpeg";
                } else if (".jpeg".equalsIgnoreCase(Utils.getExt(req_file))) {
                    res_content_type = "image/png";
                } else if (".gif".equalsIgnoreCase(Utils.getExt(req_file))) {
                    res_content_type = "image/gif";
                } else if (".webp".equalsIgnoreCase(Utils.getExt(req_file))) {
                    res_content_type = "image/webp";
                } else if (".txt".equalsIgnoreCase(Utils.getExt(req_file)) || ".text".equalsIgnoreCase(Utils.getExt(req_file))) {
                    res_content_type = "text/plain";
                }
                long res_content_len = req_file.length();
                String response = "HTTP/1.1 200 OK\r\n"+
                                "Date: " + new Date() + "\r\n" +
                                "Server: Java HTTP Server: 1.0\r\n" +
                                "Content-Type: "+ res_content_type +"\r\n" +
                                "Content-Length: " + res_content_len + "\r\n" +
                                "\r\n";
                pr.write(response);
                pr.flush();
                logger2.log_res(response);

                BufferedInputStream file_bis = null;
                byte[] buffer = new byte[Utils.CHUNK_SIZE];
                int buffer_size;
                try {
                    file_bis = new BufferedInputStream(new FileInputStream(req_file));
                    int i = 0;
                    while((buffer_size = file_bis.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, buffer_size);
                        outputStream.flush();
//                        System.out.println(buffer);
                        logger2.log("\n["+(++i)+"-TH BINARY CHUNK OF SIZE "+ buffer_size +"]");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger2.log("\n\nSUCCESSFULLY SENT");

            }
        }
        else if (request_headers.get(0).startsWith("UPLOAD")) {
            String req_upload_file_path = "root/uploaded/"+request_headers.get(0)
                                                            .substring(request_headers.get(0).indexOf(' ')+1)
                                                            .replace("%20"," ")
                                                            .replaceAll("/+","/");
            String req_upload_dir_path = req_upload_file_path.substring(0,req_upload_file_path.lastIndexOf("/"));
            long req_upload_file_length = 0;
            try{
                req_upload_file_length = Long.valueOf(request_headers.get(1));
            } catch (NumberFormatException e) {
                pr.write("invalid\r\n");
                pr.flush();
                System.out.println("Invalid format");
                logger2.log("\nFILE ERROR");
                kill();
                return;
            }

            if(req_upload_file_length<0 || !Utils.isUploadFilenameValid(req_upload_file_path)) {
                // error
                pr.write("invalid\r\n");
                pr.flush();
                System.out.println("File error");
                logger2.log("\nFILE ERROR");
                kill();
                return;
            }

            pr.write("valid\r\n");
            pr.flush();



            File file_tobe_uploaded = new File(req_upload_file_path);

            File dir = new File(req_upload_dir_path);
            // apply lock
            if(!dir.exists()) {
                dir.mkdirs();
            }
            // release lock

            BufferedOutputStream file_bos = null;
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//            try {
//                bufferedInputStream = new BufferedInputStream(inputStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            byte buffer[] = new byte[Utils.CHUNK_SIZE];
            int buffer_size;
            try {
                file_bos = new BufferedOutputStream(new FileOutputStream(file_tobe_uploaded));
                int i = 0;
                while((buffer_size = bufferedInputStream.read(buffer)) > 0) {
                    file_bos.write(buffer, 0, buffer_size);
                    file_bos.flush();
//                    System.out.println(new String(buffer));
                    logger2.log("\n["+(++i)+"-TH BINARY CHUNK OF SIZE " + buffer_size + "]");
                }
                logger2.log("\nSUCCESSFULLY UPLOADED");
                System.out.println("SUCCESSFULLY UPLOADED");

//                System.out.println(Math.ceil((double)req_upload_file_length/(double) Utils.CHUNK_SIZE));
//                for(long i = 0; i < Math.ceil((double)req_upload_file_length/(double) Utils.CHUNK_SIZE) /* && ((buffer_size = bufferedInputStream.read(buffer)) > 0) */; i++) {
//                    buffer_size = inputStream.read(buffer);
//                    file_bos.write(buffer, 0, buffer_size);
//                    logger2.log("\n[BINARY CHUNK OF SIZE " + buffer_size + "]");
//                }
//                file_bos.flush();

                file_bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        kill();

    }

    private void kill() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            System.out.println("=======END=======");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

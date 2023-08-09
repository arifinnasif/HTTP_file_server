import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Logger {
    private static final String LOG_DIR = "logs";
    private File logfile;
    private FileWriter fileWriter;
    private static int client_count = 0;
    public Logger() {
        String now = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime());

        logfile = new File(LOG_DIR+"/"+now+"_"+(++client_count)+".txt");
        try {
            fileWriter = new FileWriter(logfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void log(String string) {
        try {
            fileWriter.write(string);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void log_req(String req) {
        log(">>>REQUEST<<<\n\n");
        log(req);
        log("\n\n\n\n");
    }

    void log_res(String res) {
        log("<<<RESPONSE>>>\n\n");
        log(res);
    }
}

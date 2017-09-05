import Controllers.Compress;

import java.io.File;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(7000);
        threadPool(Runtime.getRuntime().availableProcessors());
        deleteDirectory(new File("upload"));

        Compress compressController = new Compress();

        path("/api", () -> {

           post("/compress", compressController::ReceiveTextures);
           post("/status", compressController::GetStatusOfCompressionJob);

        });

        createScheduler(compressController);
    }

    private static void createScheduler(Compress compressController) {

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 2);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Timer timer = new Timer();
        timer.schedule(new ClearJobsTask(compressController), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
    }


    static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return(directory.delete());
    }
}

class ClearJobsTask extends TimerTask {
    private Compress controller;

    ClearJobsTask(Compress controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        Main.deleteDirectory(new File("upload"));
        controller.ClearJobs();
    }
}
import Controllers.Compress;

import java.io.File;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(7000);
        threadPool(Runtime.getRuntime().availableProcessors());
        deleteDirectory(new File("upload"));

        Compress compressController = new Compress();

        path("/api", ()-> {

           post("/compress", compressController::ReceiveTextures);
           get("/status", compressController::GetStatusOfCompressionJob);

        });
    }

    private static boolean deleteDirectory(File directory) {
        if(directory.exists()){
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

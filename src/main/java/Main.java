import Controllers.Compress;

import java.io.File;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(7000);
        threadPool(Runtime.getRuntime().availableProcessors());

        Compress compressController = new Compress();

        path("/api", ()-> {

           post("/compress", compressController::ReceiveTextures);
           get("/status", compressController::GetStatusOfCompressionJob);

        });
    }

    private static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null != files){
                for (int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }
}

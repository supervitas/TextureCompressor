import Controllers.Compress;

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
}

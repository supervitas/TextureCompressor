package Controllers;

import org.json.JSONArray;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class Compress {

    private String uploadFolder = "textures";
    private long maxFileSize = 20000000; // 20 mb per file
    private long maxRequestSize = 200000000; // 200 mb per request
    private int fileSizeThreshold = 1024;

    public String ReceiveTextures(Request req, Response res) {


        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(this.uploadFolder, this.maxFileSize,
                this.maxRequestSize, this.fileSizeThreshold);
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                multipartConfigElement);

        Collection<Part> parts = null;
        try {
            parts = req.raw().getParts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (parts == null) {
            res.status(400);
            return "Error";
        }

        for (Part part : parts) {
            System.out.println("Name:" + part.getName());
            System.out.println("Size: " + part.getSize());
            System.out.println("Filename:" + part.getSubmittedFileName());
        }

        return "Ok";
    }

    public String GetStatusOfCompressionJob(Request req, Response res){
        return "OK";
    }


}

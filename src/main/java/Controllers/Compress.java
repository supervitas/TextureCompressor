package Controllers;

import Job.JobManager;
import Json.JsonUtil;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

public class Compress {

    private String uploadFolder = "textures";
    private long maxFileSize = 20000000; // 20 mb per file
    private long maxRequestSize = 200000000; // 200 mb per request
    private int fileSizeThreshold = 1024;

    private Integer jobID = 0;
    private JobManager jobManager = new JobManager();

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

        if (parts == null || parts.size() == 0) {
            res.status(400);
            return JsonUtil.ToJson("error", "Files Not Passed");
        }

        jobID++;
        String jobFolder = CreateFoldersForJob(jobID.toString());

        for (Part part : parts) {
            Path out = Paths.get(jobFolder + part.getSubmittedFileName());

            try (final InputStream in = part.getInputStream()) {
                Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if (jobManager.CreateJobCompress(jobFolder, jobID)) {
            return JsonUtil.ToJson("jobID", jobID.toString());
        }

        res.status(403);
        return JsonUtil.ToJson("error", "All workers are busy. Repeat latter");
    }

    public String GetStatusOfCompressionJob(Request req, Response res){
        return "OK";
    }


    private String CreateFoldersForJob(String jobID){
        File dir = new File("upload/job/".concat(jobID).concat("/result"));
        dir.mkdirs();
        return String.format("upload/job/%s/", jobID);
    }

}

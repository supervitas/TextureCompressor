package Job;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class JobCompress {
    int jobId;
    private boolean jobDone = false;
    private String folderWithTextures;

    private String PVRTexToolPath = "/Applications/Imagination/PowerVR_Graphics/PowerVR_Tools/PVRTexTool/CLI/OSX_x86/PVRTexToolCLI";
    private String convertPath = "/usr/local/bin/convert";

    JobCompress(int jobID, String folder) {
        jobId = jobID;
        folderWithTextures = folder;
        CompressFiles();
    }

    private void CompressFiles() {
        try (Stream<Path> paths = Files.walk(Paths.get(folderWithTextures))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file->{
                        if(isFileImage(file)) {
                            PVRCompress(file);
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isFileImage(Path path) {
        File f = new File(path.toString());
        String mimeType = new MimetypesFileTypeMap().getContentType(f);
        String type = mimeType.split("/")[0];
        return type.equals("image");
    }

    private void PVRCompress(Path path){
        System.out.println(path.getFileName());
    }

    boolean GetStatusOfJob() {
        return jobDone;
    }
}

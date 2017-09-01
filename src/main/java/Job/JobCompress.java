package Job;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
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
        new Thread(this::CompressFiles).run();
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
            System.out.println("Done!");
            jobDone = true;
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
        String[] command = {PVRTexToolPath, "-i", path.getFileName().toString(), "-o",
                String.format("%s.pvr", path.getFileName().toString().replaceFirst("[.][^.]+$", "")),
        "-f", "PVRTC1_4,UBN,lRGB", "-q", "pvrtcbest", "-flip", "y", "-m"};

        ProcessBuilder processBuilder = new ProcessBuilder(command).inheritIO();
        processBuilder.directory(new File(path.toAbsolutePath().getParent().toString()));

        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean GetStatusOfJob() {
        return jobDone;
    }
}

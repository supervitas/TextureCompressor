package Job;

import sun.net.www.URLConnection;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;


class JobCompress {
    int jobId;
    private boolean jobDone = false;
    private String folderWithTextures;
    private Runnable onFinish;

    private String PVRTexToolPath = "/Applications/Imagination/PowerVR_Graphics/PowerVR_Tools/PVRTexTool/CLI/OSX_x86/PVRTexToolCLI";
    private String convertPath = "/usr/local/bin/convert";


    JobCompress(int jobID, String folder, Runnable onFinishCallback) {
        jobId = jobID;
        folderWithTextures = folder;
        onFinish = onFinishCallback;
        new Thread(this::CompressFiles).start();
    }

    private void CompressFiles() {
        try (Stream<Path> paths = Files.walk(Paths.get(folderWithTextures))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        if (isFileImage(file)) {

                            DDSCompress(file);
                            PVRCompress(file);

                        }
                    });
            onFinish.run();
            System.out.println("Done!");
            jobDone = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isFileImage(Path path) {
        File f = new File(path.toString());
        String mimeType = URLConnection.guessContentTypeFromName(f.getName());

        if (mimeType == null) return false;

        String type = mimeType.split("/")[0];
        return type.equals("image");
    }

    private void PVRCompress(Path path) {
        String[] command = {PVRTexToolPath, "-i", path.getFileName().toString(), "-o",
                String.format("result/%s.pvr", path.getFileName().toString().replaceFirst("[.][^.]+$", "")),
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

    private void DDSCompress(Path path) {
        String dxtCompression = "dds:compression=dxt1";

        int i = path.getFileName().toString().lastIndexOf('.');
        if (i > 0) {
            String extension = path.getFileName().toString().substring(i + 1);
            if (Objects.equals(extension, "png")) {
                dxtCompression = "dds:compression=dxt5";
            }
        }

        String[] command = {convertPath, path.getFileName().toString(), "-flip", "-define", dxtCompression,
                "-define", "dds:cluster-fit=true",
                String.format("result/%s.dds", path.getFileName().toString().replaceFirst("[.][^.]+$", ""))};

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

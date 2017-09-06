package Job;

import org.zeroturnaround.zip.ZipUtil;
import sun.net.www.URLConnection;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;


class JobCompress {
    int jobId;
    private Boolean jobDone = false;
    private String folderWithTextures;
    private Runnable onFinish;
    private String path = null;

    private Integer allFilesCount = 0;
    private Integer processedFilesCount = 0;


    //OS X
    private String PVRTexToolPath = "/Applications/Imagination/PowerVR_Graphics/PowerVR_Tools/PVRTexTool/CLI/OSX_x86/PVRTexToolCLI";
    private String convertPath = "/usr/local/bin/convert";

    // linux
//    private String PVRTexToolPath = "/opt/Imagination/PowerVR_Graphics/PowerVR_Tools/PVRTexTool/CLI/Linux_x86_64/PVRTexToolCLI";
//    private String convertPath = "/usr/bin/convert";

    JobCompress(int jobID, String folder, Runnable onFinishCallback) {
        jobId = jobID;
        folderWithTextures = folder;
        onFinish = onFinishCallback;
        new Thread(this::CompressFiles).start();
    }

    private void CompressFiles() {
        try {
            Files.walk(Paths.get(folderWithTextures))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    if (isFileImage(file)) {
                        allFilesCount++;
                    }
                });

            Files.walk(Paths.get(folderWithTextures))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    if (isFileImage(file)) {

                        DDSCompress(file);
                        PVRCompress(file);

                        new File(file.toString()).delete(); // delete original after compressing

                        processedFilesCount++;
                    }
                });

            ZipResult();
            onFinish.run();
            jobDone = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ZipResult() {
        ZipUtil.pack(new File(folderWithTextures.concat("result")).getAbsoluteFile(),
                new File(folderWithTextures.concat("result.zip")));
        path = folderWithTextures.concat("result.zip");
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

        CreateAwaitableProcess(command, path);
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

        CreateAwaitableProcess(command, path);
    }

    private void CreateAwaitableProcess(String[] command, Path path) {
        ProcessBuilder processBuilder = new ProcessBuilder(command).inheritIO();
        processBuilder.directory(new File(path.toAbsolutePath().getParent().toString()));

        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    HashMap<String, String> GetStatusOfJob() {
        HashMap<String, String> status = new HashMap<>();
        status.put("isReady", jobDone.toString());
        status.put("allFiles", allFilesCount.toString());
        status.put("processedFiles", processedFilesCount.toString());

        if (path != null) {
            status.put("path", path);
        }

        return status;
    }
}

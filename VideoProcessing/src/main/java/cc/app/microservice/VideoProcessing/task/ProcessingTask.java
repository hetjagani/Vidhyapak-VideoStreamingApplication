package cc.app.microservice.VideoProcessing.task;

import cc.app.microservice.VideoProcessing.VideoProcessingApplication;
import cc.app.microservice.VideoProcessing.model.*;
import cc.app.microservice.VideoProcessing.util.DiscoveryUtil;
import com.abercap.mediainfo.api.MediaInfo;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.*;


public class ProcessingTask implements Runnable{

    private RestTemplate restTemplate = new RestTemplate();
    private DiscoveryUtil discoveryUtil = new DiscoveryUtil();

    private static Path INPUT_FOLDER_PATH;
    private static Path OUTPUT_FOLDER_PATH;
    private static Process proc;
    private static ArrayList<String> DOC_FILE_EXTS;
    private static ArrayList<String> MEDIA_FILE_EXTS;
    private static ArrayList<String> VID_RESOLUTION_LIST;
    private static HttpHeaders authHeader;
    private static String status;
    private static String statusMsg;

    public ProcessingTask() {
        DOC_FILE_EXTS = new ArrayList<>(){
            {
                add("pdf");add("ppt");add("pptx");add("doc");add("docx");add("txt");add("odt");
                add("ods");add("htm");add("html");add("epub");add("xls");add("xlsx");add("c");add("py");add("cpp");add("java");
            }
        };
        MEDIA_FILE_EXTS = new ArrayList<>(){
            {
                add("mp4");add("mkv");add("mov");add("avi");add("wmv");add("flv");
                add("webm");add("ogg");
            }
        };
        VID_RESOLUTION_LIST = new ArrayList<>(){
            {
                add("144");add("240");add("360");add("480");add("720");add("1080");
            }
        };
        INPUT_FOLDER_PATH = Paths.get("/mnt/shared/unprocessed_files");
        OUTPUT_FOLDER_PATH = Paths.get("/mnt/shared/processed_files/");
    }

    public HttpHeaders getJwtHeader() {
        try {
            String authIp = discoveryUtil.getServiceAddress("AuthenticationService");
            if(authIp == null) {
                return null;
            }
            authIp += "/authentication/authenticate";
            AuthenticationRequest req = new AuthenticationRequest("hetjagani", "password");
            AuthenticationResponse res = restTemplate.postForObject(authIp, req, AuthenticationResponse.class);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + res.getJwt());
            return headers;
        }catch (HttpClientErrorException ex) {
            return null;
        }catch (ResourceAccessException ex) {
            return null;
        }
    }

    public UploadedFile getFileForProcessing(){
        try {
            String uplUrl = discoveryUtil.getServiceAddress("VideoUploadService")+"/upload/pending/path";

            HttpEntity entity = new HttpEntity(authHeader);
            ResponseEntity<UploadedFile> responseEntity = restTemplate.exchange(uplUrl, HttpMethod.GET, entity, UploadedFile.class);

            return responseEntity.getBody();
        }
        catch (HttpClientErrorException e){
            return null;
        }catch (NullPointerException e){
            return null;
        }
    }

    public int unzipFile(Path inputFilePath, Path outputFilePath){
        try{
            proc = new ProcessBuilder("unzip", "-n", inputFilePath.toString()
                    , "-d", outputFilePath.toString()).start();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            BufferedReader outReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = outReader.readLine()) != null) {
                System.out.println(line);
            }
            errorReader.close();
            outReader.close();
            if(proc.waitFor()==0){
                System.out.println("Unzipped successfully");
                proc.destroy();
                return 0;
            }
            else {
                System.out.println("Failure in unzipping");
                status = "FAILED";
                statusMsg = "Failure unzipping the provided file";
                proc.destroy();
                return -1;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        proc.destroy();
        return -1;
    }

    public Map<String,List<String>> getAllFileNames(Path dirPath){
        Map<String,List<String>> files = new HashMap<>() {
            {
                put("MEDIA", new ArrayList<>());
                put("DOC", new ArrayList<>());
            }
        };
        File[] listOfFiles = new File(dirPath.toString()).listFiles();
        if(listOfFiles==null){return null;}
        String ext, filePath;
        for (File file: listOfFiles){
            if(file.isFile()){
                file.renameTo(new File(file.getAbsolutePath().replaceAll(" ", "_")));
            }
        }
        listOfFiles = new File(dirPath.toString()).listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                filePath = file.getAbsolutePath();
                ext = Files.getFileExtension(filePath);
                if (MEDIA_FILE_EXTS.contains(ext.toLowerCase())) {
                    files.get("MEDIA").add(Paths.get(dirPath.getFileName().getFileName().toString(), file.getName()).toString());
                }
                if (DOC_FILE_EXTS.contains(ext.toLowerCase())) {
                    files.get("DOC").add(Paths.get(dirPath.getFileName().getFileName().toString(), file.getName()).toString());
                }
            }
        }
        return files;
    }

    public int processVideo(String inputVideoFilePath){
        try{
            String videoFileName = Paths.get(INPUT_FOLDER_PATH.toString(), inputVideoFilePath).getFileName().toString();
            String videoFolderPath = Paths.get(OUTPUT_FOLDER_PATH.toString(), inputVideoFilePath).getParent().toString();

            String processScriptPath = VideoProcessingApplication.getScriptPath();

            ProcessBuilder processBuilder = new ProcessBuilder("bash", processScriptPath, videoFileName, videoFolderPath);
            proc = processBuilder.start();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            BufferedReader outReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = outReader.readLine()) != null) {
                System.out.println(line);
            }
            errorReader.close();
            outReader.close();
            if(proc.waitFor()==0){
                System.out.println("Video processing successful");
                proc.destroy();
                return 0;
            }
            else{
                System.out.println("Error executing bash script for video processing");
                proc.destroy();
                return -1;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        proc.destroy();
        return -1;
    }

    private void informUploadService(UploadedFile pendingFile){

        pendingFile.setStatus(status);
        pendingFile.setStatusMessage(statusMsg);

        String uplUrl = discoveryUtil.getServiceAddress("VideoUploadService") + "/upload/status/";

        HttpEntity uplEntity = new HttpEntity(pendingFile, authHeader);
        ResponseEntity<String> ack = restTemplate.exchange(uplUrl + pendingFile.getFileId(), HttpMethod.PUT, uplEntity, String.class);

        System.out.println(ack.getStatusCode());
        System.out.println(ack.getBody());
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            authHeader = getJwtHeader();
            if(authHeader == null) {
                System.out.println("Did not get Authentication headers... Check status of authentication service");
                continue;
            }

            /* Getting filepath by sending HTTP GET to Upload service using restTemplate */
            UploadedFile pendingFile = getFileForProcessing();
            if (pendingFile == null) {
                System.out.println("No pending video found");
                continue;
            }
            Path inputFilePath = Paths.get(INPUT_FOLDER_PATH.toString(), pendingFile.getFilePath());
            Path outputFilePath = Paths.get(OUTPUT_FOLDER_PATH.toString(), String.valueOf(inputFilePath.getParent().getFileName()));

            if (unzipFile(inputFilePath, outputFilePath) != 0) {
                informUploadService(pendingFile);
                continue;
            }
            Map<String, List<String>> filePathList;
            filePathList = getAllFileNames(outputFilePath);
            System.out.println(filePathList.toString());
            if (filePathList.get("MEDIA").size() != 1) {
                // Exception: More than 1 video in a zip file or no video at all.
                status = "FAILED";
                statusMsg = "More than 1 video files in a zip file or no video file found." +
                        " Zip file should contain only 1 video file and one/many document (pdf, docx, ppt, etc.) files.";
                informUploadService(pendingFile);
                continue;
            }
            if (processVideo(filePathList.get("MEDIA").get(0)) != 0) {
                // Exception: Video processing failure
                status = "FAILED";
                statusMsg = "Error occured while processing the video file " + filePathList.get("MEDIA").get(0) +
                        ". Please contact admin to resolve the issue.";
                informUploadService(pendingFile);
                continue;
            }
            Video postData = createVideoObject(pendingFile,
                    filePathList.get("DOC"),
                    outputFilePath);

            //Add all the required entries to video streaming service database by making HTTP POST requests
            String regUrl = discoveryUtil.getServiceAddress("VideoStreamingService") + "/stream/video/register";

            HttpEntity entity = new HttpEntity(postData, authHeader);
            ResponseEntity<String> ack = restTemplate.exchange(regUrl, HttpMethod.POST, entity, String.class);

            System.out.println(ack.getStatusCode());
            System.out.println(ack.getBody());

            if (ack.getStatusCode() == HttpStatus.OK) {
                status = "ARCHIVED";
                statusMsg = "Your uploaded file has been successfully processed and uploaded to the server.";
                informUploadService(pendingFile);
            }
        }
    }

    public Video createVideoObject(UploadedFile file, List<String> docPaths, Path processedPath){
        Video videoObj = new Video();

        videoObj.setVideoTitle(file.getVideoTitle());
        videoObj.setVideoDescription(file.getVideoDescription());
        videoObj.setCourseId(file.getCourseId());

        List<LectureNote> listOfLecNotes = new ArrayList<>();
        for(String docPath: docPaths){
            String fName = Paths.get(docPath).getFileName().toString();
            String fType = Files.getFileExtension(fName);
            float fSize = (float) new File(Paths.get(processedPath.getParent().toString(), docPath).toString()).length()/1024;
            listOfLecNotes.add(new LectureNote(fName, docPath, fType, fSize));
        }

        videoObj.setLectureNotes(listOfLecNotes);

        List<VideoResolution> videoResolutionList = new ArrayList<>();
        File[] listOfFiles = new File(processedPath.toString()).listFiles();
        if(listOfFiles==null){return null;}
        for(File folder: listOfFiles) {
            if (folder.isDirectory()) {
                String dirName = folder.getName();
                if(VID_RESOLUTION_LIST.contains(dirName)){
                    String fName = dirName + ".mp4";
                    Path fPath = Paths.get(processedPath.getFileName().toString(), dirName, fName);
                    String fFormat = Files.getFileExtension(fName);
                    File videoFile = new File(Paths.get(processedPath.toString(), dirName, fName).toString());
                    MediaInfo info = new MediaInfo();
                    info.open(videoFile);

                    float fps = Float.parseFloat(info.get(MediaInfo.StreamKind.Video, 0, "FrameRate"));
                    int width = Integer.parseInt(info.get(MediaInfo.StreamKind.Video, 0, "Width"));
                    int height = Integer.parseInt(info.get(MediaInfo.StreamKind.Video, 0, "Height"));
                    long duration = Long.parseLong(info.get(MediaInfo.StreamKind.Video, 0, "Duration"));
                    float fSize = (float) videoFile.length()/1024;
                    videoResolutionList.add(new VideoResolution(fName, fPath.toString(), new Time(duration), width, height, fps, fSize, fFormat));
                }
            }
        }
        videoObj.setVideoResolutions(videoResolutionList);
        System.out.println(videoObj.toString());
        return videoObj;
    }
}

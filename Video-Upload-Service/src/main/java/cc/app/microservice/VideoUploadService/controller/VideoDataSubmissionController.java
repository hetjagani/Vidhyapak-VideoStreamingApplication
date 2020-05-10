package cc.app.microservice.VideoUploadService.controller;

import cc.app.microservice.VideoUploadService.VideoUploadServiceApplication;
import cc.app.microservice.VideoUploadService.model.RequestData;
import cc.app.microservice.VideoUploadService.model.UploadedFile;
import cc.app.microservice.VideoUploadService.data.UploadedFileRepository;
import cc.app.microservice.VideoUploadService.util.DiscoveryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

@RestController
@RequestMapping("/upload")
public class VideoDataSubmissionController {

    @Autowired
    UploadedFileRepository dataRepo;

    @Autowired
    private DiscoveryUtil discUtil;

    private static String UPLOAD_PATH = "/mnt/shared/unprocessed_files/";

    @PostMapping("/video/submit")
    public ResponseEntity<String> submitVideoData(@ModelAttribute RequestData reqData) {
        if(!reqData.isValid()){
            return new ResponseEntity<>("Invalid field values",
                    HttpStatus.BAD_REQUEST);
        }
        MultipartFile file = reqData.getFile();
        try {
            UploadedFile fileMeta = new UploadedFile(file.getOriginalFilename(),
                    reqData.getVideoTitle(),
                    reqData.getVideoDescription(),
                    reqData.getCourseId(),
                    (float)file.getSize()/(1024*1024),
                    file.getContentType(),
                    "",
                    new Timestamp(System.currentTimeMillis()),
                    "PENDING",
                    "Video queued up for processing");

            Long fileId = dataRepo.save(fileMeta);
            System.out.println("Uploaded Video Added to the database...");
            if(fileId == -1){
                return new ResponseEntity<>("Internal Failure!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String folderPath = UPLOAD_PATH + fileId + "/";
            boolean retVal = new File(folderPath).mkdir();
            if(!retVal){
                throw new Exception("Can't create directory at the location " + folderPath);
            }
            file.transferTo(new File(folderPath + file.getOriginalFilename()));
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Internal Failure!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(HttpStatus.MOVED_TEMPORARILY)
                .header("Location", "http://" + VideoUploadServiceApplication.getHostIP() + "/video/upload")
                .body("Upload successful!");
    }

    @GetMapping("{id}/details")
    public ResponseEntity<UploadedFile> getVideoDetails(@PathVariable("id") String videoId) {
        try {
            return ResponseEntity.ok(dataRepo.getVideoDetails(videoId).get());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pending/path")
    public ResponseEntity<UploadedFile> getFileForProcessing() {
        try {
            return ResponseEntity.ok(dataRepo.getFileForProcessing().get());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pending/total")
    public ResponseEntity<Integer> getNumberOfPendingEntries() {
        try {
            return ResponseEntity.ok(dataRepo.getNumberOfPendingEntries());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/status/{id}")
    public ResponseEntity<String> changeFileStatus(@PathVariable("id") long fileId, @RequestBody UploadedFile file){
        System.out.println(file.toString());
        try {
            return ResponseEntity.ok(dataRepo.changeFileStatus(fileId, file).get());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

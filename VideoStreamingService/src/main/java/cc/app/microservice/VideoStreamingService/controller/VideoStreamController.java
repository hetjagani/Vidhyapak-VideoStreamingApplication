package cc.app.microservice.VideoStreamingService.controller;

import cc.app.microservice.VideoStreamingService.data.VideoInfoRepo;
import cc.app.microservice.VideoStreamingService.model.Video;
import cc.app.microservice.VideoStreamingService.model.VideoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stream")
public class VideoStreamController {

    @Autowired
    private VideoInfoRepo videoInfoRepo;

    private static Path OUTPUT_FOLDER_PATH = Paths.get("/mnt/shared/processed_files/");

    @GetMapping("/video/{id}/res/{res}")
    public ResponseEntity<ResourceRegion> streamVideo(@PathVariable("id") long videoId,
                                                      @PathVariable("res") int res,
                                                      @RequestHeader HttpHeaders headers) {
        try{
            String filePath = videoInfoRepo.getVideoPath(videoId, res).get();
            UrlResource video = new UrlResource(
                    "file:" + Paths.get(OUTPUT_FOLDER_PATH.toString(), filePath).toString());
            long videoLen = video.contentLength();
            long totalBytes;
            List<HttpRange> range = headers.getRange();
            ResourceRegion resourceRegion;
            if(!range.isEmpty()){
                long start = range.get(0).getRangeStart(videoLen);
                long end;
                if(res>=144 && res<=360){
                    end = start + (1048576/2);
                }
                else {
                    end = start + 2*1048576;
                }
                if(videoLen <= end){
                    end = videoLen;
                }
                totalBytes = end-start+1;
                resourceRegion = new ResourceRegion(video, start, totalBytes);
            }
            else {
                totalBytes = (1048576<videoLen?1048576:videoLen);
                resourceRegion = new ResourceRegion(video, 0, totalBytes);
            }
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header("keep-alive", "true")
                    .contentType(MediaTypeFactory.getMediaType(video)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .contentLength(totalBytes)
                    .body(resourceRegion);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/video/{id}/details")
    public ResponseEntity<Video> getVideoDetails(@PathVariable("id") long videoId){
        try {
            return ResponseEntity.ok(videoInfoRepo.getVideoDetails(videoId).get());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/video/search")
    public ResponseEntity<VideoList> searchVideos(@RequestParam("search") String userInput){
        try {
            return ResponseEntity.ok(new VideoList(videoInfoRepo.searchVideo(userInput)));
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/video/titleAndDescription/{id}")
    public ResponseEntity<Video> getVideoTitleAndDesc(@PathVariable("id") long videoId){
        try {
            return ResponseEntity.ok(videoInfoRepo.getVideoTitleAndDesc(videoId).get());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/video/titleAndDescription/{id}")
    public ResponseEntity<String> changeVideoTitleAndDesc(@PathVariable("id") long videoId,
                                                          @RequestBody Video videoObj){
        try {
            return ResponseEntity.ok(videoInfoRepo.changeVideoTitleAndDesc(videoId, videoObj).get());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/video/{id}")
    public ResponseEntity<String> removeVideoFromId(@PathVariable("id") long videoId){
        try {
            return ResponseEntity.ok(videoInfoRepo.removeVideoFromId(videoId).get());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/video/register")
    public ResponseEntity<String> registerVideo(@RequestBody Video videoObj){
        try {
            return ResponseEntity.ok(videoInfoRepo.saveVideoDetails(videoObj).get());
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/course/{courseId}/videos")
    public ResponseEntity<VideoList> getVideosForCourse(@PathVariable("courseId") long courseId){
        try {
            return ResponseEntity.ok(new VideoList(videoInfoRepo.getVideosFromCourseId(courseId)));
        }
        catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lectureNote/{id}/download")
    public ResponseEntity<Resource> downloadDocFile(@PathVariable("id") long docId){
        try {
            Optional<String> subPath = videoInfoRepo.getDocFilePath(docId);
            if(!subPath.isPresent()){throw new FileNotFoundException();}
            Path docFilePath = Paths.get(OUTPUT_FOLDER_PATH.toString(), subPath.get());
            File file = new File(docFilePath.toString());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(docFilePath));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + docFilePath.getFileName())
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        } catch (FileNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (NullPointerException | IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

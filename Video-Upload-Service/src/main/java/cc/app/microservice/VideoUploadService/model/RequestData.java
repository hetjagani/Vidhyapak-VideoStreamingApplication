package cc.app.microservice.VideoUploadService.model;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.sql.Time;

public class RequestData {

    private MultipartFile file;
    private String videoTitle;
    private String videoDescription;
    private long courseId;

    public RequestData() {}

    public RequestData(MultipartFile file, String videoTitle, String videoDescription, long courseId) {
        this.file = file;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.courseId = courseId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public boolean isValid(){
        return (!videoTitle.equals("") && !videoDescription.equals("") &&
                !file.isEmpty() && courseId != (-1));
    }

    @Override
    public String toString() {
        return "RequestData{" +
                "file=" + file +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoDescription='" + videoDescription + '\'' +
                ", courseId=" + courseId +
                '}';
    }
}

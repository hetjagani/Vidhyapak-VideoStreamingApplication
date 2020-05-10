package cc.app.microservice.VideoProcessing.model;

import java.sql.Timestamp;

public class UploadedFile {
    enum myEnum {
        PENDING, PROCESSING, FAILED, ARCHIVED
    }

    private long fileId;
    private String fileName;
    private String videoTitle;
    private String videoDescription;
    private long courseId;
    private float fileSize;
    private String fileContentType;
    private String filePath;
    private Timestamp uploadTime;
    private myEnum status;
    private String statusMessage;

    public UploadedFile() {}

    public UploadedFile(String fileName, String videoTitle, String videoDescription, long courseId,
                        float fileSize, String fileContentType, String filePath, Timestamp uploadTime,
                        String status, String msg) {
        this.fileName = fileName;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.courseId = courseId;
        this.fileSize = fileSize;
        this.fileContentType = fileContentType;
        this.filePath = filePath;
        this.uploadTime = uploadTime;
        this.status = myEnum.valueOf(status);
        statusMessage = msg;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getStatus() {
        return status.name();
    }

    public void setStatus(String status) {
        this.status = myEnum.valueOf(status);
    }

    public String getStatusMessage(){
        return this.statusMessage;
    }

    public void setStatusMessage(String msg){
        statusMessage = msg;
    }
}

package cc.app.microservice.UIService.model;

import java.sql.Time;

public class VideoResolution {

    private long videoFileId;
    private String videoFileName;
    private String videoFilePath;
    private Time videoFileDuration;
    private int videoWidth;
    private int videoHeight;
    private float videoFPS;
    private float videoFileSize;
    private String videoFileFormat;

    public VideoResolution(){}

    public long getVideoFileId() {
        return videoFileId;
    }

    public VideoResolution(long videoFileId, String videoFileName, String videoFilePath, Time videoFileDuration, int videoWidth, int videoHeight, float videoFPS, float videoFileSize, String videoFileFormat) {
        this.videoFileId = videoFileId;
        this.videoFileName = videoFileName;
        this.videoFilePath = videoFilePath;
        this.videoFileDuration = videoFileDuration;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFPS = videoFPS;
        this.videoFileSize = videoFileSize;
        this.videoFileFormat = videoFileFormat;
    }

    public VideoResolution(String videoFileName, String videoFilePath, Time videoFileDuration, int videoWidth, int videoHeight, float videoFPS, float videoFileSize, String videoFileFormat) {
        this.videoFileName = videoFileName;
        this.videoFilePath = videoFilePath;
        this.videoFileDuration = videoFileDuration;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFPS = videoFPS;
        this.videoFileSize = videoFileSize;
        this.videoFileFormat = videoFileFormat;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public Time getVideoFileDuration() {
        return videoFileDuration;
    }

    public void setVideoFileDuration(Time videoFileDuration) {
        this.videoFileDuration = videoFileDuration;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public float getVideoFPS() {
        return videoFPS;
    }

    public void setVideoFPS(float videoFPS) {
        this.videoFPS = videoFPS;
    }

    public float getVideoFileSize() {
        return videoFileSize;
    }

    public void setVideoFileSize(float videoFileSize) {
        this.videoFileSize = videoFileSize;
    }

    public String getVideoFileFormat() {
        return videoFileFormat;
    }

    public void setVideoFileFormat(String videoFileFormat) {
        this.videoFileFormat = videoFileFormat;
    }

    @Override
    public String toString() {
        return "VideoResolution{" +
                "videoFileName='" + videoFileName + '\'' +
                ", videoFilePath='" + videoFilePath + '\'' +
                ", videoFileDuration=" + videoFileDuration +
                ", videoWidth=" + videoWidth +
                ", videoHeight=" + videoHeight +
                ", videoFPS=" + videoFPS +
                ", videoFileSize=" + videoFileSize +
                ", videoFileFormat='" + videoFileFormat + '\'' +
                '}';
    }
}

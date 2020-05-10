package cc.app.microservice.UIService.model;

import java.util.List;

public class Video {

    private long videoId;
    private String videoTitle;
    private String videoDescription;
    private long courseId;
    private List<LectureNote> lectureNotes;
    private List<VideoResolution> videoResolutions;

    public Video(){}

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public Video(String videoTitle, String videoDescription, long courseId) {
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.courseId = courseId;
    }

    public Video(String videoTitle, String videoDescription, long courseId, List<LectureNote> lectureNotes, List<VideoResolution> videoResolutions) {
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.courseId = courseId;
        this.lectureNotes = lectureNotes;
        this.videoResolutions = videoResolutions;
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

    public List<LectureNote> getLectureNotes() {
        return lectureNotes;
    }

    public void setLectureNotes(List<LectureNote> lectureNotes) {
        this.lectureNotes = lectureNotes;
    }

    public List<VideoResolution> getVideoResolutions() {
        return videoResolutions;
    }

    public void setVideoResolutions(List<VideoResolution> videoResolutions) {
        this.videoResolutions = videoResolutions;
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId='" + String.valueOf(videoId) + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoDescription='" + videoDescription + '\'' +
                ", courseId=" + courseId +
                ", lectureNotes=" + lectureNotes +
                ", videoResolutions=" + videoResolutions +
                '}';
    }
}

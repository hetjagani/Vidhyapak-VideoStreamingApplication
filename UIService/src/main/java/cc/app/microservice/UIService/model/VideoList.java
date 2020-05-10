package cc.app.microservice.UIService.model;

import java.util.List;

public class VideoList {

    private List<Video> videoList;

    public VideoList(){}

    public VideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }
}

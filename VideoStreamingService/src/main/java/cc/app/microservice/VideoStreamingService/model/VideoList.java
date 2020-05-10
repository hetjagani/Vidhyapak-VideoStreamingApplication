package cc.app.microservice.VideoStreamingService.model;

import java.io.Serializable;
import java.util.List;

public class VideoList implements Serializable {

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

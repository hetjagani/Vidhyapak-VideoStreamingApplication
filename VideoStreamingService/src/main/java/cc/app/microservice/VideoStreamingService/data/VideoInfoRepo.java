package cc.app.microservice.VideoStreamingService.data;

import cc.app.microservice.VideoStreamingService.model.LectureNote;
import cc.app.microservice.VideoStreamingService.model.Video;
import cc.app.microservice.VideoStreamingService.model.VideoList;
import cc.app.microservice.VideoStreamingService.model.VideoResolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VideoInfoRepo {

    @Autowired
    @Qualifier("videoInfoJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public Optional<String> getVideoPath(long videoId, int resolution){
        try{
            String sql = "SELECT videoFilePath FROM VideoResolutions WHERE videoId=? AND videoHeight" +
                    "=?;";
            String filePath = jdbcTemplate.queryForObject(sql, new Object[]{videoId, resolution}, String.class);
            return Optional.of(filePath);
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Cacheable(value = "video-stream-videos-from-courseId", key = "#courseId")
    public List<Video> getVideosFromCourseId(long courseId){
        String sql;
        try {
            sql = "SELECT * FROM Video WHERE courseId=?;";
            List<Video> videoList = jdbcTemplate.query(sql,
                    new Object[]{courseId},
                    (rs, i) -> {
                        Video vid = new Video(
                                rs.getString("videoTitle"),
                                rs.getString("videoDescription"),
                                courseId);
                        vid.setVideoId(rs.getLong("videoId"));
                        return vid;
                    });
            for(var video: videoList){
                sql = "SELECT * FROM LectureNote WHERE videoId=?";
                List<LectureNote> lecNotes = jdbcTemplate.query(
                        sql, new Object[]{video.getVideoId()},
                        new LectureNoteRowMapper());
                video.setLectureNotes(lecNotes);
                sql = "SELECT * FROM VideoResolutions WHERE videoId=?";
                List<VideoResolution> vidRes = jdbcTemplate.query(
                        sql, new Object[]{video.getVideoId()},
                        new VideoResolutionMapper());
                video.setVideoResolutions(vidRes);
            }
            return videoList;
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Video> searchVideo(String userInput){
        String sql;
        try {
            sql = "SELECT * from Video WHERE videoTitle LIKE '%" + userInput +
                    "%' OR videoDescription LIKE '%" + userInput + "%'";
            List<Video> videoList = jdbcTemplate.query(sql,
                    (rs, i) -> {
                        Video vid = new Video(
                                rs.getString("videoTitle"),
                                rs.getString("videoDescription"),
                                rs.getLong("courseId"));
                        vid.setVideoId(rs.getLong("videoId"));
                        return vid;
                    });
            for(var video: videoList){
                sql = "SELECT * FROM LectureNote WHERE videoId=?";
                List<LectureNote> lecNotes = jdbcTemplate.query(
                        sql, new Object[]{video.getVideoId()},
                        new LectureNoteRowMapper());
                video.setLectureNotes(lecNotes);
                sql = "SELECT * FROM VideoResolutions WHERE videoId=?";
                List<VideoResolution> vidRes = jdbcTemplate.query(
                        sql, new Object[]{video.getVideoId()},
                        new VideoResolutionMapper());
                video.setVideoResolutions(vidRes);
            }
            return videoList;
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Optional<String> getDocFilePath(long docId){
        String sql;
        try {
            sql = "SELECT lectureNoteFilePath FROM LectureNote WHERE lectureNoteId=?;";
            return Optional.of(jdbcTemplate.queryForObject(sql, new Object[]{docId}, String.class));
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Caching(evict = {  @CacheEvict(value = "video-stream-video-from-videoId", key = "#videoId"),
            @CacheEvict(value = "video-stream-videos-from-courseId", allEntries = true),
            @CacheEvict(value = "video-stream-videoTitleAndDesc-from-videoId", key = "#videoId")})
    public Optional<String> removeVideoFromId(long videoId){
        String sql;
        try{
            sql = "DELETE FROM Video WHERE videoId=?";
            jdbcTemplate.update(sql, videoId);
            sql = "DELETE FROM VideoResolutions WHERE videoId=?";
            jdbcTemplate.update(sql, videoId);
            sql = "DELETE FROM LectureNote WHERE videoId=?";
            jdbcTemplate.update(sql, videoId);
            return Optional.of("Deleted uploaded video with given video ID");
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Caching(evict = {  @CacheEvict(value = "video-stream-video-from-videoId", key = "#videoId"),
                        @CacheEvict(value = "video-stream-videos-from-courseId", key = "#video.courseId"),
                        @CacheEvict(value = "video-stream-videoTitleAndDesc-from-videoId", key = "#videoId")})
    public Optional<String> changeVideoTitleAndDesc(long videoId, Video video){
        String sql;
        try{
            sql = "UPDATE Video SET videoTitle=?, videoDescription=? WHERE videoId=?";
            jdbcTemplate.update(sql, video.getVideoTitle(), video.getVideoDescription(), videoId);
            return Optional.of("Updated video title and video description Successfully for given video ID");
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Cacheable(value = "video-stream-videoTitleAndDesc-from-videoId", key = "#videoId")
    public Optional<Video> getVideoTitleAndDesc(long videoId){
        String sql;
        try{
            sql = "SELECT * FROM Video WHERE videoId=?";
            Video videoObj = jdbcTemplate.queryForObject(sql, new Object[]{videoId},
                    (rs, rowNum)->{
                        Video vid = new Video(
                                rs.getString("videoTitle"),
                                rs.getString("videoDescription"),
                                rs.getLong("courseId"),
                                new ArrayList<LectureNote>(),
                                new ArrayList<VideoResolution>());
                        vid.setVideoId(rs.getLong("videoId"));
                        return vid;
                    });
            return Optional.of(videoObj);
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Cacheable(value = "video-stream-video-from-videoId", key = "#videoId")
    public Optional<Video> getVideoDetails(long videoId){
        String sql;
        try{
            sql = "SELECT * FROM Video WHERE videoId=?";
            Video videoObj = jdbcTemplate.queryForObject(sql, new Object[]{videoId},
                    (rs, rowNum)->{
                        Video vid = new Video(
                                rs.getString("videoTitle"),
                                rs.getString("videoDescription"),
                                rs.getLong("courseId"));
                        return vid;
                    });
            videoObj.setVideoId(videoId);
            sql = "SELECT * FROM LectureNote WHERE videoId=?";
            List<LectureNote> lecNotes = jdbcTemplate.query(
                    sql, new Object[]{videoId},
                    new LectureNoteRowMapper());
            videoObj.setLectureNotes(lecNotes);
            sql = "SELECT * FROM VideoResolutions WHERE videoId=?";
            List<VideoResolution> vidRes = jdbcTemplate.query(
                    sql, new Object[]{videoId},
                    new VideoResolutionMapper());
            videoObj.setVideoResolutions(vidRes);
            return Optional.of(videoObj);
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @CacheEvict(value = "video-stream-videos-from-courseId", allEntries = true)
    public Optional<String> saveVideoDetails(Video videoObj){
        String insert_query;
        try {
            insert_query = "INSERT INTO Video (videoTitle, videoDescription, courseId) " +
                    "VALUES (?, ?, ?);";
            jdbcTemplate.update(insert_query, videoObj.getVideoTitle(), videoObj.getVideoDescription(),
                                                videoObj.getCourseId());
            Long id = jdbcTemplate.queryForObject("select last_insert_id();", Long.class);
                insert_query = "INSERT INTO LectureNote (lectureNoteFileName, lectureNoteFilePath," +
                    " lectureNoteFileSize, lectureNoteFileType, videoId)" +
                    " VALUES (?, ?, ?, ?, ?);";
            for(LectureNote lecNote: videoObj.getLectureNotes()){
                jdbcTemplate.update(insert_query, lecNote.getLectureNoteFileName(),
                        lecNote.getLectureNoteFilePath(), lecNote.getLectureNoteFileSize(),
                        lecNote.getLectureNoteFileType(), id);
            }
            insert_query = "INSERT INTO VideoResolutions (videoId, videoFileName," +
                    " videoFilePath, videoFileDuration, videoWidth, videoHeight, " +
                    "videoFPS, videoFileSize, videoFileFormat) VALUES (?,?,?,?,?,?,?,?,?);";
            for(VideoResolution vidRes: videoObj.getVideoResolutions()){
                jdbcTemplate.update(insert_query, id, vidRes.getVideoFileName(), vidRes.getVideoFilePath(),
                        vidRes.getVideoFileDuration(), vidRes.getVideoWidth(), vidRes.getVideoHeight(),
                        vidRes.getVideoFPS(), vidRes.getVideoFileSize(), vidRes.getVideoFileFormat());
            }
            return Optional.of("Video data submission successful");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

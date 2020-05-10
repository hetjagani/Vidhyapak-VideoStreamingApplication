package cc.app.microservice.VideoStreamingService.data;

import cc.app.microservice.VideoStreamingService.model.VideoResolution;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoResolutionMapper implements RowMapper<VideoResolution> {


    @Override
    public VideoResolution mapRow(ResultSet rs, int i) throws SQLException {
        return new VideoResolution(
                rs.getLong("videoFileId"),
                rs.getString("videoFileName"),
                rs.getString("videoFilePath"),
                rs.getTime("videoFileDuration"),
                rs.getInt("videoWidth"),
                rs.getInt("videoHeight"),
                rs.getFloat("videoFPS"),
                rs.getFloat("videoFileSize"),
                rs.getString("videoFileFormat"));
    }
}

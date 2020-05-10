package cc.app.microservice.VideoStreamingService.data;
import cc.app.microservice.VideoStreamingService.model.LectureNote;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LectureNoteRowMapper implements RowMapper<LectureNote> {
    @Override
    public LectureNote mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new LectureNote(
                rs.getLong("lectureNoteId"),
                rs.getString("lectureNoteFileName"),
                rs.getString("lectureNoteFilePath"),
                rs.getString("lectureNoteFileType"),
                rs.getFloat("lectureNoteFileSize"));
    }
}

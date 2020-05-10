package cc.app.microservice.VideoProcessing.model;

public class LectureNote {

    private long lectureNoteId;
    private String lectureNoteFileName;
    private String lectureNoteFilePath;
    private String lectureNoteFileType;
    private float lectureNoteFileSize;

    public LectureNote(){}

    public long getLectureNoteId() {
        return lectureNoteId;
    }

    public LectureNote(long lectureNoteId, String lectureNoteFileName, String lectureNoteFilePath, String lectureNoteFileType, float lectureNoteFileSize) {
        this.lectureNoteId = lectureNoteId;
        this.lectureNoteFileName = lectureNoteFileName;
        this.lectureNoteFilePath = lectureNoteFilePath;
        this.lectureNoteFileType = lectureNoteFileType;
        this.lectureNoteFileSize = lectureNoteFileSize;
    }

    public LectureNote(String lectureNoteFileName, String lectureNoteFilePath, String lectureNoteFileType, Float lectureNoteFileSize) {
        this.lectureNoteFileName = lectureNoteFileName;
        this.lectureNoteFilePath = lectureNoteFilePath;
        this.lectureNoteFileType = lectureNoteFileType;
        this.lectureNoteFileSize = lectureNoteFileSize;
    }

    public String getLectureNoteFileName() {
        return lectureNoteFileName;
    }

    public void setLectureNoteFileName(String lectureNoteFileName) {
        this.lectureNoteFileName = lectureNoteFileName;
    }

    public String getLectureNoteFilePath() {
        return lectureNoteFilePath;
    }

    public void setLectureNoteFilePath(String lectureNoteFilePath) {
        this.lectureNoteFilePath = lectureNoteFilePath;
    }

    public String getLectureNoteFileType() {
        return lectureNoteFileType;
    }

    public void setLectureNoteFileType(String lectureNoteFileType) {
        this.lectureNoteFileType = lectureNoteFileType;
    }

    public float getLectureNoteFileSize() {
        return lectureNoteFileSize;
    }

    public void setLectureNoteFileSize(float lectureNoteFileSize) {
        this.lectureNoteFileSize = lectureNoteFileSize;
    }

    @Override
    public String toString() {
        return "LectureNote{" +
                "lectureNoteFileName='" + lectureNoteFileName + '\'' +
                ", lectureNoteFilePath='" + lectureNoteFilePath + '\'' +
                ", lectureNoteFileType='" + lectureNoteFileType + '\'' +
                ", lectureNoteFileSize=" + lectureNoteFileSize +
                '}';
    }
}

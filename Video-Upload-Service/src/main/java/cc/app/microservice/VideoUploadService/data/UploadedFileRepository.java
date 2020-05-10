package cc.app.microservice.VideoUploadService.data;

import cc.app.microservice.VideoUploadService.model.AuthenticationRequest;
import cc.app.microservice.VideoUploadService.model.AuthenticationResponse;
import cc.app.microservice.VideoUploadService.model.DiscoveryInfo;
import cc.app.microservice.VideoUploadService.model.UploadedFile;
import cc.app.microservice.VideoUploadService.util.DiscoveryUtil;
import cc.app.microservice.VideoUploadService.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Repository
public class UploadedFileRepository {

    @Autowired
    @Qualifier("uploadInfoJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryUtil discUtil;

    public Long save(UploadedFile file) {
        try{
            String query = "INSERT INTO UploadedFile (fileName, videoTitle, videoDescription, courseId, fileSize," +
                    " fileContentType, uploadTime, status, statusMessage) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(query, file.getFileName(), file.getVideoTitle(), file.getVideoDescription(),
                    file.getCourseId(), file.getFileSize(), file.getFileContentType(), file.getUploadTime(),
                    file.getStatus(), file.getStatusMessage());
            Long id = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
            String filePath = "/" + id + "/" + file.getFileName();
            jdbcTemplate.update("UPDATE UploadedFile SET filePath = ? WHERE fileId = ?", filePath, id);
            return id;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return (long)-1;
    }

    public Optional<UploadedFile> getVideoDetails(String videoId) {
        try {
            String query = "SELECT * FROM UploadedFile WHERE fileId=?";
            return jdbcTemplate.queryForObject(query, new Object[]{videoId},
                    (rs, rowNum)->{
                            UploadedFile fileData = new UploadedFile(
                                    rs.getString("fileName"),
                                    rs.getString("videoTitle"),
                                    rs.getString("videoDescription"),
                                    rs.getLong("courseId"),
                                    rs.getFloat("fileSize"),
                                    rs.getString("fileContentType"),
                                    rs.getString("filePath"),
                                    rs.getTimestamp("uploadTime"),
                                    rs.getString("status"),
                                    rs.getString("statusMessage"));
                            fileData.setFileId(rs.getLong("fileId"));
                            return Optional.of(fileData);
            });
        }
        catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private ResponseEntity<String> getTargetMail(UploadedFile file){
        String baseURL = discUtil.getServiceAddress("AuthenticationService");
        String authIp = baseURL + "/authentication/authenticate";

        AuthenticationRequest req = new AuthenticationRequest("hetjagani", "password");
        AuthenticationResponse res = restTemplate.postForObject(authIp, req, AuthenticationResponse.class);

        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization", "Bearer "+res.getJwt());

        String destURI = discUtil.getServiceAddress("UserInformation") + "/course/" + file.getCourseId()
                 + "/professor";
        return restTemplate.exchange(destURI, HttpMethod.GET, new HttpEntity<>(authHeader), String.class);
    }

    public Optional<String> changeFileStatus(long fileId, UploadedFile file){
        try{
            String sql = "UPDATE UploadedFile SET status=?, statusMessage=? WHERE fileId=?";
            jdbcTemplate.update(sql, file.getStatus(), file.getStatusMessage(), fileId);
            var resp = getTargetMail(file);
            if(resp.getStatusCode() != HttpStatus.OK){
                throw new Exception(resp.getBody());
            }
            String subject = "Regarding file upload " + file.getFileName() + " at " + file.getUploadTime().toString();
            String targetMail = resp.getBody();
            //String targetMail = "tempanonymous1999@gmail.com";
            System.out.println("Sending mail");
            String text = "\n\nFile upload at " + file.getUploadTime();
            text += "\n\nWith following details: \n";
            text += "Video Title: " + file.getVideoTitle();
            text += "\nVideo Description: " + file.getVideoDescription();
            text += "\nmarked as '" + file.getStatus() + "'";
            text += "\nStatus message: " + file.getStatusMessage();
            System.out.println(text);
            if(MailUtil.sendMail(text, subject, targetMail) == 0) {
                return Optional.of("Status changed for supplied fileId and informed professor");
            }
            else{
                throw new Exception("Exception while mailing professor");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Optional<UploadedFile> getFileForProcessing(){
        try {
            String query = "SELECT * FROM UploadedFile where status='PENDING'" +
                    " ORDER BY uploadTime ASC LIMIT 1";
            UploadedFile pendingFile = jdbcTemplate.queryForObject(query, new Object[]{}, (rs, i) -> {
                        UploadedFile pendingFileData = new UploadedFile(
                                rs.getString("fileName"),
                                rs.getString("videoTitle"),
                                rs.getString("videoDescription"),
                                rs.getLong("courseId"),
                                rs.getFloat("fileSize"),
                                rs.getString("fileContentType"),
                                rs.getString("filePath"),
                                rs.getTimestamp("uploadTime"),
                                rs.getString("status"),
                                rs.getString("statusMessage"));
                                pendingFileData.setFileId(rs.getLong("fileId"));
                                return pendingFileData;
                    }
            );
            jdbcTemplate.update("UPDATE UploadedFile SET status='PROCESSING', " +
                    "statusMessage='File is being processed by processing service' WHERE fileId=?", pendingFile.getFileId());
            return Optional.of(pendingFile);
        }
        catch (EmptyResultDataAccessException e){
            System.out.println("PENDING VIDEO NOT FOUND...");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Integer getNumberOfPendingEntries(){
        try {
            String query = "SELECT COUNT(*) FROM UploadedFile WHERE status = 'PENDING'";
            return jdbcTemplate.queryForObject(query, new Object[]{}, Integer.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}

package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.UiServiceApplication;
import cc.app.microservice.UIService.model.*;
import cc.app.microservice.UIService.service.CourseInfoWithUserName;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import cc.app.microservice.UIService.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class VideoUploadController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DiscoveryUtil discUtil;

    @Autowired
    private CourseInfoWithUserName courseInfoUN;

    @GetMapping(value = "/videoDetails/edit")
    @ResponseBody
    public Video getVideo(@CookieValue("jwt") String jwt, HttpServletResponse servletResponse, long id){

        // Create Auth Header to send HTTP requests to other services
        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization", "Bearer " + jwt);

        // Fetch address of Video Streaming Service from Discovery Service
        String baseURL = discUtil.getServiceAddress("VideoStreamingService");

        String destURI = baseURL + "/stream/video/titleAndDescription/" + String.valueOf(id);

        // Get list of videos with course IDs
        ResponseEntity<Video> videoResponseEntity = restTemplate.exchange(destURI,
                HttpMethod.GET,
                new HttpEntity(authHeader),
                Video.class);

        // If JWT is invalid then redirect to login
        if (videoResponseEntity.getStatusCode() != HttpStatus.OK) {
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return null;
        }
        return videoResponseEntity.getBody();
    }

    @RequestMapping(value = "/video/update", method={RequestMethod.GET, RequestMethod.PUT})
    public String changeVideoTitleAndDesc(Video video, @CookieValue("jwt") String jwt,
                                          HttpServletResponse servletResponse){

        // Create Auth Header to send HTTP requests to other services
        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization", "Bearer " + jwt);

        // Fetch address of Video Streaming Service from Discovery Service
        // Fetch address of Video Streaming Service from Discovery Service
        String baseURL = discUtil.getServiceAddress("VideoStreamingService");

        String destURI = baseURL + "/stream/video/titleAndDescription/" + String.valueOf(video.getVideoId());

        // Get list of videos with course IDs
        ResponseEntity<String> videoResponseEntity = restTemplate.exchange(destURI,
                HttpMethod.PUT,
                new HttpEntity(video, authHeader),
                String.class);

        // If JWT is invalid then redirect to login
        if (videoResponseEntity.getStatusCode() != HttpStatus.OK) {
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/login";
        }
        return "redirect:/video/upload";
    }

    @GetMapping("/video/upload")
    public String getVideoUploadPage(@CookieValue("jwt") String jwt, HttpServletResponse servletResponse, Model model){

        // Create Auth Header to send HTTP requests to other services
        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization", "Bearer " + jwt);

        // Checks if the subject is professor or not
        final boolean isProfessor = jwtUtil.extractRoles(jwt).contains("ROLE_PROFESSOR");
        model.addAttribute("isProfessor", isProfessor);
        final boolean isAdmin = jwtUtil.extractRoles(jwt).contains("ROLE_ADMIN");
        model.addAttribute("isAdmin", isAdmin);
        final boolean isStudent = jwtUtil.extractRoles(jwt).contains("ROLE_STUDENT");
        model.addAttribute("username", jwtUtil.extractUsername(jwt));
        model.addAttribute("isStudent", isStudent);

        // Extract Username from the JWT
        String userName = jwtUtil.extractUsername(jwt);

        // Fetch address of User Info Service from Discovery Service
        String baseAddr = "";
        try {
            baseAddr = discUtil.getServiceAddress("UserInformation");
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        String destURI = baseAddr + "/course/professor/" + userName;

        List<Course> courses = courseInfoUN.getAllCourseFromProfUN(userName,
                destURI, authHeader).getList();

        // If JWT is invalid then redirect to login
        if(courses == null){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/video/upload";
        }

        model.addAttribute("courses", courses);

        // Fetch address of Video Streaming Service from Discovery Service
        baseAddr = discUtil.getServiceAddress("VideoStreamingService");

        List<VideoDetails> videos = new ArrayList<>();
        for(Course course: courses) {

            // Send GET request to Video Streaming Service with Auth header and courseID
            destURI = baseAddr + "/stream/course/" + course.getCourseId() + "/videos";

            // Get list of videos with course IDs
            ResponseEntity<VideoList> videoResponseEntity = restTemplate.exchange(destURI,
                    HttpMethod.GET,
                    new HttpEntity(authHeader),
                    VideoList.class);

            // If JWT is invalid then redirect to login
            if (videoResponseEntity.getStatusCode() != HttpStatus.OK) {
                Cookie cookie = new Cookie("jwt", null);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(0);
                servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
                servletResponse.addCookie(cookie);
                return "redirect:/login";
            }
            for(Video video: videoResponseEntity.getBody().getVideoList()){
                videos.add(new VideoDetails(video.getVideoId(),
                        video.getVideoTitle(), video.getVideoDescription(),
                        course));
            }
        }
        model.addAttribute("videos", videos);
        model.addAttribute("uploadEndpoint", "http://" + UiServiceApplication.getHostIP() + ":5000/upload/video/submit");
        return "videoUpload";
    }

    @GetMapping(value = "/video/remove")
    public String deleteVideoById(@CookieValue("jwt") String jwt, HttpServletResponse servletResponse,
                                  Model model, long id){

        // Create Auth Header to send HTTP requests to other services
        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization", "Bearer " + jwt);

        // Fetch address of Video Streaming Service from Discovery Service
        String baseURL = discUtil.getServiceAddress("VideoStreamingService");

        String destURI = baseURL + "/stream/video/" + String.valueOf(id);

        // Get video with given videoId
        ResponseEntity<String> videoResponseEntity = restTemplate.exchange(destURI,
                HttpMethod.DELETE,
                new HttpEntity(authHeader),
                String.class);

        // If JWT is invalid then redirect to login
        if (videoResponseEntity.getStatusCode() != HttpStatus.OK) {
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return null;
        }
        return "redirect:/video/upload";
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleMissingCookies(MissingRequestCookieException ex, Model model) {
        return "redirect:/login";
    }
}

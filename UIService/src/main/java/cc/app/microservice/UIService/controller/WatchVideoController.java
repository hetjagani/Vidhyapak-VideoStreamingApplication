package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.UiServiceApplication;
import cc.app.microservice.UIService.model.Video;
import cc.app.microservice.UIService.model.VideoList;
import cc.app.microservice.UIService.model.VideoResolution;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import cc.app.microservice.UIService.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/video")
public class WatchVideoController {

    @Autowired
    private DiscoveryUtil discUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/watch")
    public String getWatchVideoPage(String id, Model model, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        // Get information about video that is playing
        String baseURL = discUtil.getServiceAddress("VideoStreamingService");
        String getVideoURL = baseURL + "/stream/video/" + id + "/details";

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

        ResponseEntity<Video> response = restTemplate.exchange(getVideoURL, HttpMethod.GET, new ResponseEntity<>(authHeader, HttpStatus.OK), Video.class);

        // If JWT is invalid then redirect to login
        if(response.getStatusCode() != HttpStatus.OK){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/login";
        }

        // Fetched video that is to be played
        Video nowPlayingVideo = response.getBody();

        // Fetch all the other videos of this course to display in bottom
        getVideoURL = baseURL + "/stream/course/" + nowPlayingVideo.getCourseId() + "/videos";

        ResponseEntity<VideoList> listResponse = restTemplate.exchange(getVideoURL, HttpMethod.GET,
                new ResponseEntity<>(authHeader, HttpStatus.OK), VideoList.class);

        // If JWT is invalid then redirect to login
        if(response.getStatusCode() != HttpStatus.OK){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/login";
        }

        // Fetched the list of videos that are to be displayed
        List<Video> videoList = listResponse.getBody().getVideoList();
        System.out.println(videoList.toString());

        // Creating source strings of the currently playing video
        Map<String, VideoResolution> nowPlayingVideoSource = new HashMap<String, VideoResolution>();
        for(VideoResolution v: nowPlayingVideo.getVideoResolutions()) {
            nowPlayingVideoSource.put( "http://" + UiServiceApplication.getHostIP() + ":554/stream/video/" + id + "/res/" + v.getVideoHeight(), v);
        }
        System.out.println(nowPlayingVideoSource.toString());

        model.addAttribute("video", nowPlayingVideo);
        model.addAttribute("videoList", videoList);
        if(!nowPlayingVideoSource.isEmpty())
            model.addAttribute("sources", nowPlayingVideoSource);

        return "watchVideopage";
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleMissingCookies(MissingRequestCookieException ex, Model model) {
        return "redirect:/login";
    }

}

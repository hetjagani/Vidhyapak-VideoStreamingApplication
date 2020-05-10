package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.model.Course;
import cc.app.microservice.UIService.model.Video;
import cc.app.microservice.UIService.model.VideoList;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import cc.app.microservice.UIService.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DiscoveryUtil discUtil;

    @GetMapping("/videos")
    public String searchVideos(String searchStr, Model model, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String baseURL = discUtil.getServiceAddress("VideoStreamingService");
        String getVideoURL = baseURL + "/stream/video/search?search=" + searchStr;

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

        ResponseEntity<VideoList> response = restTemplate.exchange(getVideoURL, HttpMethod.GET,
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

        List<Video> videoList = response.getBody().getVideoList();

        Map<Video, Course> videoMap = new HashMap<Video, Course>();

        String userInfoURL = discUtil.getServiceAddress("UserInformation");
        for(Video v: videoList) {
            // Get the course object
            String getCourseURL = userInfoURL + "/course/id/" + v.getCourseId();

            ResponseEntity<Course> cres = restTemplate.exchange(getCourseURL, HttpMethod.GET,
                    new ResponseEntity<>(authHeader, HttpStatus.OK), Course.class);

            // If JWT is invalid then redirect to login
            if(cres.getStatusCode() != HttpStatus.OK){
                Cookie cookie = new Cookie("jwt", null);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(0);
                servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
                servletResponse.addCookie(cookie);
                return "redirect:/login";
            }

            videoMap.put(v, cres.getBody());
        }

        model.addAttribute("videoList", videoMap);

        return "searchresults";
    }
}

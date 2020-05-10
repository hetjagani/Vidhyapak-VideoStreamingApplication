package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.model.Course;
import cc.app.microservice.UIService.model.ProfessorList;
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
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/video")
public class VideopageController {

   @Autowired
   private JwtUtil jwtUtil;

   @Autowired
   private DiscoveryUtil discUtil;

   @Autowired
   private RestTemplate restTemplate;

   @GetMapping("/course")
   public String getVideosOfCourse(String id, Model model, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse){

      String baseURL = discUtil.getServiceAddress("VideoStreamingService");
      String getVideoURL = baseURL + "/stream/course/" + id + "/videos";

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

      model.addAttribute("videos", videoList);

      return "videopage";

   }

   @GetMapping("/one")
   @ResponseBody
   public Video getVideo(String id, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

      String baseURL = discUtil.getServiceAddress("VideoStreamingService");
      String getVideoURL = baseURL + "/stream/video/" + id + "/details";

      HttpHeaders authHeader = new HttpHeaders();
      authHeader.add("Authorization", "Bearer " + jwt);

      ResponseEntity<Video> response = restTemplate.exchange(getVideoURL, HttpMethod.GET,
              new ResponseEntity<>(authHeader, HttpStatus.OK), Video.class);

      // If JWT is invalid then redirect to login
      if(response.getStatusCode() != HttpStatus.OK){
         Cookie cookie = new Cookie("jwt", null);
         cookie.setPath("/");
         cookie.setHttpOnly(true);
         cookie.setMaxAge(0);
         servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
         servletResponse.addCookie(cookie);
         return null;
      }

      return response.getBody();
   }

   @ExceptionHandler(MissingRequestCookieException.class)
   public String handleMissingCookies(MissingRequestCookieException ex, Model model) {
      return "redirect:/login";
   }

}

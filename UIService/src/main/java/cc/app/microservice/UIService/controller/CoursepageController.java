package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.model.Course;
import cc.app.microservice.UIService.service.CourseInfoWithID;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import cc.app.microservice.UIService.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/course")
public class CoursepageController {

    @Autowired
    private DiscoveryUtil discUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CourseInfoWithID courseInfo;

    @GetMapping("/professor")
    public String getCourseOfProfessor(String id, Model model, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String baseAddr = "";
        try {
            baseAddr = discUtil.getServiceAddress("UserInformation");
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        String getCourses = baseAddr + "/course/professor?id=" + id;

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

        List<Course> courseList = courseInfo.getAllCourseFromProfId(id, getCourses, authHeader).getList();

        // If JWT is invalid then redirect to login
        if(courseList == null){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/login";
        }

        model.addAttribute("courses", courseList);

        return "coursepage";
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleMissingCookies(MissingRequestCookieException ex, Model model) {
        return "redirect:/login";
    }

}

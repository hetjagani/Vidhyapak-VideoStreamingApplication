package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.model.Course;
import cc.app.microservice.UIService.model.CourseList;
import cc.app.microservice.UIService.model.DiscoveryInfo;
import cc.app.microservice.UIService.model.Professor;
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
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("courses/")
public class AddCourseController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryUtil discUtil;

    @GetMapping("/all")
    public String getAddCoursePage(Model model, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String username = jwtUtil.extractUsername(jwt);

        String baseAddr = discUtil.getServiceAddress("UserInformation");
        String proffCoursesURI = baseAddr + "/course/professor/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        // Checks if the subject is professor or not
        final boolean isProfessor = jwtUtil.extractRoles(jwt).contains("ROLE_PROFESSOR");
        model.addAttribute("isProfessor", isProfessor);
        final boolean isAdmin = jwtUtil.extractRoles(jwt).contains("ROLE_ADMIN");
        model.addAttribute("isAdmin", isAdmin);
        final boolean isStudent = jwtUtil.extractRoles(jwt).contains("ROLE_STUDENT");
        model.addAttribute("username", jwtUtil.extractUsername(jwt));
        model.addAttribute("isStudent", isStudent);

        ResponseEntity<CourseList> response = restTemplate.exchange(proffCoursesURI, HttpMethod.GET, new ResponseEntity<>(headers, HttpStatus.OK), CourseList.class);

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

        List<Course> courseList = response.getBody().getList();

        model.addAttribute("courses", courseList);
        return "addCourse";
    }

    @GetMapping("/one")
    @ResponseBody
    public Optional<Course> getOne(String id, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String baseAddr = discUtil.getServiceAddress("UserInformation");

        String courseURI = baseAddr + "/course/id/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        ResponseEntity<Course> response = restTemplate.exchange(courseURI, HttpMethod.GET,
                new ResponseEntity<>(headers, HttpStatus.OK),
                Course.class);

        // If JWT is invalid then redirect to login
        if(response.getStatusCode() != HttpStatus.OK){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return Optional.empty();
        }
        return Optional.of(
                response.getBody()
        );
    }

    @PostMapping("/add")
    public String addCourse(Course c, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String username = jwtUtil.extractUsername(jwt);

        String baseAddr = discUtil.getServiceAddress("UserInformation");

        String getProfessorURI = baseAddr + "/professor/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        ResponseEntity<Professor> response = restTemplate.exchange(getProfessorURI, HttpMethod.GET,
                new ResponseEntity<>(headers, HttpStatus.OK),
                Professor.class);

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
        c.setProfessor(response.getBody());

        String saveCourseURI = baseAddr + "/course/save";
        ResponseEntity<String> res = restTemplate.exchange(saveCourseURI, HttpMethod.POST,
                new ResponseEntity<Course>(c, headers, HttpStatus.OK), String.class);

        // If JWT is invalid then redirect to login
        if(res.getStatusCode() != HttpStatus.OK){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/login";
        }
        System.out.println(res.getBody());
        return "redirect:/courses/all";
    }

    @RequestMapping(path = "/update", method = {RequestMethod.PUT, RequestMethod.GET})
    public String updateCourse(Course c, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String username = jwtUtil.extractUsername(jwt);

        String baseAddr = discUtil.getServiceAddress("UserInformation");

        String getProfessorURI = baseAddr + "/professor/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        ResponseEntity<Professor> response = restTemplate.exchange(getProfessorURI, HttpMethod.GET,
                new ResponseEntity<>(headers, HttpStatus.OK),
                Professor.class);

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
        c.setProfessor(response.getBody());

        String saveCourseURI = baseAddr + "/course/update";
        ResponseEntity<String> res = restTemplate.exchange(saveCourseURI, HttpMethod.POST,
                new ResponseEntity<Course>(c, headers, HttpStatus.OK), String.class);

        // If JWT is invalid then redirect to login
        if(res.getStatusCode() != HttpStatus.OK){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/login";
        }
        System.out.println(res.getBody());

        System.out.println("Updated" + c.toString());
        return "redirect:/courses/all";
    }

    @RequestMapping(path = "/remove", method = {RequestMethod.DELETE, RequestMethod.GET})
    public String deleteCourse(String id, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String baseAddr = discUtil.getServiceAddress("UserInformation");

        String deleteURI = baseAddr + "/course/remove?id=" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        ResponseEntity<String> res = restTemplate.exchange(deleteURI, HttpMethod.DELETE,
                new ResponseEntity<Course>(headers, HttpStatus.OK), String.class);

        // If JWT is invalid then redirect to login
        if(res.getStatusCode() != HttpStatus.OK){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/login";
        }
        System.out.println(res.getBody());
        return "redirect:/courses/all";
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleMissingCookies(MissingRequestCookieException ex, Model model) {
        return "redirect:/login";
    }
}

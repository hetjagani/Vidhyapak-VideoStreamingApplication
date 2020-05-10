package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.model.Course;
import cc.app.microservice.UIService.model.CourseList;
import cc.app.microservice.UIService.model.Professor;
import cc.app.microservice.UIService.model.ProfessorList;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import cc.app.microservice.UIService.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/professors")
public class AddProfessorController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryUtil discUtil;

    @GetMapping("/all")
    public String getAllProfessors(Model model, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String username = jwtUtil.extractUsername(jwt);

        String baseAddr = discUtil.getServiceAddress("UserInformation");
        String proffURI = baseAddr + "/professor/all";

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

        ResponseEntity<ProfessorList> response = restTemplate.exchange(proffURI, HttpMethod.GET, new ResponseEntity<>(headers, HttpStatus.OK), ProfessorList.class);

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

        List<Professor> proffList = response.getBody().getList();

        model.addAttribute("professors", proffList);
        return "addProfessor";
    }

    @GetMapping("/one")
    @ResponseBody
    public Optional<Professor> getOneProfessor(String username, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String baseAddr = discUtil.getServiceAddress("UserInformation");

        String proffURL = baseAddr + "/professor/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        ResponseEntity<Professor> response = restTemplate.exchange(proffURL, HttpMethod.GET,
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
            return Optional.empty();
        }
        return Optional.of(
                response.getBody()
        );
    }

    @PostMapping("/add")
    public String saveProfessor(Professor pr, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String baseAddr = discUtil.getServiceAddress("UserInformation");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        String saveProffURL = baseAddr + "/professor/save";
        ResponseEntity<String> res = restTemplate.exchange(saveProffURL, HttpMethod.POST,
                new ResponseEntity<Professor>(pr, headers, HttpStatus.OK), String.class);

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
        return "redirect:/professors/all";
    }

    @RequestMapping(path = "/update", method = {RequestMethod.PUT, RequestMethod.GET})
    public String updateProfessor(Professor pr, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String baseAddr = discUtil.getServiceAddress("UserInformation");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        String saveProffURL = baseAddr + "/professor/update";
        ResponseEntity<String> res = restTemplate.exchange(saveProffURL, HttpMethod.PUT,
                new ResponseEntity<Professor>(pr, headers, HttpStatus.OK), String.class);

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
        return "redirect:/professors/all";
    }

    @RequestMapping(path = "/remove", method = {RequestMethod.DELETE, RequestMethod.GET})
    public String deleteProfessor(String username, String id, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String baseAddr = discUtil.getServiceAddress("UserInformation");

        String deleteURI = baseAddr + "/professor/remove?username=" + username + "&id=" + id;

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
        return "redirect:/professors/all";
    }
}

package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.model.Student;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import cc.app.microservice.UIService.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class StudentAccountController {

    @Autowired
    private DiscoveryUtil discUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/create")
    public String createAccount(boolean usernameExist,boolean notvalid, Model model) {

        model.addAttribute("usernameExist", usernameExist);
        model.addAttribute("notvalid", notvalid);

        return "createAccount";
    }

    @PostMapping("/save")
    public String saveStudent(Student s) {

        //If the username of student already exist then add goto:/account/create?usernameExist=true
        String infoServiceURL = discUtil.getServiceAddress("UserInformation");

        String userExistUrl = infoServiceURL + "/person/usernameExist?username=" + s.getPersonUserName();

        ResponseEntity<Boolean> response = restTemplate.exchange(userExistUrl, HttpMethod.GET, HttpEntity.EMPTY, Boolean.class);
        if(response.getBody())
            return "redirect:/account/create?usernameExist=true";

        if(!s.valid())
            return "redirect:/account/create?notvalid=true";

        //Add the account information through user-info service
        String saveUserURL = infoServiceURL + "/student/save";

        ResponseEntity<String> saveRes = restTemplate.exchange(saveUserURL, HttpMethod.POST, new HttpEntity<>(s), String.class);

        if(response.getStatusCode() == HttpStatus.OK && saveRes.getBody().equals("Saved"))
            return "redirect:/login?usersaved=true";

        System.out.println(s.toString());
        return "redirect:/account/create";
    }

    @GetMapping("/update")
    public String updateStudentPage(String username, Model model, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        // Checks if the subject is professor or not
        final boolean isProfessor = jwtUtil.extractRoles(jwt).contains("ROLE_PROFESSOR");
        model.addAttribute("isProfessor", isProfessor);
        final boolean isAdmin = jwtUtil.extractRoles(jwt).contains("ROLE_ADMIN");
        model.addAttribute("isAdmin", isAdmin);
        final boolean isStudent = jwtUtil.extractRoles(jwt).contains("ROLE_STUDENT");
        model.addAttribute("username", jwtUtil.extractUsername(jwt));
        model.addAttribute("isStudent", isStudent);

        String userInfoURL = discUtil.getServiceAddress("UserInformation");
        String getStudentURL = userInfoURL + "/student/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        ResponseEntity<Student> response = restTemplate.exchange(getStudentURL, HttpMethod.GET, new HttpEntity<>(headers), Student.class);

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
        Student stu = response.getBody();

        Date date = stu.getPersonDOB();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String yyyymmdd = format.format(date);

        model.addAttribute("dob", yyyymmdd);

        if(stu.getPersonGender().equals("male")) {
            model.addAttribute("isMale", true);
        }else {
            model.addAttribute("isFemale", true);
        }

        model.addAttribute("stu", stu);

        return "updateAccount";
    }

    @PostMapping("/update")
    public String updateAccount(Student s, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String userInfoURL = discUtil.getServiceAddress("UserInformation");
        String updateURL = userInfoURL + "/student/update";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        ResponseEntity<String> response = restTemplate.exchange(updateURL, HttpMethod.PUT, new HttpEntity<>(s, headers), String.class);

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

        return "redirect:/account/update?username=" + s.getPersonUserName();
    }
}

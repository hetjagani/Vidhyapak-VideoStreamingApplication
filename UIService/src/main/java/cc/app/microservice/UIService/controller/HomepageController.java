package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.model.Professor;
import cc.app.microservice.UIService.model.ProfessorList;
import cc.app.microservice.UIService.service.ProfessorsInfo;
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
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class HomepageController {

    @Autowired
    private DiscoveryUtil discUtil;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private ProfessorsInfo profInfo;

    @GetMapping("/homepage")
    public String getHomepage(Model model, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {
        String baseAddr = "";
        try {
            baseAddr = discUtil.getServiceAddress("UserInformation");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String getAllProffURI = baseAddr + "/professor/all";

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


        List<Professor> professorList = profInfo.getAllProfessors(getAllProffURI, authHeader).getList();

        // If JWT is invalid then redirect to login
        if(professorList == null){
            Cookie cookie = new Cookie("jwt", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            servletResponse.addCookie(cookie);
            return "redirect:/login";
        }
        model.addAttribute("professors", professorList);
        return "homepage";
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleMissingCookies(MissingRequestCookieException ex, Model model) {
        return "redirect:/login";
    }
}

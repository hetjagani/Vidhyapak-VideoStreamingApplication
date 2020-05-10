package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.model.AuthenticationRequest;
import cc.app.microservice.UIService.model.AuthenticationResponse;
import cc.app.microservice.UIService.model.DiscoveryInfo;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Controller
public class LoginScreenController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryUtil discUtil;

    @GetMapping("/login")
    public String getLoginForm(boolean usersaved, boolean invalidCred, Model model){

        model.addAttribute("usersaved", usersaved);
        model.addAttribute("invalid", invalidCred);

        return "loginForm";
    }

    @PostMapping("/login")
    public String getLoginForm(HttpServletRequest request, HttpServletResponse servletResponse){
        AuthenticationRequest authRequest = new AuthenticationRequest(
                request.getParameter("username"),
                request.getParameter("password"));

        String baseAddr = discUtil.getServiceAddress("AuthenticationService");

        try{
            String authServURI = baseAddr + "/authentication/authenticate";

            ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(
                    authServURI,
                    authRequest,
                    AuthenticationResponse.class);

            if(response.getStatusCode() == HttpStatus.OK) {
                Cookie cookie = new Cookie("jwt", response.getBody().getJwt());
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
                servletResponse.addCookie(cookie);
                return "redirect:/homepage";
            }
            else {
                return "redirect:/login";
            }
        }
        catch (Exception e){
            return "redirect:/login?invalidCred=true";
        }
    }

    @GetMapping("/logout")
    public String deleteCookie(HttpServletResponse servletResponse, Model model){
        Cookie cookie = new Cookie("jwt", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        servletResponse.addCookie(cookie);
        return "redirect:/login";
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleMissingCookies(MissingRequestCookieException ex, Model model) {
        return "redirect:/login";
    }
}

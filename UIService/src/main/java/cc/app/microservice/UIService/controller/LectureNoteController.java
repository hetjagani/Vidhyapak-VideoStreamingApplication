package cc.app.microservice.UIService.controller;

import cc.app.microservice.UIService.UiServiceApplication;
import cc.app.microservice.UIService.model.LectureNote;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/lecturenote")
public class LectureNoteController {

    @Autowired
    private DiscoveryUtil discUtil;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/download")
    @ResponseBody
    public String download(String id, @CookieValue("jwt") String jwt, HttpServletResponse servletResponse) {

        String downloadLink = "http://" + UiServiceApplication.getHostIP() + ":554/stream/lectureNote/" + id + "/download";

        return downloadLink;
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public String handleMissingCookies(MissingRequestCookieException ex, Model model) {
        return "redirect:/login";
    }
}

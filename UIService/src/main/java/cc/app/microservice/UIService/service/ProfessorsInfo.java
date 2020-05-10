package cc.app.microservice.UIService.service;

import cc.app.microservice.UIService.model.Professor;
import cc.app.microservice.UIService.model.ProfessorList;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;

import java.util.Arrays;

@Service
public class ProfessorsInfo {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Jedis jedis;

    @Autowired
    private Gson gson;

    @HystrixCommand(fallbackMethod = "getFallBackProfList", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")})
    public ProfessorList getAllProfessors(String uri, HttpHeaders authHeader){
        ResponseEntity<ProfessorList> response = restTemplate.exchange(uri, HttpMethod.GET,
                new ResponseEntity<>(authHeader, HttpStatus.OK), ProfessorList.class);

        // If JWT is invalid then redirect to login
        if(response.getStatusCode() != HttpStatus.OK){
            return null;
        }
        ProfessorList professorList = new ProfessorList(response.getBody().getList());

        jedis.set("ui-professor-list",gson.toJson(professorList));

        return professorList;
    }

    public ProfessorList getFallBackProfList(String uri, HttpHeaders authHeader) {
        if(jedis.exists("ui-professor-list")){
            return gson.fromJson(jedis.get("ui-professor-list"), ProfessorList.class);
        }
        else{
            Professor professor = new Professor();
            professor.setPersonFirstName("Not");
            professor.setPersonLastName("Found");
            return new ProfessorList(Arrays.asList(professor));
        }
    }
}

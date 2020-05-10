package cc.app.microservice.UIService.service;

import cc.app.microservice.UIService.model.Course;
import cc.app.microservice.UIService.model.CourseList;
import cc.app.microservice.UIService.model.Professor;
import cc.app.microservice.UIService.model.ProfessorList;
import com.google.gson.Gson;
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

import java.util.Arrays;

@Service
public class CourseInfoWithID {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Jedis jedis;

    @Autowired
    private Gson gson;

    @HystrixCommand(fallbackMethod = "getFallBackCourseListFromProfId", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")})
    public CourseList getAllCourseFromProfId(String profId, String uri, HttpHeaders authHeader){
        ResponseEntity<CourseList> response = restTemplate.exchange(uri, HttpMethod.GET,
                new ResponseEntity<>(authHeader, HttpStatus.OK), CourseList.class);

        // If JWT is invalid then redirect to login
        if(response.getStatusCode() != HttpStatus.OK){
            return null;
        }
        CourseList courseList = new CourseList(response.getBody().getList());

        jedis.set("ui-courseList-from-profId::" + profId, gson.toJson(courseList));

        return courseList;
    }

    public CourseList getFallBackCourseListFromProfId(String profId, String uri, HttpHeaders authHeader){
        String key = "ui-courseList-from-profId::" + profId;
        if(jedis.exists(key)){
            return gson.fromJson(jedis.get(key), CourseList.class);
        }
        else{
            Course course = new Course();
            course.setCourseId("-1");
            course.setCourseName("Not found");
            return new CourseList(Arrays.asList(course));
        }
    }

}

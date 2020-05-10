package cc.app.microservice.UIService.service;

import cc.app.microservice.UIService.model.Course;
import cc.app.microservice.UIService.model.CourseList;
import com.google.gson.Gson;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

@Service
public class CourseInfoWithUserName {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Jedis jedis;

    @Autowired
    private Gson gson;

    @HystrixCommand(fallbackMethod = "getFallBackCourseListFromProfUN", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")})
    public CourseList getAllCourseFromProfUN(String profUserName, String uri, HttpHeaders authHeader){

        // Send GET request to User Info Service with Auth header and username
        // Get list of courses with username extracted from JWT
        ResponseEntity<CourseList> courseResponseEntity = restTemplate.exchange(uri,
                HttpMethod.GET,
                new HttpEntity(authHeader),
                CourseList.class);

        // If JWT is invalid then redirect to login
        if(courseResponseEntity.getStatusCode() != HttpStatus.OK){
            return null;
        }
        CourseList courseList = new CourseList(courseResponseEntity.getBody().getList());

        jedis.set("ui-courseList-from-profUserName::" + profUserName, gson.toJson(courseList));

        return courseList;
    }

    public CourseList getFallBackCourseListFromProfUN(String profUserName, String uri, HttpHeaders authHeader){
        String key = "ui-courseList-from-profUserName::" + profUserName;
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

package com.gyapak.userinformation.controller;

import com.gyapak.userinformation.model.Course;
import com.gyapak.userinformation.model.CourseList;
import com.gyapak.userinformation.model.Person;
import com.gyapak.userinformation.model.Professor;
import com.gyapak.userinformation.util.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Null;
import java.util.List;

@RestController
@RequestMapping(path = "/course")
public class CourseServiceController {
    @Autowired
    private CourseRepository courseRepo;

    @GetMapping(path = "/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepo.getAll());
    }

    @GetMapping(path = "/code/{courseCode}")
    public ResponseEntity<Course> getCourseFromCode(@PathVariable String courseCode) {
        try{
            return ResponseEntity.ok(courseRepo.getCoursefromCourseCode(courseCode).get());
        }catch (NullPointerException nullEx) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Code Not found");
        }
    }

    @GetMapping(path = "/id/{courseId}")
    public ResponseEntity<Course> getCourseFromId(@PathVariable String courseId) {
        try{
            return ResponseEntity.ok(courseRepo.getCourseFromCourseId(courseId).get());
        }catch (NullPointerException nullEx) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Code Not found");
        }
    }

    @GetMapping(path = "/professor/{username}")
    public ResponseEntity<CourseList> getCoursesOfProfessor(@PathVariable String username) {
        try{
            return ResponseEntity.ok(new CourseList(courseRepo.getCourseOfProfessor(username)));
        }catch (NullPointerException nullEx) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This professor username is invalid");
        }
    }

    @GetMapping(path = "/professor")
    public ResponseEntity<CourseList> getCoursesOfProfessorFromId(String id) {
        try{
            return ResponseEntity.ok(new CourseList(courseRepo.getCourseOfProfessorId(id)));
        }catch (NullPointerException nullEx) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This professor username is invalid");
        }
    }

    @PostMapping(path = "/save")
    public String saveCourse(@RequestBody Course course) {

        if(courseRepo.save(course, course.getProfessor().getPersonUserName()) != 0){
            return "Saved";
        }else{
            return "Not Saved";
        }
    }

    @GetMapping(path = "{id}/professor")
    public ResponseEntity<String> getProffessorFromCourseId(@PathVariable("id") Long courseId){
        String email = courseRepo.getProffessorFromCourseId(courseId);
        if(email != null){
            return ResponseEntity.ok(email);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This courseId is invalid");
        }
    }

    @PostMapping(path = "/update")
    public String updateCourse(@RequestBody Course course) {

        if(courseRepo.update(course, course.getProfessor().getPersonUserName()) != 0){
            return "Updated";
        }else{
            return "Not Updated";
        }
    }

    @DeleteMapping(path = "/remove")
    public String deleteCourse(String id) {
       if(courseRepo.delete(id) != 0){
           return "Deleted";
       }else {
           return "Not Deleted";
       }
    }
}

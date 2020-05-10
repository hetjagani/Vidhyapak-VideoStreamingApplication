package com.gyapak.userinformation.util;


import com.gyapak.userinformation.model.Course;
import com.gyapak.userinformation.model.Person;
import com.gyapak.userinformation.model.Professor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.swing.text.html.Option;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepository {

    @Resource(name = "userInfoJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    ProfessorRepository proffRepo;

    public List<Course> getAll() {
        return jdbcTemplate.query("SELECT * FROM Course;",
            (rs, i) -> {
                String proffId = rs.getString("professorId");
                Professor proff = null;
                try {
                    proff = proffRepo.getProfessorFromId(proffId).get();
                }catch (NullPointerException nullEx){
                    return null;
                }


                return new Course(
                        rs.getString("courseId"),
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        Year.of(rs.getInt("courseYear")),
                        rs.getString("courseSemester"),
                        rs.getString("courseType"),
                        proff
                );
        });
    }

    @Cacheable(value = "user-info-course-from-courseCode", key = "#code")
    public Optional<Course> getCoursefromCourseCode(String code) {
        String sql = "SELECT * FROM Course WHERE courseCode = ?;";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{code},
                    (rs, i) -> {
                        String proffId = rs.getString("professorId");
                        Professor proff = null;
                        try {
                            proff = proffRepo.getProfessorFromId(proffId).get();
                        } catch (NullPointerException nullEx) {
                            return null;
                        }


                        return Optional.of(new Course(
                                rs.getString("courseId"),
                                rs.getString("courseCode"),
                                rs.getString("courseName"),
                                Year.of(rs.getInt("courseYear")),
                                rs.getString("courseSemester"),
                                rs.getString("courseType"),
                                proff
                        ));
                    });
        }catch(EmptyResultDataAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Cacheable(value = "user-info-course-from-courseId", key = "#Id")
    public Optional<Course> getCourseFromCourseId(String Id) {
        String sql = "SELECT * FROM Course WHERE courseId = ?;";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{Integer.parseInt(Id)},
                    (rs, i) -> {
                        String proffId = rs.getString("professorId");
                        Professor proff = null;
                        try {
                            proff = proffRepo.getProfessorFromId(proffId).get();
                        } catch (NullPointerException nullEx) {
                            return null;
                        }


                        return Optional.of(new Course(
                                rs.getString("courseId"),
                                rs.getString("courseCode"),
                                rs.getString("courseName"),
                                Year.of(rs.getInt("courseYear")),
                                rs.getString("courseSemester"),
                                rs.getString("courseType"),
                                proff
                        ));
                    });
        }catch(EmptyResultDataAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Cacheable(value = "user-info-courses-from-profUsername", key = "#username")
    public List<Course> getCourseOfProfessor(String username) {
        String sql = "SELECT * FROM Course join Professor USING(professorId) WHERE personUserName = ?;";
        return jdbcTemplate.query(sql,
                new Object[]{username},
                (rs, i) -> {
                    String proffId = rs.getString("professorId");
                    Professor proff = null;
                    try {
                        proff = proffRepo.getProfessorFromId(proffId).get();
                    }catch (NullPointerException nullEx){
                        return null;
                    }


                    return new Course(
                            rs.getString("courseId"),
                            rs.getString("courseCode"),
                            rs.getString("courseName"),
                            Year.of(rs.getInt("courseYear")),
                            rs.getString("courseSemester"),
                            rs.getString("courseType"),
                            proff
                    );
                });
    }

    @Cacheable(value = "user-info-courses-from-profId", key = "#id")
    public List<Course> getCourseOfProfessorId(String id) {
        String sql = "SELECT * FROM Course join Professor USING(professorId) WHERE professorId = ?;";
        return jdbcTemplate.query(sql,
                new Object[]{id},
                (rs, i) -> {
                    String proffId = rs.getString("professorId");
                    Professor proff = null;
                    try {
                        proff = proffRepo.getProfessorFromId(proffId).get();
                    }catch (NullPointerException nullEx){
                        return null;
                    }


                    return new Course(
                            rs.getString("courseId"),
                            rs.getString("courseCode"),
                            rs.getString("courseName"),
                            Year.of(rs.getInt("courseYear")),
                            rs.getString("courseSemester"),
                            rs.getString("courseType"),
                            proff
                    );
                });
    }

    public String getProffessorFromCourseId(Long id){
        try {
            String sql = "SELECT professorId from Course WHERE courseId=?";
            Long profId = jdbcTemplate.queryForObject(sql, new Object[]{id}, Long.class);
            System.out.println(profId);
            sql = "SELECT personUserName from Professor WHERE professorId=?";
            String personUserName = jdbcTemplate.queryForObject(sql, new Object[]{profId}, String.class);
            System.out.println(personUserName);
            sql = "SELECT personEmail from Person WHERE personUserName=?";
            return jdbcTemplate.queryForObject(sql, new Object[]{personUserName}, String.class);
        }
        catch (EmptyResultDataAccessException ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Caching(evict = { @CacheEvict(value = "user-info-courses-from-profId", allEntries = true),
                        @CacheEvict(value = "user-info-courses-from-profUsername", key = "#proffUserName")})
    public int save(Course course, String proffUserName) {

        try {
            Professor proff = proffRepo.getProfessorFromUserName(proffUserName).get();
            course.setProfessor(proff);
        }catch (NullPointerException nullEx) {
            // TODO: Return the error for professor not found
            nullEx.printStackTrace();
        }

        String sql = "INSERT INTO `UserInfo`.`Course` (`courseCode`, `courseName`, `courseYear`, `courseSemester`, `courseType`, `professorId`) " +
                "VALUES (?, ?, ?, ?, ?, ?);";

        return jdbcTemplate.update(sql,
                course.getCourseCode(),
                course.getCourseName(),
                Integer.parseInt(course.getCourseYear().toString()),
                course.getCourseSemester(),
                course.getCourseType(),
                course.getProfessor().getProfessorId()
                );
    }

    @Caching(evict = {  @CacheEvict(value = "user-info-course-from-courseId", key = "#course.courseId"),
                        @CacheEvict(value = "user-info-courses-from-profUsername", key = "#proffUserName"),
                        @CacheEvict(value = "user-info-course-from-courseCode", key = "#course.courseCode"),
                        @CacheEvict(value = "user-info-courses-from-profId", allEntries = true)})
    public int update(Course course, String proffUserName) {

        try {
            Professor proff = proffRepo.getProfessorFromUserName(proffUserName).get();
            course.setProfessor(proff);
        }catch (NullPointerException nullEx) {
            // TODO: Return the error for professor not found
            nullEx.printStackTrace();
        }

        String sql = "UPDATE Course SET courseCode=?, courseName=?, courseSemester=?, courseType=?, " +
                "courseYear=? WHERE courseId=?";

        return jdbcTemplate.update(sql,
                course.getCourseCode(),
                course.getCourseName(),
                course.getCourseSemester(),
                course.getCourseType(),
                Integer.parseInt(course.getCourseYear().toString()),
                course.getCourseId()
        );
    }

    @Caching(evict = {  @CacheEvict(value = "user-info-course-from-courseId", key = "#id"),
            @CacheEvict(value = "user-info-courses-from-profUsername", allEntries = true),
            @CacheEvict(value = "user-info-course-from-courseCode", allEntries = true),
            @CacheEvict(value = "user-info-courses-from-profId", allEntries = true)})
    public int delete(String id) {
        String sql = "DELETE FROM Course WHERE courseId=?";

        return jdbcTemplate.update(sql, Integer.parseInt(id));
    }

}

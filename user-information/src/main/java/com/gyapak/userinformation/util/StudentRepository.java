package com.gyapak.userinformation.util;

import com.gyapak.userinformation.model.Person;
import com.gyapak.userinformation.model.Student;
import com.gyapak.userinformation.model.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

	@Autowired
    @Qualifier("userInfoJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

	@Autowired
    @Qualifier("userAuthJdbcTemplate")
    private JdbcTemplate authTemplate;

    public List<Student> getAll(){
        return jdbcTemplate.query("SELECT * FROM Person JOIN Student USING(personUserName);",
                (resultSet, i) -> {
                    return new Student(
                            resultSet.getString("personFirstName"),
                            resultSet.getString("personMiddleName"),
                            resultSet.getString("personLastName"),
                            resultSet.getDate("personDOB"),
                            resultSet.getString("personContactNum"),
                            resultSet.getString("personEmail"),
                            resultSet.getString("personGender"),
                            resultSet.getString("personUserName"),
                            resultSet.getString("studentEnrollNum"),
                            Year.of(resultSet.getInt("studentAdmitYear"))
                    );
                });
    }

    public Optional<Student> getStudentFromUserName(String username) {
        String sql = "SELECT * FROM Person JOIN Student USING(personUserName) WHERE personUserName = ?;";

        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{username},
                    (resultSet, i) -> {
                        return Optional.of(new Student(
                                resultSet.getString("personFirstName"),
                                resultSet.getString("personMiddleName"),
                                resultSet.getString("personLastName"),
                                resultSet.getDate("personDOB"),
                                resultSet.getString("personContactNum"),
                                resultSet.getString("personEmail"),
                                resultSet.getString("personGender"),
                                resultSet.getString("personUserName"),
                                resultSet.getString("studentEnrollNum"),
                                Year.of(resultSet.getInt("studentAdmitYear"))
                        ));
                    });
        }catch (EmptyResultDataAccessException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public int save(Student s, UserDetail ud) {
        String perSql = "INSERT INTO Person VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(perSql,
                s.getPersonFirstName(),
                s.getPersonMiddleName(),
                s.getPersonLastName(),
                s.getPersonDOB(),
                s.getPersonContactNumber(),
                s.getPersonEmail(),
                s.getPersonGender(),
                s.getPersonUserName()
        );

        String profSql = "INSERT INTO `UserInfo`.`Student` (`studentEnrollNum`, `studentAdmitYear`, `personUserName`) " +
                "VALUES (?, ?, ?);";

        jdbcTemplate.update(profSql,
                s.getStudentEnrollNum(),
                Integer.parseInt(s.getStudentAdmitYear().toString()),
                s.getPersonUserName()
        );

        String authSql = "INSERT INTO `users` (`username`, `password`, `enabled`) " +
                "VALUES (?, ?, 1);";
        authTemplate.update(authSql,
                ud.getUsername(),
                ud.getPassword());

        for(GrantedAuthority a: ud.getAuthorities()) {
            String asql = "INSERT INTO `authorities` VALUES (?, ?);";

            authTemplate.update(asql, ud.getUsername(), a.getAuthority());
        }

        return 1;
    }

    public int update(Student p, UserDetail ud) {
        String perSql = "UPDATE Person SET `personFirstName` = ?, `personMiddleName` = ?, `personLastName` = ?, `personDOB` = ?, `personContactNum` = ?, `personEmail` = ?, `personGender` = ? WHERE (`personUserName` = ?);";
        jdbcTemplate.update(perSql,
                p.getPersonFirstName(),
                p.getPersonMiddleName(),
                p.getPersonLastName(),
                p.getPersonDOB(),
                p.getPersonContactNumber(),
                p.getPersonEmail(),
                p.getPersonGender(),
                p.getPersonUserName()
        );

        String stuUpdate = "UPDATE `UserInfo`.`Student` SET `studentEnrollNum` = ?, `studentAdmitYear` = ? WHERE (`studentEnrollNum` = ?);";
        jdbcTemplate.update(stuUpdate, p.getStudentEnrollNum(), p.getStudentAdmitYear().toString(), p.getStudentEnrollNum());

        if(!ud.getPassword().equals("") && ud.getPassword() != null){
            String authSql = "UPDATE users SET password = ? WHERE (username=?);";
            authTemplate.update(authSql,
                    ud.getPassword(),
                    ud.getUsername());
        }
        return 1;
    }
}

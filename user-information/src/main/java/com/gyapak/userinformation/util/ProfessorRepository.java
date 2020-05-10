package com.gyapak.userinformation.util;

import com.gyapak.userinformation.model.Person;
import com.gyapak.userinformation.model.Professor;
import com.gyapak.userinformation.model.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class ProfessorRepository {
	@Autowired
    @Qualifier("userInfoJdbcTemplate")
    JdbcTemplate jdbcTemplate;

	@Autowired
    @Qualifier("userAuthJdbcTemplate")
    private JdbcTemplate authTemplate;

    @Cacheable(value = "user-info-professors")
    public List<Professor> getAll() {
        return jdbcTemplate.query("SELECT * FROM Person JOIN Professor USING(personUserName);",
                (resultSet, i) -> {

                    return new Professor(resultSet.getString("personFirstName"),
                            resultSet.getString("personMiddleName"),
                            resultSet.getString("personLastName"),
                            resultSet.getDate("personDOB"),
                            resultSet.getString("personContactNum"),
                            resultSet.getString("personEmail"),
                            resultSet.getString("personGender"),
                            resultSet.getString("personUserName"),
                            resultSet.getString("professorId"),
                            resultSet.getString("professorOfficeNum"),
                            resultSet.getString("professorDepartment"),
                            resultSet.getString("professorDesignation")
                    );
                });
    }

    @Cacheable(value = "user-info-professor-from-id", key = "#id")
    public Optional<Professor> getProfessorFromId(String id) {

        String sql = "SELECT * FROM Person JOIN Professor USING(personUserName) WHERE professorId = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{id},
                    (resultSet, i) -> {

                        return Optional.of(new Professor(resultSet.getString("personFirstName"),
                                resultSet.getString("personMiddleName"),
                                resultSet.getString("personLastName"),
                                resultSet.getDate("personDOB"),
                                resultSet.getString("personContactNum"),
                                resultSet.getString("personEmail"),
                                resultSet.getString("personGender"),
                                resultSet.getString("personUserName"),
                                resultSet.getString("professorId"),
                                resultSet.getString("professorOfficeNum"),
                                resultSet.getString("professorDepartment"),
                                resultSet.getString("professorDesignation")
                        ));
                    });
        } catch (EmptyResultDataAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Cacheable(value = "user-info-professor-from-username", key = "#username")
    public Optional<Professor> getProfessorFromUserName(String username) {

        String sql = "SELECT * FROM Person JOIN Professor USING(personUserName) WHERE personUserName = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{username},
                    (resultSet, i) -> {

                        return Optional.of(new Professor(resultSet.getString("personFirstName"),
                                resultSet.getString("personMiddleName"),
                                resultSet.getString("personLastName"),
                                resultSet.getDate("personDOB"),
                                resultSet.getString("personContactNum"),
                                resultSet.getString("personEmail"),
                                resultSet.getString("personGender"),
                                resultSet.getString("personUserName"),
                                resultSet.getString("professorId"),
                                resultSet.getString("professorOfficeNum"),
                                resultSet.getString("professorDepartment"),
                                resultSet.getString("professorDesignation")
                        ));
                    });
        } catch (EmptyResultDataAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Caching(evict = { @CacheEvict(value = "user-info-professors", allEntries = true) })
    public int save(Professor p, UserDetail ud) {

        String perSql = "INSERT INTO Person VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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


        String profSql = "INSERT INTO `UserInfo`.`Professor` (`professorOfficeNum`, `professorDepartment`, `professorDesignation`, `personUserName`) " +
                "VALUES (?, ?, ?, ?);";
        jdbcTemplate.update(profSql,
                p.getProfessorOfficeNum(),
                p.getProfessorDepartment(),
                p.getProfessorDesignation(),
                p.getPersonUserName()
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

    @Caching(evict = {  @CacheEvict(value = "user-info-professor-from-username", key = "#p.personUserName"),
                        @CacheEvict(value = "user-info-professors", allEntries = true)
    })
    public int update(Professor p, UserDetail ud) {

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


        String profSql = "UPDATE Professor SET `professorOfficeNum` = ?, `professorDepartment` = ?, `professorDesignation` = ? WHERE (`professorId` = ?);";
        jdbcTemplate.update(profSql,
                p.getProfessorOfficeNum(),
                p.getProfessorDepartment(),
                p.getProfessorDesignation(),
                p.getProfessorId()
        );

        if(!ud.getPassword().equals("") && ud.getPassword() != null){
            String authSql = "UPDATE users SET password = ? WHERE (username=?);";
            authTemplate.update(authSql,
                    ud.getPassword(),
                    ud.getUsername());
        }
        return 1;
    }

    @Caching(evict = {  @CacheEvict(value = "user-info-professor-from-username", key = "#username"),
                        @CacheEvict(value = "user-info-professor-from-id", key = "#id"),
                        @CacheEvict(value = "user-info-professors", allEntries = true)
    })
    public int delete(String id ,String username) {
        String deleteUserAuthorities = "DELETE FROM authorities WHERE username=?;";
        String deleteUserPass = "DELETE FROM users WHERE username = ?;";
        authTemplate.update(deleteUserAuthorities, username);
        authTemplate.update(deleteUserPass, username);

        String deleteInfoProff = "DELETE FROM Professor WHERE (professorId = ?);";
        String deleteInfoPerson = "DELETE FROM Person WHERE (`personUserName` = ?);";
        jdbcTemplate.update(deleteInfoProff, id);
        jdbcTemplate.update(deleteInfoPerson, username);

        return 1;
    }
}

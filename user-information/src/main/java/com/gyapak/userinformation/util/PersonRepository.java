package com.gyapak.userinformation.util;

import com.gyapak.userinformation.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonRepository {

    @Autowired
    @Qualifier("userInfoJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public List<Person> getAll() {
        return jdbcTemplate.query("SELECT * FROM Person",
                (resultSet, i) -> {

                    return new Person(
                            resultSet.getString("personFirstName"),
                            resultSet.getString("personMiddleName"),
                            resultSet.getString("personLastName"),
                            resultSet.getDate("personDOB"),
                            resultSet.getString("personContactNum"),
                            resultSet.getString("personEmail"),
                            resultSet.getString("personGender"),
                            resultSet.getString("personUserName")
                    );
                });
    }

    public Optional<Person> getPersonFromUserName(String userName) {

        String sql = "SELECT * FROM Person WHERE personUserName = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{userName},
                    (resultSet, i) -> {

                        return Optional.of(new Person(
                                resultSet.getString("personFirstName"),
                                resultSet.getString("personMiddleName"),
                                resultSet.getString("personLastName"),
                                resultSet.getDate("personDOB"),
                                resultSet.getString("personContactNum"),
                                resultSet.getString("personEmail"),
                                resultSet.getString("personGender"),
                                resultSet.getString("personUserName")
                        ));
                    });

        } catch (EmptyResultDataAccessException ex){
            ex.printStackTrace();
        }
        return null;
    }
    public int save(Person p) {
        String sql = "INSERT INTO Person VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                p.getPersonFirstName(),
                p.getPersonMiddleName(),
                p.getPersonLastName(),
                p.getPersonDOB(),
                p.getPersonContactNumber(),
                p.getPersonEmail(),
                p.getPersonGender(),
                p.getPersonUserName()
        );
    }
}

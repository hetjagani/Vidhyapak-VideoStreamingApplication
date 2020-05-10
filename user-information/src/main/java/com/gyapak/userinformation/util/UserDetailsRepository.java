package com.gyapak.userinformation.util;

import com.gyapak.userinformation.model.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDetailsRepository {

    @Autowired
    @Qualifier("userAuthJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public UserDetail getUserfromUsername(String username) {
        String userSql = "SELECT * FROM users WHERE username=?";
        String authoritiesSql = "SELECT * FROM authorities WHERE username=?";

        UserDetail user = new UserDetail();
        try {
            jdbcTemplate.queryForObject(userSql,
                    new Object[]{username},
                    (rs, i) -> {
                        user.setUsername(username);
                        user.setPassword(rs.getString("password"));
                        user.setActive(rs.getBoolean("enabled"));
                        return user;
                    }
            );
        }catch (EmptyResultDataAccessException ex){
            ex.printStackTrace();
        }
        List<GrantedAuthority> a = new ArrayList<>();
        try {
            jdbcTemplate.query(authoritiesSql,
                    new Object[]{username},
                    (rs, i) -> {
                        a.add(new SimpleGrantedAuthority(rs.getString("authority")));
                        return a;
                    });
            user.setAuthorities(a);
        }catch (EmptyResultDataAccessException ex) {
            ex.printStackTrace();
        }

        //DEBUG PRINT
        System.out.println(user.toString());

        return user;
    }
}

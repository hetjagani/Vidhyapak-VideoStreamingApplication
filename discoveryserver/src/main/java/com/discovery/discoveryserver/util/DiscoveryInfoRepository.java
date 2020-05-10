package com.discovery.discoveryserver.util;

import com.discovery.discoveryserver.model.DiscoveryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DiscoveryInfoRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<DiscoveryInfo> getAll(){
        String sql = "SELECT * FROM DiscoveryInfo";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new DiscoveryInfo(rs.getString("ipAddress"),
                    rs.getString("serviceName"),
                    rs.getInt("servicePort"));
        });
    }

    public int save(DiscoveryInfo info) {
        String sql = "INSERT INTO `DiscoveryService`.`DiscoveryInfo` (`ipAddress`, `serviceName`, `servicePort`, `accessTime`) " +
                "VALUES (?, ?, ?, NOW());";
        return jdbcTemplate.update(sql,info.getIpAddr(), info.getServiceName(), info.getServicePort());
    }

    public int updateTimestamp(DiscoveryInfo info){
        String sql = "UPDATE DiscoveryInfo SET accessTime=now() WHERE ipAddress=? and serviceName=? and servicePort=?";
        return jdbcTemplate.update(sql, info.getIpAddr(), info.getServiceName(), info.getServicePort());
    }

    public Optional<DiscoveryInfo> getOldestDiscovery(String serviceName) {
        String sql = "SELECT * FROM DiscoveryService.DiscoveryInfo WHERE serviceName=? order by accessTime LIMIT 1;";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{serviceName},
                    (rs, i) -> {
                        return Optional.of(new DiscoveryInfo(rs.getString("ipAddress"), rs.getString("serviceName"), rs.getInt("servicePort")));
                    });
        } catch (EmptyResultDataAccessException ex){
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean isRegistered(DiscoveryInfo info) {
        List<DiscoveryInfo> allReg = getAll();
        for(DiscoveryInfo d: allReg) {
            if(info.equals(d))
                return true;
        }
        return false;
    }

    public int remove(DiscoveryInfo info) {
        String sql = "DELETE FROM DiscoveryInfo WHERE ipAddress=? and serviceName=? and servicePort=?;";
        return jdbcTemplate.update(sql, info.getIpAddr(), info.getServiceName(), info.getServicePort());
    }

}

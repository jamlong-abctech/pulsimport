package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.model.SiteModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SiteDao {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SiteDao.class);

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public SiteModel findByCode(String code)  {
        try {
            SiteModel siteModel = jdbcTemplate
                    .queryForObject("SELECT id, code, device, name FROM site WHERE code = ?", new Object[]{code},
                            new SiteRowMapper());
            return siteModel;
        } catch (EmptyResultDataAccessException e) {
            log.debug("SiteModel not found for site code {}", code);
        }

        return null;
    }

    public SiteModel findById(Long id)  {
        SiteModel siteModel = jdbcTemplate
                .queryForObject("SELECT id, code, device, name FROM site WHERE id = ?", new Object[]{id}, new SiteRowMapper());

        return siteModel;
    }


    public List<SiteModel> findAllSite(){
        return jdbcTemplate.query("SELECT id, code, device, name FROM site", new SiteRowMapper());
    }

    public List<SiteModel> findByDevice(SiteDeviceEnum device) {
        List<SiteModel> res = jdbcTemplate.query("SELECT id, code, device, name " +
                " FROM site WHERE device = ? ",
                new Object[]{device.toTextValue()}, new SiteRowMapper());

        return res;
    }


    private static class SiteRowMapper implements RowMapper<SiteModel> {

        @Override
        public SiteModel mapRow(ResultSet rs, int i) throws SQLException {
            SiteModel model = new SiteModel();
            model.setId(rs.getLong("id"));
            model.setCode(rs.getString("code"));
            model.setDevice(SiteDeviceEnum.fromText(rs.getString("device")));
            model.setName(rs.getString("name"));

            return model;
        }
    }



}

package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.model.SiteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SiteDao {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    public void setDataSource(DataSource ds) {
        dataSource = ds;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public SiteModel findByCode(String code)  {
        SiteModel siteModel = jdbcTemplate
                .queryForObject("SELECT id, code, device, name FROM site WHERE code = ?", new Object[]{code},
                        new SiteRowMapper());

        return siteModel;
    }

    public SiteModel findById(Long id)  {
        SiteModel siteModel = jdbcTemplate
                .queryForObject("SELECT id, code, device, name FROM site WHERE id = ?", new Object[]{id}, new SiteRowMapper());

        return siteModel;
    }

    public List<SiteModel> findByName(String name){
        List<SiteModel> siteModel = jdbcTemplate.query("SELECT id, code, device, name FROM site WHERE name = ?",
                new Object[]{name}, new SiteRowMapper());
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

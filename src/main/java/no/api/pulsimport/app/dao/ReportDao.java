package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.model.ReportModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ReportDao {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public ReportModel findById(Long id) {
        String sql = "SELECT id , name FROM report WHERE id = ?";
        ReportModel reportModel = jdbcTemplate.queryForObject(sql, new Object[] {id}, new ReportRowMapper()) ;
        return reportModel;
    }


    public ReportModel findByName(String name){
        String sql = "SELECT id , name FROM report where name = ?" ;
        ReportModel reportModel = jdbcTemplate.queryForObject(sql, new Object[]{name},new ReportRowMapper()) ;
        return reportModel;
    }


    private class ReportRowMapper implements RowMapper<ReportModel> {
        @Override
        public ReportModel mapRow(ResultSet rs, int i) throws SQLException {
            ReportModel reportModel = new ReportModel();
            reportModel.setId(rs.getLong("id"));
            reportModel.setName(rs.getString("name"));
            return reportModel;
        }
    }
}
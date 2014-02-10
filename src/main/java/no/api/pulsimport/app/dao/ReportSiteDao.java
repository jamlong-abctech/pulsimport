package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.model.ReportSiteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReportSiteDao {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private SiteDao siteDao;


    public List<ReportSiteModel> findBySiteId(Long siteId) {
        String sql = "SELECT id,report_id,site_id FROM report_site WHERE site_id = ?";
        List<ReportSiteModel> reportList = jdbcTemplate.query(sql, new ReportSiteRowMapper(), siteId);
        return reportList;
    }

    public ReportSiteModel findBySiteIdAndReportId(Long siteId, Long reportId){
        String sql = "SELECT id,report_id,site_id FROM report_site WHERE site_id = ? and report_id = ?" ;
        ReportSiteModel reportSiteModel = jdbcTemplate.queryForObject(sql,new Object[]{siteId , reportId},new ReportSiteRowMapper());
        return reportSiteModel;
    }


    private  class ReportSiteRowMapper implements RowMapper<ReportSiteModel> {

        @Override
        public ReportSiteModel mapRow(ResultSet resultSet, int i) throws SQLException {
            ReportSiteModel reportSiteModel = new ReportSiteModel();
            reportSiteModel.setId(resultSet.getLong("id"));
            reportSiteModel.setReportModel(reportDao.findById(resultSet.getLong("report_id")));
            reportSiteModel.setSite(siteDao.findById(resultSet.getLong("site_id")));
            return reportSiteModel;

        }
    }
}

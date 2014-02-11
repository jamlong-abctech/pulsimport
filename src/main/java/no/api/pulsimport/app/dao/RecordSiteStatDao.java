package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.model.RecordSiteStatModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class RecordSiteStatDao {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SiteDao siteDao;

    private static final Logger log = LoggerFactory.getLogger(RecordSiteStatDao.class);

    public RecordSiteStatModel save(RecordSiteStatModel model) {
        if(model.getId() != null){
            return updateRecordSiteStat(model);
        } else {
            return insertRecordSiteStat(model);
        }
    }

    public void batchInsert(final List<RecordSiteStatModel> recordSiteStatModelList) {
        String sql = "INSERT INTO recordsitestat (uniquevisitor, uniquevisitordate, " +
                "pageview, pageviewdate, visit, visitdate, site_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RecordSiteStatModel model = recordSiteStatModelList.get(i);
                ps.setInt(1, model.getUniqueVisitor());
                ps.setLong(2, model.getUniqueVisitorDate().getMillis());
                ps.setInt(3, model.getPageView());
                ps.setLong(4, model.getPageViewDate().getMillis());
                ps.setInt(5,model.getVisit());
                ps.setLong(6,model.getVisitDate().getMillis());
                ps.setLong(7, model.getSite().getId());
            }

            @Override
            public int getBatchSize() {
                return recordSiteStatModelList.size();
            }
        });
    }

    public RecordSiteStatModel findById(Long id) {
        String sql = "SELECT id, uniquevisitor, uniquevisitordate, pageview, pageviewdate, visit, visitdate, site_id FROM recordsitestat WHERE id = ?";
        RecordSiteStatModel recordSiteStatModel = jdbcTemplate.queryForObject(sql, new Object[]{id}, new RecordSiteStatRowMapper());
        return recordSiteStatModel;
    }

    public RecordSiteStatModel findBySiteId(Long siteId) {
        String sql = "SELECT id, uniquevisitor, uniquevisitordate, pageview, pageviewdate, visit, visitdate, site_id FROM recordsitestat WHERE site_id = ?";
        RecordSiteStatModel recordSiteStatModel = jdbcTemplate.queryForObject(sql, new Object[]{siteId}, new RecordSiteStatRowMapper());
        return recordSiteStatModel;
    }

    public List<RecordSiteStatModel> findBySiteDeviceSortSiteNameAsc(String device) {
        String sql = "SELECT a.id, a.uniquevisitor, a.uniquevisitordate, a.pageview, a.pageviewdate, a.visit, " +
                " a.visitdate, a.site_id, (CASE WHEN b.code = 'amedia' THEN '' ELSE b.code END) as amediaontop " +
                " FROM recordsitestat a JOIN site b ON a.site_id = b.id WHERE " +
                " b.device = ? ORDER BY amediaontop ASC ";

        List<RecordSiteStatModel> siteStatList = jdbcTemplate.query(sql, new Object[]{device},
                new RecordSiteStatRowMapper());

        return siteStatList;
    }

    private RecordSiteStatModel insertRecordSiteStat(RecordSiteStatModel recordSiteStatModel){
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new RecordSiteStatInsertStatementCreator(recordSiteStatModel), key);
        long returnedId = key.getKey().longValue();
        recordSiteStatModel.setId(returnedId);

        log.debug("Insert recordsitestat and got id returned = " + returnedId);

        return recordSiteStatModel;
    }

    private RecordSiteStatModel updateRecordSiteStat(RecordSiteStatModel recordSiteStatModel){
        String sql = "UPDATE recordsitestat SET uniquevisitor = ?, uniquevisitordate = ?, pageview = ?, pageviewdate = ?, visit = ?, visitdate = ?, site_id = ? WHERE id = ?";
        int rowAffect =
                jdbcTemplate.update(sql,
                recordSiteStatModel.getUniqueVisitor(),
                recordSiteStatModel.getUniqueVisitorDate().getMillis(),
                recordSiteStatModel.getPageView(),
                recordSiteStatModel.getPageViewDate().getMillis(),
                recordSiteStatModel.getVisit(),
                recordSiteStatModel.getVisitDate().getMillis(),
                recordSiteStatModel.getSite().getId(),
                recordSiteStatModel.getId());
        log.debug("update = " + sql);
        if(rowAffect !=1){
            log.warn("No recordsitestat id = {} found to be updated", recordSiteStatModel.getId());
        }
        return recordSiteStatModel;
    }

    private static final class RecordSiteStatInsertStatementCreator implements PreparedStatementCreator {

        private RecordSiteStatModel recordSiteStatModel;

        private RecordSiteStatInsertStatementCreator(RecordSiteStatModel model) {
            this.recordSiteStatModel = model;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            String sql = "INSERT INTO recordsitestat (uniquevisitor, uniquevisitordate, " +
                    "pageview, pageviewdate, visit, visitdate, site_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, recordSiteStatModel.getUniqueVisitor());
            ps.setLong(2, recordSiteStatModel.getUniqueVisitorDate().getMillis());
            ps.setInt(3, recordSiteStatModel.getPageView());
            ps.setLong(4, recordSiteStatModel.getPageViewDate().getMillis());
            ps.setInt(5, recordSiteStatModel.getVisit());
            ps.setLong(6, recordSiteStatModel.getVisitDate().getMillis());
            ps.setLong(7, recordSiteStatModel.getSite().getId());

            log.trace("Prepared statement created as" + ps.toString());
            log.debug("ps: {}", ps.toString());
            return ps;
        }
    }

    private class RecordSiteStatRowMapper implements RowMapper<RecordSiteStatModel> {

        @Override
        public RecordSiteStatModel mapRow(ResultSet rs, int i) throws SQLException {
            RecordSiteStatModel model = new RecordSiteStatModel();
            model.setId(rs.getLong("id"));
            model.setUniqueVisitor(rs.getInt("uniquevisitor"));
            model.setUniqueVisitorDate(new DateTime(rs.getLong("uniquevisitordate")));
            model.setPageView(rs.getInt("pageview"));
            model.setPageViewDate(new DateTime(rs.getLong("pageviewdate")));
            model.setVisit(rs.getInt("visit"));
            model.setVisitDate(new DateTime(rs.getLong("visitdate")));
            model.setSite(siteDao.findById(rs.getLong("site_id")));

            return model;
        }
    }
}

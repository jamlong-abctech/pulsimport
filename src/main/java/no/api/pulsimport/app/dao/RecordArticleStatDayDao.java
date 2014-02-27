package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.model.RecordArticleStatDayModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class RecordArticleStatDayDao {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SiteDao siteDao;

    private static final Logger log = LoggerFactory.getLogger(RecordArticleStatDayDao.class);

    public RecordArticleStatDayModel save(RecordArticleStatDayModel model) {
        if(model.getId() != null){
            return updateRecordArticleStatDay(model);
        } else {
            return insertRecordArticleStatDay(model);
        }
    }

    public int deleteAll() {
        String sql = "DELETE FROM recordarticlestatday";
        return jdbcTemplate.update(sql);
    }

    private RecordArticleStatDayModel insertRecordArticleStatDay(RecordArticleStatDayModel recordArticleStatDayModel){
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new RecordArticleStatDayInsertStatementCreator(recordArticleStatDayModel), key);
        long returnedId = key.getKey().longValue();
        recordArticleStatDayModel.setId(returnedId);

        log.debug("Insert recordsitestat and got id returned = " + returnedId);

        return recordArticleStatDayModel;
    }

    private RecordArticleStatDayModel updateRecordArticleStatDay(RecordArticleStatDayModel recordArticleStatDayModel){
        String sql = "UPDATE recordarticlestatday SET uniquevisitor = ?, uniquevisitorarticleid = ?, uniquevisitorarticletitle = ?, uniquevisitorarticleurl = ?, uniquevisitordate = ?," +
                " pageview = ?, pageviewarticleid = ?, pageviewarticletitle = ?, pageviewarticleurl = ?, pageviewdate = ?," +
                " visit = ?, visitarticleid = ?, visitarticletitle = ?, visitarticleurl = ?, visitdate = ?," +
                " site_id = ? WHERE id = ?";
        int rowAffect =
                jdbcTemplate.update(sql,
                        recordArticleStatDayModel.getUniqueVisitor(),
                        recordArticleStatDayModel.getUniqueVisitorArticleId(),
                        recordArticleStatDayModel.getUniqueVisitorArticleTitle(),
                        recordArticleStatDayModel.getUniqueVisitorArticleUrl(),
                        recordArticleStatDayModel.getUniqueVisitorDate().getMillis(),
                        recordArticleStatDayModel.getPageView(),
                        recordArticleStatDayModel.getPageViewArticleId(),
                        recordArticleStatDayModel.getPageViewArticleTitle(),
                        recordArticleStatDayModel.getPageViewArticleUrl(),
                        recordArticleStatDayModel.getPageViewDate().getMillis(),
                        recordArticleStatDayModel.getVisit(),
                        recordArticleStatDayModel.getVisitArticleId(),
                        recordArticleStatDayModel.getVisitArticleTitle(),
                        recordArticleStatDayModel.getVisitArticleUrl(),
                        recordArticleStatDayModel.getVisitDate().getMillis(),
                        recordArticleStatDayModel.getSite().getId(),
                        recordArticleStatDayModel.getId());

        if(rowAffect !=1){
            log.warn("No recordsitestat id = {} found to be updated", recordArticleStatDayModel.getId());
        }
        return recordArticleStatDayModel;
    }

    public RecordArticleStatDayModel findBySiteId(Long siteId) {

        String sql = "SELECT id, uniquevisitor, uniquevisitorarticleid, uniquevisitorarticletitle, uniquevisitorarticleurl, uniquevisitordate," +
                " pageview, pageviewarticleid, pageviewarticletitle, pageviewarticleurl, pageviewdate," +
                " visit, visitarticleid, visitarticletitle, visitarticleurl, visitdate, site_id" +
                " FROM recordarticlestatday WHERE site_id = ?";
        try {
            RecordArticleStatDayModel recordArticleStatDayModel = jdbcTemplate.queryForObject(sql, new Object[]{siteId}, new RecordArticleStatDayRowMapper());
            return recordArticleStatDayModel;
        } catch (EmptyResultDataAccessException e) {
            log.debug("RecordArticleStatDayModel not found for siteId : {}", siteId);
            return null;
        }
    }

    private List<RecordArticleStatDayModel> findBySiteDeviceSortByDesc(String siteDevice, String columnName) {
        String sql = "SELECT a.id, a.uniquevisitor, a.uniquevisitorarticleid, a.uniquevisitorarticletitle, a.uniquevisitorarticleurl, a.uniquevisitordate," +
                " a.pageview, a.pageviewarticleid, a.pageviewarticletitle, a.pageviewarticleurl, a.pageviewdate," +
                " a.visit, a.visitarticleid, a.visitarticletitle, a.visitarticleurl, a.visitdate, a.site_id" +
                " FROM recordarticlestatday a JOIN site b ON a.site_id = b.id WHERE b.device = ? " +
                " ORDER BY a." + columnName + " DESC ";

        List<RecordArticleStatDayModel> statDayList =
                jdbcTemplate.query(sql, new Object[]{siteDevice}, new RecordArticleStatDayRowMapper());

        return statDayList;
    }

    private static final class RecordArticleStatDayInsertStatementCreator implements PreparedStatementCreator {

        private RecordArticleStatDayModel recordArticleStatDayModel;

        private RecordArticleStatDayInsertStatementCreator(RecordArticleStatDayModel model) {
            this.recordArticleStatDayModel = model;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            String sql = "INSERT INTO recordarticlestatday (" +
                    "uniquevisitor, uniquevisitorarticleid, uniquevisitorarticletitle, uniquevisitorarticleurl, uniquevisitordate," +
                    " pageview, pageviewarticleid, pageviewarticletitle, pageviewarticleurl, pageviewdate," +
                    " visit, visitarticleid, visitarticletitle, visitarticleurl, visitdate," +
                    " site_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, recordArticleStatDayModel.getUniqueVisitor());
            ps.setString(2, recordArticleStatDayModel.getUniqueVisitorArticleId());
            ps.setString(3, recordArticleStatDayModel.getUniqueVisitorArticleTitle());
            ps.setString(4, recordArticleStatDayModel.getUniqueVisitorArticleUrl());
            ps.setLong(5, recordArticleStatDayModel.getUniqueVisitorDate().getMillis());

            ps.setInt(6, recordArticleStatDayModel.getPageView());
            ps.setString(7, recordArticleStatDayModel.getPageViewArticleId());
            ps.setString(8, recordArticleStatDayModel.getPageViewArticleTitle());
            ps.setString(9, recordArticleStatDayModel.getPageViewArticleUrl());
            ps.setLong(10, recordArticleStatDayModel.getPageViewDate().getMillis());

            ps.setInt(11, recordArticleStatDayModel.getVisit());
            ps.setString(12, recordArticleStatDayModel.getVisitArticleId());
            ps.setString(13, recordArticleStatDayModel.getVisitArticleTitle());
            ps.setString(14, recordArticleStatDayModel.getVisitArticleUrl());
            ps.setLong(15, recordArticleStatDayModel.getVisitDate().getMillis());

            ps.setLong(16, recordArticleStatDayModel.getSite().getId());

            return ps;
        }
    }

    private class RecordArticleStatDayRowMapper implements RowMapper<RecordArticleStatDayModel> {

        @Override
        public RecordArticleStatDayModel mapRow(ResultSet rs, int i) throws SQLException {
            RecordArticleStatDayModel model = new RecordArticleStatDayModel();
            model.setId(rs.getLong("id"));
            model.setUniqueVisitor(rs.getInt("uniquevisitor"));
            model.setUniqueVisitorArticleId(rs.getString("uniquevisitorarticleid"));
            model.setUniqueVisitorArticleTitle(rs.getString("uniquevisitorarticletitle"));
            model.setUniqueVisitorArticleUrl(rs.getString("uniquevisitorarticleurl"));
            model.setUniqueVisitorDate(new DateTime(rs.getLong("uniquevisitordate")));
            model.setPageView(rs.getInt("pageview"));
            model.setPageViewArticleId(rs.getString("pageviewarticleid"));
            model.setPageViewArticleTitle(rs.getString("pageviewarticletitle"));
            model.setPageViewArticleUrl(rs.getString("pageviewarticleurl"));
            model.setPageViewDate(new DateTime(rs.getLong("pageviewdate")));
            model.setVisit(rs.getInt("visit"));
            model.setVisitArticleId(rs.getString("visitarticleid"));
            model.setVisitArticleTitle(rs.getString("visitarticletitle"));
            model.setVisitArticleUrl(rs.getString("visitarticleurl"));
            model.setVisitDate(new DateTime(rs.getLong("visitdate")));
            model.setSite(siteDao.findById(rs.getLong("site_id")));

            return model;
        }
    }

}

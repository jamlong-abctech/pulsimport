package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.model.SiteStatModel;
import org.joda.time.DateTime;
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

/**
 * Created by tum on 2/9/2014 AD.
 */
@Repository
public class SiteStatDao {

    //private static final Logger log = LoggerFactory.getLogger(SiteStatDao.class);

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SiteDao siteDao;

    public SiteStatModel save(SiteStatModel model) {
        if (model.getId() != null) {
            return updateSiteStat(model);
        } else {
            return insertSiteStat(model);
        }
    }

    public void batchInsert(final List<SiteStatModel> siteStatModelList) {
        String sql = "INSERT INTO sitestat (uniquevisitor, pageview, visit, hour,video, site_id) VALUES (?, ?, ?, ?,?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SiteStatModel model = siteStatModelList.get(i);
                ps.setInt(1, model.getUniqueVisitor());
                ps.setInt(2, model.getPageView());
                ps.setInt(3, model.getVisit());
                ps.setLong(4, model.getHour().getMillis());
                ps.setLong(5,model.getVideo());
                ps.setLong(6, model.getSite().getId());
            }

            @Override
            public int getBatchSize() {
                return siteStatModelList.size();
            }
        });
    }

    public SiteStatModel findByHourAndSiteCodeId(DateTime hour, long siteCodeId) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, hour, video,site_id FROM sitestat WHERE hour = ? AND site_id = ?";
        SiteStatModel model =
                jdbcTemplate.queryForObject(sql, new Object[]{hour.getMillis(), siteCodeId}, new SiteStatRowMapper());

        return model;
    }

    public List<SiteStatModel> findByHourPeriodAndSiteId(DateTime from, DateTime to, long siteCodeId) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, hour,video, site_id FROM sitestat " +
                "WHERE hour BETWEEN ? AND ? AND site_id = ?";
        return jdbcTemplate.query(sql, new Object[]{from.getMillis(), to.getMillis(), siteCodeId}, new SiteStatRowMapper());
    }

    public SiteStatModel findLatestHourByDate(long siteId, DateTime asOf){
        String sql = "SELECT id, uniquevisitor, pageview, visit, hour, video,site_id FROM sitestat WHERE site_id = ? AND hour >= ? AND hour < ? ORDER BY hour DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, new Object[]{siteId, asOf.getMillis(), asOf.plusDays(1).getMillis()},
                new SiteStatRowMapper());
    }

    public List<SiteStatModel>findPreviousDate(long siteId,DateTime asOf){
        String sql = "SELECT id, uniquevisitor, pageview, visit, hour, video,site_id FROM sitestat WHERE site_id = ? and hour<=?";
        return jdbcTemplate.query(sql, new Object[]{siteId,asOf.minusDays(1).getMillis()}, new SiteStatRowMapper());
    }

    //TODO Tone.3/26/13, add unit test
    public List<SiteStatModel> findByDateAndSiteId(DateTime date, long siteId) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, hour, video,site_id FROM sitestat " +
                "WHERE hour >= ? AND hour < ? AND site_id = ? ";
        List<SiteStatModel> res = jdbcTemplate
                .query(sql, new Object[]{date.withMillisOfDay(0).getMillis(),
                        date.plusDays(1).withMillisOfDay(0).getMillis(), siteId}, new SiteStatRowMapper());
        return res;
    }

    private SiteStatModel insertSiteStat(SiteStatModel model) {
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new SiteStatInsertStatementCreator(model), key);
        long returnedId = key.getKey().longValue();
        model.setId(returnedId);

        //log.debug("Insert sitestat and got id returned = " + returnedId);

        return model;
    }

    private SiteStatModel updateSiteStat(SiteStatModel model) {
        // NOTE, there is no site_id column update, because it's not common to update the site_id
        String sql = "UPDATE sitestat SET uniquevisitor = ?, pageview = ?, visit = ?, hour = ? ,video = ? WHERE id = ?";

        int rowAffect = jdbcTemplate.update(sql, model.getUniqueVisitor(), model.getPageView(), model.getVisit(),
                model.getHour().getMillis(),model.getVideo(), model.getId());
        if (rowAffect != 1) {
            //log.warn("No sitestat id = {} found to be updated", model.getId());
        }

        return model;
    }

    private static final class SiteStatInsertStatementCreator implements PreparedStatementCreator {

        private SiteStatModel model;

        private SiteStatInsertStatementCreator(SiteStatModel model) {
            this.model = model;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            String sql = "INSERT INTO sitestat (uniquevisitor, pageview, visit, hour,video, site_id) VALUES (?, ?, ?, ?,?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, model.getUniqueVisitor());
            ps.setInt(2, model.getPageView());
            ps.setInt(3, model.getVisit());
            ps.setLong(4, model.getHour().getMillis());
            ps.setLong(5,model.getVideo());
            ps.setLong(6, model.getSite().getId());

            return ps;
        }
    }

    private class SiteStatRowMapper implements RowMapper<SiteStatModel> {

        @Override
        public SiteStatModel mapRow(ResultSet rs, int i) throws SQLException {
            SiteStatModel model = new SiteStatModel();
            model.setId(rs.getLong("id"));
            model.setUniqueVisitor(rs.getInt("uniquevisitor"));
            model.setPageView(rs.getInt("pageview"));
            model.setVisit(rs.getInt("visit"));
            model.setHour(new DateTime(rs.getLong("hour")));
            model.setVideo(rs.getInt("video"));
            model.setSite(siteDao.findById(rs.getLong("site_id")));

            return model;
        }
    }
}

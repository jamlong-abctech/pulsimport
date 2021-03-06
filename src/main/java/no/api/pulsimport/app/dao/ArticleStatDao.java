package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.bean.TotalOfArticleBean;
import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.model.ArticleStatModel;
import no.api.pulsimport.app.model.ArticleStatModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by jamlong on 2/10/2014 AD.
 */
@Repository
public class ArticleStatDao {

    private static final Logger log = LoggerFactory.getLogger(ArticleStatDao.class);

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SiteDao siteDao;


    public long countArticleStat(Long siteId) {
        String sql = "SELECT COUNT(*) FROM articlestat WHERE site_id = "+siteId;
        return  jdbcTemplate.queryForObject(sql, Long.class);
    }

    public DateTime fineMinTimeFromArticleStat(Long siteId) {
        String sql = "SELECT MIN(date) FROM articlestat WHERE site_id = "+siteId;
        long maxdateInLong = jdbcTemplate.queryForObject(sql, Long.class);
        return new DateTime(maxdateInLong);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void batchInsert(final List<ArticleStatModel> articleStatModelList) {
        String sql = "INSERT IGNORE INTO articlestat (uniquevisitor, pageview, visit, date,articleid, articletitle,articleurl,site_id) VALUES (?, ?, ?, ?,?, ?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ArticleStatModel model = articleStatModelList.get(i);
                ps.setInt(1, model.getUniqueVisitor());
                ps.setInt(2, model.getPageView());
                ps.setInt(3, model.getVisit());
                ps.setLong(4, model.getDate().getMillis());
                ps.setString(5,model.getArticleId());
                ps.setString(6, model.getArticleTitle());
                ps.setString(7, model.getArticleUrl());
                ps.setLong(8, model.getSite().getId());
            }

            @Override
            public int getBatchSize() {
                return articleStatModelList.size();
            }
        });
    }

    public Long findLastDateTime() {
        String sql = "SELECT MAX(date) FROM articlestat ";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /*
       The getTotalOfUniqueVisitor method ,getTotalOfPageView method and getTotalOfVisitor method return only ariticle id
        which is the maximum of user access in each site. To call this method, it returns article model that we need to obtain article url and article title.
        We need only one record because others have same article title and article url
     */

    public ArticleStatModel findByArticleIdAndSiteIdForTotalNumber(String articleId, Long siteId)  {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE articleid = ? AND site_id = ? limit 1";

        ArticleStatModel model = jdbcTemplate
                .queryForObject(sql, new Object[]{articleId, siteId}, new ArticleStatRowMapper());

        return model;
    }

    public ArticleStatModel findHighestUniqueVisitorOlderDateAndSite(long siteId, DateTime uniqueDate) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE site_id = ?  AND date < ? ORDER BY uniquevisitor DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{siteId, uniqueDate.getMillis()}, new ArticleStatRowMapper());
        }catch (EmptyResultDataAccessException e) {
            log.debug("ArticleStatModel not found for siteId : {}", siteId);
            return null;
        }

    }

    public ArticleStatModel findHighestPageViewOlderByDateAndSite(long siteId, DateTime pageViewDate) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE site_id = ? AND date < ? ORDER BY pageview DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{siteId, pageViewDate.getMillis()}, new ArticleStatRowMapper());
        }catch (EmptyResultDataAccessException e) {
            log.debug("ArticleStatModel not found for siteId : {}", siteId);
            return null;
        }
    }

    public ArticleStatModel findHighestVisitOlderByDateAndSite(long siteId, DateTime visitDate) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE site_id = ? AND date < ? ORDER BY visit DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{siteId, visitDate.getMillis()}, new ArticleStatRowMapper());
        }catch (EmptyResultDataAccessException e) {
            log.debug("ArticleStatModel not found for siteId : {}", siteId);
            return null;
        }
    }


    private ArticleStatModel insert(ArticleStatModel model) {
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new ArticleStatInsertStatementCreator(model), key);
        Long id = key.getKey().longValue();
        model.setId(id);

        return model;
    }

    public TotalOfArticleBean getTotalOfUniqueVisitor(Long siteId) {
        String sql = "SELECT articleid, SUM(uniquevisitor) AS total" +
                " FROM articlestat WHERE site_id = ? GROUP BY articleid  ORDER BY total DESC LIMIT 1 ";
        try {
            return  jdbcTemplate.queryForObject(sql, new Object[]{siteId}, new TotalOfArticleRowMapper(siteId));
        } catch (EmptyResultDataAccessException e) {
            return null;

        }

    }

    public TotalOfArticleBean getTotalOfPageView(Long siteId) {
        String sql = "SELECT articleid,  SUM(pageview) AS total " +
                "FROM articlestat WHERE site_id = ?  GROUP BY articleid ORDER BY total DESC LIMIT 1 ";
        try {
            return  jdbcTemplate.queryForObject(sql, new Object[]{siteId}, new TotalOfArticleRowMapper(siteId));
        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }

    public TotalOfArticleBean getTotalOfVisitor(Long siteId) {
        String sql = "SELECT articleid, SUM(visit) AS total " +
                "FROM articlestat WHERE site_id = ? GROUP BY articleid ORDER BY total DESC LIMIT 1 ";
        try {
            return   jdbcTemplate.queryForObject(sql, new Object[]{siteId}, new TotalOfArticleRowMapper(siteId));
        } catch (EmptyResultDataAccessException e) {
            return null;

        }
    }


    private static final class ArticleStatInsertStatementCreator implements PreparedStatementCreator {

        private ArticleStatModel model;

        private ArticleStatInsertStatementCreator(ArticleStatModel model) {
            this.model = model;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            String sql =
                    "INSERT INTO articlestat (uniquevisitor, pageview, visit, date, articleid, articletitle, " +
                            "articleurl, site_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, model.getUniqueVisitor());
            ps.setInt(2, model.getPageView());
            ps.setInt(3, model.getVisit());
            ps.setLong(4, model.getDate().getMillis());
            ps.setString(5, model.getArticleId());
            ps.setString(6, model.getArticleTitle());
            ps.setString(7, model.getArticleUrl());
            ps.setLong(8, model.getSite().getId());

            return ps;
        }
    }

    private class ArticleStatRowMapper implements RowMapper<ArticleStatModel> {

        @Override
        public ArticleStatModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            ArticleStatModel model = new ArticleStatModel();
            model.setId(rs.getLong("id"));
            model.setUniqueVisitor(rs.getInt("uniquevisitor"));
            model.setPageView(rs.getInt("pageview"));
            model.setVisit(rs.getInt("visit"));
            model.setDate(new DateTime(rs.getLong("date")));
            model.setArticleId(rs.getString("articleid"));
            model.setArticleTitle(rs.getString("articletitle"));
            model.setArticleUrl(rs.getString("articleurl"));
            model.setSite(siteDao.findById(rs.getLong("site_id")));

            return model;
        }
    }


    private class TotalOfArticleRowMapper implements RowMapper<TotalOfArticleBean> {
        private Long siteId;

        public TotalOfArticleRowMapper(Long siteId) {
            this.siteId = siteId;

        }


        @Override
        public TotalOfArticleBean mapRow(ResultSet rs, int rowNum) throws SQLException {
            TotalOfArticleBean result = new TotalOfArticleBean();
            ArticleStatModel articleStatModel = findByArticleIdAndSiteIdForTotalNumber(rs.getString("articleid"), siteId);
            result.setArticleId(rs.getString("articleid"));
            result.setArticleTitle(articleStatModel.getArticleTitle());
            result.setArticleUrl(articleStatModel.getArticleUrl());
            result.setTotal(rs.getInt("total"));
            return result;
        }
    }
}

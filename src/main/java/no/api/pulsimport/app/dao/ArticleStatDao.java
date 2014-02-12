package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.bean.TotalOfArticleBean;
import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.model.ArticleStatModel;
import no.api.pulsimport.app.model.ArticleStatModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ArticleStatModel save(ArticleStatModel model) {
        if (model.getId() != null) {
            return update(model);
        } else {
            return insert(model);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void batchInsert(final List<ArticleStatModel> articleStatModelList) {
    String sql = "INSERT INTO articlestat (uniquevisitor, pageview, visit, date,articleid, articletitle,articleurl,site_id) VALUES (?, ?, ?, ?,?, ?,?,?)";
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

    public Long findFirstDateTime() {
        String sql = "SELECT MIN(date) FROM articlestat ";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public Long findLastDateTime() {
        String sql = "SELECT MAX(date) FROM articlestat ";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public ArticleStatModel findByDateArticleIdAndSiteId(DateTime date, String articleId, Long siteId) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE date = ? AND articleid = ? AND site_id = ?";


        try {
            return  jdbcTemplate
                    .queryForObject(sql, new Object[]{date.getMillis(), articleId, siteId}, new ArticleStatRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;

        }
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

    public ArticleStatModel findById(Long id)  {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE id = ?";
        ArticleStatModel model =
                jdbcTemplate.queryForObject(sql, new Object[]{id}, new ArticleStatRowMapper());

        return model;
    }

    public ArticleStatModel findHighestUniqueVisitorByDateAndSite(long siteId, DateTime uniqueDate) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE site_id = ? AND date >= ? AND date < ? ORDER BY uniquevisitor DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{siteId, uniqueDate.getMillis(), uniqueDate.plusDays(1).getMillis()}, new ArticleStatRowMapper());
        }catch (EmptyResultDataAccessException e) {
            log.debug("ArticleStatModel not found for siteId : {}", siteId);
            return null;
        }

    }

    public ArticleStatModel findHighestPageViewByDateAndSite(long siteId, DateTime pageViewDate) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE site_id = ? AND date >= ? AND date < ? ORDER BY pageview DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{siteId, pageViewDate.getMillis(), pageViewDate.plusDays(1).getMillis()}, new ArticleStatRowMapper());
        }catch (EmptyResultDataAccessException e) {
            log.debug("ArticleStatModel not found for siteId : {}", siteId);
            return null;
        }
    }

    public ArticleStatModel findHighestVisitByDateAndSite(long siteId, DateTime visitDate) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id" +
                " FROM articlestat WHERE site_id = ? AND date >= ? AND date < ? ORDER BY visit DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{siteId, visitDate.getMillis(), visitDate.plusDays(1).getMillis()}, new ArticleStatRowMapper());
        }catch (EmptyResultDataAccessException e) {
            log.debug("ArticleStatModel not found for siteId : {}", siteId);
            return null;
        }
    }

    public ArticleStatModel update(ArticleStatModel model) {
        String sql = "UPDATE articlestat SET uniquevisitor = ?, pageview = ?, visit = ?, date = ?, articleid = ?," +
                " articletitle = ?, site_id = ? WHERE id = ?";

        int rowEffect = jdbcTemplate.update(sql, new Object[]{model.getUniqueVisitor(), model.getPageView(),
                model.getVisit(), model.getDate().getMillis(), model.getArticleId(), model.getArticleTitle(),
                model.getSite().getId(), model.getId()});

        if (rowEffect != 1) {
            //log.warn("No articlestat id = {} found to be updated", model.getId());
        }

        return model;
    }

    private ArticleStatModel insert(ArticleStatModel model) {
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new ArticleStatInsertStatementCreator(model), key);
        Long id = key.getKey().longValue();
        model.setId(id);

        return model;
    }


    public List<ArticleStatModel> findByDateAndSiteIdSortPageViewDesc(DateTime date, Long siteId, int limit) {
        String sql = "SELECT id, uniquevisitor, pageview, visit, date, articleid, articletitle, articleurl, site_id " +
                " FROM articlestat WHERE date = ? AND site_id = ? ORDER BY pageview DESC LIMIT " + limit;

        List<ArticleStatModel> res =
                jdbcTemplate.query(sql, new Object[]{date.getMillis(), siteId}, new ArticleStatRowMapper());

        return res;
    }


    /**
     * see {@link #findByDateAndSiteIdSortPageViewDesc} but no siteId specified
     */
    public List<ArticleStatModel> findByDateAndSiteDeviceSortPageViewDesc(DateTime date, String device, int limit) {
        //log.debug("Device : : :{} " , device);
        String sql = "SELECT article.id, article.uniquevisitor, article.pageview, article.visit, " +
                "article.date, article.articleid, " +
                "article.articletitle, article.articleurl, article.site_id " +
                "FROM articlestat article LEFT JOIN site s ON article.site_id = s.id " +
                "WHERE date = ? AND s.device = ? ORDER BY pageview DESC LIMIT " + limit;

        List<ArticleStatModel> res =
                jdbcTemplate.query(sql, new Object[]{date.getMillis(), device}, new ArticleStatRowMapper());

        return res;
    }

    public List<ArticleStatModel> findTopPageViewArticleBy
            (DateTime date, String device, String totalReportCode, int limit) {
        String sql = "SELECT article.id, article.uniquevisitor, article.pageview, article.visit, " +
                "article.date, article.articleid, " +
                "article.articletitle, article.articleurl, article.site_id " +
                "FROM articlestat article " +
                "INNER JOIN site ON site.id = article.site_id and site.id  IN  " +
                "(SELECT site_id FROM report_site INNER JOIN report ON report.id = report_site.report_id  " +
                "WHERE report.name = ?) WHERE site.device= ? " +
                "AND article.date = ? ORDER BY pageview DESC limit ?";

        return jdbcTemplate.query (
                sql, new Object[]{totalReportCode, device, date.getMillis(), limit}, new ArticleStatRowMapper());
    }


    public List<ArticleStatModel> findByDateWithDesktopAndMobileDeviceSortPageViewDesc(DateTime date, int limit) {
        String sql = "SELECT article.id, article.uniquevisitor, article.pageview, article.visit, article.date, article.articleid, " +
                "article.articletitle, article.articleurl, article.site_id " +
                " FROM articlestat article LEFT JOIN site s ON article.site_id = s.id " +
                " WHERE date = ? AND (s.device = ? OR s.device = ?) ORDER BY pageview DESC LIMIT " + limit;

        List<ArticleStatModel> res =
                jdbcTemplate.query(sql, new Object[]{date.getMillis(), SiteDeviceEnum.DESKTOP.toTextValue(),
                        SiteDeviceEnum.MOBILE.toTextValue()}, new ArticleStatRowMapper());

        return res;
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

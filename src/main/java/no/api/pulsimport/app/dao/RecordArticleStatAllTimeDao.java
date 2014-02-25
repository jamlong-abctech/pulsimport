package no.api.pulsimport.app.dao;

import no.api.pulsimport.app.model.RecordArticleStatAllTimeModel;
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

@Repository
public class RecordArticleStatAllTimeDao {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SiteDao siteDao;

    private static final Logger log = LoggerFactory.getLogger(RecordArticleStatAllTimeDao.class);
    private static final String RECORDARTICLESTATALLTIME_TABLE = "recordarticlestatalltime";   //
    private static final String RECORDARTICLESTATALLTIME_COLUMN_LIST = "uniquevisitor , uniquevisitorarticleid , uniquevisitorarticletitle , uniquevisitorarticleurl , pageview,  " +
            " pageviewarticleid , pageviewarticletitle , pageviewarticleurl , visit , visitarticleid , visitarticletitle , visitarticleurl , site_id";



    public RecordArticleStatAllTimeModel save(RecordArticleStatAllTimeModel model) {

        if (model.getId() != null) {
            return updateRecordArticleStatAllTime(model);
        } else {
            return addRecordArticleStatAllTimeModel(model);
        }

    }

    private RecordArticleStatAllTimeModel addRecordArticleStatAllTimeModel(RecordArticleStatAllTimeModel model) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new RecordArticleStatAllTimePrepareStatementCreator(model), keyHolder);
        long key = keyHolder.getKey().longValue();
        model.setId(key);
        return model;
    }

    private RecordArticleStatAllTimeModel updateRecordArticleStatAllTime(RecordArticleStatAllTimeModel model) {

        String sql = "UPDATE recordarticlestatalltime SET  uniquevisitor=?, uniquevisitorarticleid=?,  uniquevisitorarticletitle=?, uniquevisitorarticleurl=?, pageview=?," +
                "pageviewarticleid=?, pageviewarticletitle=?, pageviewarticleurl=?, visit=?, visitarticleid=?, visitarticletitle=?, visitarticleurl=?, site_id=? WHERE id = ?";

        jdbcTemplate.update(sql,
                model.getUniqueVisitor(),
                model.getUniqueVisitorArticleId(),
                model.getUniqueVisitorArticleTitle(),
                model.getUniqueVisitorArticleUrl(),
                model.getPageView(),
                model.getPageViewArticleId(),
                model.getPageViewArticleTitle(),
                model.getPageViewArticleUrl(),
                model.getVisit(),
                model.getVisitArticleId(),
                model.getVisitArticleTitle(),
                model.getVisitArticleUrl(),
                model.getSite().getId(),
                model.getId()) ;

        return model;
    }

    public RecordArticleStatAllTimeModel findBySiteId(Long siteId) {
      String sql =   "SELECT  id ,uniquevisitor , uniquevisitorarticleid , uniquevisitorarticletitle , uniquevisitorarticleurl , pageview," +
              " pageviewarticleid , pageviewarticletitle , pageviewarticleurl , visit , visitarticleid , visitarticletitle , visitarticleurl , site_id   FROM recordarticlestatalltime  WHERE site_id = ?";
        RowMapper<RecordArticleStatAllTimeModel> mapper = new RecordArticleStatAllTimeRowMapper();
        try {
           return jdbcTemplate.queryForObject(sql,mapper,siteId) ;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    private static final class RecordArticleStatAllTimePrepareStatementCreator implements PreparedStatementCreator {
        private RecordArticleStatAllTimeModel recordArticleStatAllTimeModel;

        private RecordArticleStatAllTimePrepareStatementCreator(RecordArticleStatAllTimeModel model) {
            this.recordArticleStatAllTimeModel = model;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            String sql = "INSERT INTO " + RECORDARTICLESTATALLTIME_TABLE + "  (" + RECORDARTICLESTATALLTIME_COLUMN_LIST + ")" + "  VALUES  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            preparedStatement.setLong(parameterIndex++, recordArticleStatAllTimeModel.getUniqueVisitor());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getUniqueVisitorArticleId());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getUniqueVisitorArticleTitle());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getUniqueVisitorArticleUrl());
            preparedStatement.setLong(parameterIndex++, recordArticleStatAllTimeModel.getPageView());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getPageViewArticleId());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getPageViewArticleTitle());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getPageViewArticleUrl());
            preparedStatement.setLong(parameterIndex++, recordArticleStatAllTimeModel.getVisit());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getVisitArticleId());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getVisitArticleTitle());
            preparedStatement.setString(parameterIndex++, recordArticleStatAllTimeModel.getVisitArticleUrl());
            preparedStatement.setLong(parameterIndex++, recordArticleStatAllTimeModel.getSite().getId());
            log.debug("ps: {}", preparedStatement.toString());
            return preparedStatement;

        }
    }

    private class RecordArticleStatAllTimeRowMapper implements RowMapper<RecordArticleStatAllTimeModel> {
        @Override
        public RecordArticleStatAllTimeModel mapRow(ResultSet resultSet, int i) throws SQLException {
            RecordArticleStatAllTimeModel recordArticleStatAllTimeModel = new RecordArticleStatAllTimeModel();
            recordArticleStatAllTimeModel.setId(resultSet.getLong("id"));
            recordArticleStatAllTimeModel.setUniqueVisitor(resultSet.getInt("uniquevisitor"));
            recordArticleStatAllTimeModel.setUniqueVisitorArticleId(resultSet.getString("uniquevisitorarticleid"));
            recordArticleStatAllTimeModel.setUniqueVisitorArticleTitle(resultSet.getString("uniquevisitorarticletitle"));
            recordArticleStatAllTimeModel.setUniqueVisitorArticleUrl(resultSet.getString("uniquevisitorarticleurl"));
            recordArticleStatAllTimeModel.setPageView(resultSet.getInt("pageview"));
            recordArticleStatAllTimeModel.setPageViewArticleId(resultSet.getString("pageviewarticleid"));
            recordArticleStatAllTimeModel.setPageViewArticleTitle(resultSet.getString("pageviewarticletitle"));
            recordArticleStatAllTimeModel.setPageViewArticleUrl(resultSet.getString("pageviewarticleurl"));
            recordArticleStatAllTimeModel.setVisit(resultSet.getInt("visit"));
            recordArticleStatAllTimeModel.setVisitArticleId(resultSet.getString("visitarticleid"));
            recordArticleStatAllTimeModel.setVisitArticleTitle(resultSet.getString("visitarticletitle"));
            recordArticleStatAllTimeModel.setVisitArticleUrl(resultSet.getString("visitarticleurl"));
            recordArticleStatAllTimeModel.setSite(siteDao.findById(resultSet.getLong("site_id")));

            return  recordArticleStatAllTimeModel ;
        }
    }
}

package no.api.pulsimport.app.mapper;

import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.StringCleaningUtil;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.bean.StatRow;
import no.api.pulsimport.app.model.ArticleStatModel;
import no.api.pulsimport.app.model.SiteModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 */

@Component
public class ArticleStatMapper {

    public List<ArticleStatModel> map(StatResultSet resultset, SiteModel siteModel, DateTime timeLimit) {
        //List<ArticleStatModel> articleStatModels = new ArrayList<>();
        List<StatRow> articleStatRows = resultset.getRows();
        Map<String, ArticleStatModel> statRowMap = new HashMap<>();
        for (StatRow eachRow : articleStatRows) {
            DateTime hour = DateTimeFormatUtil.parseDateTime(eachRow.getField().get(4));
            if(hour == null) {
                continue;
            }
            if (hour.getMillis() < timeLimit.getMillis()) {
                ArticleStatModel articleStatModel = new ArticleStatModel();

                articleStatModel.setUniqueVisitor(Integer.parseInt(eachRow.getField().get(1)));
                articleStatModel.setPageView(Integer.parseInt(eachRow.getField().get(2)));
                articleStatModel.setVisit(Integer.parseInt(eachRow.getField().get(3)));
                articleStatModel.setDate(DateTimeFormatUtil.parseDateTime(eachRow.getField().get(4)));
                articleStatModel.setArticleId(eachRow.getField().get(5));
                articleStatModel.setArticleTitle(StringCleaningUtil.unescapeHtmlAndCapitalizeAndReplaceDash(eachRow.getField().get(6)));
                articleStatModel.setArticleUrl(StringEscapeUtils.unescapeHtml(eachRow.getField().get(7)));
                articleStatModel.setSite(siteModel);
                String aKey = articleStatModel.getArticleId() + articleStatModel.getDate().getMillis();
                ArticleStatModel fromMap = statRowMap.get(aKey);
                if (fromMap != null) {
                    statRowMap.remove(aKey);
                }
                statRowMap.put(aKey, articleStatModel);

            }
        }

        List<ArticleStatModel> articleStatModels = new ArrayList<>(statRowMap.values());
        return articleStatModels;
    }
}

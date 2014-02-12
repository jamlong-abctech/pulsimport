package no.api.pulsimport.app.mapper;

import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.StringCleaningUtil;
import no.api.pulsimport.app.bean.ArticleStatResultSet;
import no.api.pulsimport.app.bean.ArticleStatRow;
import no.api.pulsimport.app.model.ArticleStatModel;
import no.api.pulsimport.app.model.SiteModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

@Component
public class ArticleStatMapper {

    public List<ArticleStatModel> map(ArticleStatResultSet resultset, SiteModel siteModel) {
        List<ArticleStatModel> articleStatModels = new ArrayList<>();
        List<ArticleStatRow> articleStatRows = resultset.getRows();
        for(ArticleStatRow eachRow : articleStatRows) {
            ArticleStatModel articleStatModel = new ArticleStatModel();

            articleStatModel.setUniqueVisitor(Integer.parseInt(eachRow.getField().get(1)));
            articleStatModel.setPageView(Integer.parseInt(eachRow.getField().get(2)));
            articleStatModel.setVisit(Integer.parseInt(eachRow.getField().get(3)));
            articleStatModel.setDate(DateTimeFormatUtil.parseDateTime(eachRow.getField().get(4)));
            articleStatModel.setArticleId(eachRow.getField().get(5));
            articleStatModel.setArticleTitle( StringCleaningUtil.unescapeHtmlAndCapitalizeAndReplaceDash(eachRow.getField().get(6)));
            articleStatModel.setArticleUrl(StringEscapeUtils.unescapeHtml(eachRow.getField().get(7)));
            articleStatModel.setSite(siteModel);


            articleStatModels.add(articleStatModel);
        }

        return articleStatModels;
    }

}

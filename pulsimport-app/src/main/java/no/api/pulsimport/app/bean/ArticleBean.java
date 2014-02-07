package no.api.pulsimport.app.bean;

import java.util.List;

/**
 * represent stat of each row on comscore article
 */

public class ArticleBean {
    private String siteCode;
    private String articleId;
    private List<StatisticByUrlBean> statisticByUrls;
    private StatisticBean totalStatisticOfArticle;

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public List<StatisticByUrlBean> getStatisticByUrls() {
        return statisticByUrls;
    }

    public void setStatisticByUrls(List<StatisticByUrlBean> statisticByUrls) {
        this.statisticByUrls = statisticByUrls;
    }

    public StatisticBean getTotalStatisticOfArticle() {
        return totalStatisticOfArticle;
    }

    public void setTotalStatisticOfArticle(StatisticBean totalStatisticOfArticle) {
        this.totalStatisticOfArticle = totalStatisticOfArticle;
    }
}

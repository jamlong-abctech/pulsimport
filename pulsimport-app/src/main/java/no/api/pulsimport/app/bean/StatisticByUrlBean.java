package no.api.pulsimport.app.bean;

/**
 *
 *
 */
public class StatisticByUrlBean extends StatisticBean {

    private String articleUrl;
    private String title;

    public StatisticByUrlBean(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

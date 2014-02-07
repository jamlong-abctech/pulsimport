package no.api.pulsimport.app.bean;

import org.joda.time.DateTime;

import java.util.List;

/**
 * represent stat of each row on comscore article
 */

public class ArticleBean {
    private Long id;
    private Integer uniqueVisitor;
    private Integer pageView;
    private Integer session;
    private DateTime date;
    private String articleId;
    private String articleTitle;
    private String articleUrl;
    private String siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUniqueVisitor() {
        return uniqueVisitor;
    }

    public void setUniqueVisitor(Integer uniqueVisitor) {
        this.uniqueVisitor = uniqueVisitor;
    }

    public Integer getPageView() {
        return pageView;
    }

    public void setPageView(Integer pageView) {
        this.pageView = pageView;
    }

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

}

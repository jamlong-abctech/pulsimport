package no.api.pulsimport.app.model;

import org.joda.time.DateTime;

public class ArticleStatModel {

    private Long id;
    private Integer uniqueVisitor;
    private Integer pageView;
    private Integer visit;
    private DateTime date;
    private String articleId;
    private String articleTitle;
    private String articleUrl;
    private SiteModel site;

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

    public Integer getVisit() {
        return visit;
    }

    public void setVisit(Integer visit) {
        this.visit = visit;
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

    public SiteModel getSite() {
        return site;
    }

    public void setSite(SiteModel site) {
        this.site = site;
    }
}

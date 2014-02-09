package no.api.pulsimport.app.model;

public class RecordArticleStatAllTimeModel {
    private Long id;
    private Integer uniqueVisitor;
    private String uniqueVisitorArticleId;
    private String uniqueVisitorArticleTitle;
    private String uniqueVisitorArticleUrl;
    private Integer pageView;
    private String pageViewArticleId;
    private String pageViewArticleTitle;
    private String pageViewArticleUrl;
    private Integer visit;
    private String visitArticleId;
    private String visitArticleTitle;
    private String visitArticleUrl;
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

    public String getUniqueVisitorArticleId() {
        return uniqueVisitorArticleId;
    }

    public void setUniqueVisitorArticleId(String uniqueVisitorArticleId) {
        this.uniqueVisitorArticleId = uniqueVisitorArticleId;
    }

    public String getUniqueVisitorArticleTitle() {
        return uniqueVisitorArticleTitle;
    }

    public void setUniqueVisitorArticleTitle(String uniqueVisitorArticleTitle) {
        this.uniqueVisitorArticleTitle = uniqueVisitorArticleTitle;
    }

    public String getUniqueVisitorArticleUrl() {
        return uniqueVisitorArticleUrl;
    }

    public void setUniqueVisitorArticleUrl(String uniqueVisitorArticleUrl) {
        this.uniqueVisitorArticleUrl = uniqueVisitorArticleUrl;
    }

    public Integer getPageView() {
        return pageView;
    }

    public void setPageView(Integer pageView) {
        this.pageView = pageView;
    }

    public String getPageViewArticleId() {
        return pageViewArticleId;
    }

    public void setPageViewArticleId(String pageViewArticleId) {
        this.pageViewArticleId = pageViewArticleId;
    }

    public String getPageViewArticleTitle() {
        return pageViewArticleTitle;
    }

    public void setPageViewArticleTitle(String pageViewArticleTitle) {
        this.pageViewArticleTitle = pageViewArticleTitle;
    }

    public String getPageViewArticleUrl() {
        return pageViewArticleUrl;
    }

    public void setPageViewArticleUrl(String pageViewArticleUrl) {
        this.pageViewArticleUrl = pageViewArticleUrl;
    }

    public Integer getVisit() {
        return visit;
    }

    public void setVisit(Integer visit) {
        this.visit = visit;
    }

    public String getVisitArticleId() {
        return visitArticleId;
    }

    public void setVisitArticleId(String visitArticleId) {
        this.visitArticleId = visitArticleId;
    }

    public String getVisitArticleTitle() {
        return visitArticleTitle;
    }

    public void setVisitArticleTitle(String visitArticleTitle) {
        this.visitArticleTitle = visitArticleTitle;
    }

    public String getVisitArticleUrl() {
        return visitArticleUrl;
    }

    public void setVisitArticleUrl(String visitArticleUrl) {
        this.visitArticleUrl = visitArticleUrl;
    }

    public SiteModel getSite() {
        return site;
    }

    public void setSite(SiteModel site) {
        this.site = site;
    }
}

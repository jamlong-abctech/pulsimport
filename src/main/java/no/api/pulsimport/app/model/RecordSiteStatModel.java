package no.api.pulsimport.app.model;

import org.joda.time.DateTime;

public class RecordSiteStatModel {
    private Long id;
    private Integer uniqueVisitor = 0;
    private DateTime uniqueVisitorDate;
    private Integer pageView = 0;
    private DateTime pageViewDate;
    private Integer visit = 0;
    private DateTime visitDate;
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

    public DateTime getUniqueVisitorDate() {
        return uniqueVisitorDate;
    }

    public void setUniqueVisitorDate(DateTime uniqueVisitorDate) {
        this.uniqueVisitorDate = uniqueVisitorDate;
    }

    public Integer getPageView() {
        return pageView;
    }

    public void setPageView(Integer pageView) {
        this.pageView = pageView;
    }

    public DateTime getPageViewDate() {
        return pageViewDate;
    }

    public void setPageViewDate(DateTime pageViewDate) {
        this.pageViewDate = pageViewDate;
    }

    public Integer getVisit() {
        return visit;
    }

    public void setVisit(Integer visit) {
        this.visit = visit;
    }

    public DateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(DateTime visitDate) {
        this.visitDate = visitDate;
    }

    public SiteModel getSite() {
        return site;
    }

    public void setSite(SiteModel site) {
        this.site = site;
    }
}

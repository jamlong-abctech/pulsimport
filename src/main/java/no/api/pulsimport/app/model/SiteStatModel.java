package no.api.pulsimport.app.model;

import org.joda.time.DateTime;

public class SiteStatModel {

    private Long id;
    private Integer uniqueVisitor;
    private Integer pageView;
    private Integer visit;
    private DateTime hour;
    private Integer video;
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

    public DateTime getHour() {
        return hour;
    }

    public void setHour(DateTime hour) {
        this.hour = hour;
    }

    public SiteModel getSite() {
        return site;
    }

    public void setSite(SiteModel site) {
        this.site = site;
    }

    public Integer getVideo() {
        return video;
    }

    public void setVideo(Integer video) {
        this.video = video;
    }
}

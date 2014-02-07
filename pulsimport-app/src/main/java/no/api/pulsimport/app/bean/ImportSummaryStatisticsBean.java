package no.api.pulsimport.app.bean;

/**
 *  represent the statistic for each import
 */

public class ImportSummaryStatisticsBean {

    private Integer totalOfUniqueVisitor;
    private Integer totalOfSession;
    private Integer totalOfPageView;
    private Integer totalOfMobileUniqueVisitor;
    private Integer totalOfMobileSession;
    private Integer totalOfMobilePageView;
    private Integer totalOfVideo;
    private Integer totalOfMobileVideo;



    public Integer getTotalOfMobilePageView() {
        return totalOfMobilePageView;
    }

    public void setTotalOfMobilePageView(Integer totalOfMobilePageView) {
        this.totalOfMobilePageView = totalOfMobilePageView;
    }

    public Integer getTotalOfMobileSession() {
        return totalOfMobileSession;
    }

    public void setTotalOfMobileSession(Integer totalOfMobileSession) {
        this.totalOfMobileSession = totalOfMobileSession;
    }

    public Integer getTotalOfMobileUniqueVisitor() {
        return totalOfMobileUniqueVisitor;
    }

    public void setTotalOfMobileUniqueVisitor(Integer totalOfMobileUniqueVisitor) {
        this.totalOfMobileUniqueVisitor = totalOfMobileUniqueVisitor;
    }

    public Integer getTotalOfPageView() {
        return totalOfPageView;
    }

    public void setTotalOfPageView(Integer totalOfPageView) {
        this.totalOfPageView = totalOfPageView;
    }

    public Integer getTotalOfSession() {
        return totalOfSession;
    }

    public void setTotalOfSession(Integer totalOfSession) {
        this.totalOfSession = totalOfSession;
    }

    public Integer getTotalOfUniqueVisitor() {
        return totalOfUniqueVisitor;
    }

    public void setTotalOfUniqueVisitor(Integer totalOfUniqueVisitor) {
        this.totalOfUniqueVisitor = totalOfUniqueVisitor;
    }

    public Integer getTotalOfVideo() {
        return totalOfVideo;
    }

    public void setTotalOfVideo(Integer totalOfVideo) {
        this.totalOfVideo = totalOfVideo;
    }

    public Integer getTotalOfMobileVideo() {
        return totalOfMobileVideo;
    }

    public void setTotalOfMobileVideo(Integer totalOfMobileVideo) {
        this.totalOfMobileVideo = totalOfMobileVideo;
    }
}

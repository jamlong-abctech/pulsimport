package no.api.pulsimport.app.bean;

/**
 *   represent stat of site
 */

public class StatisticBean {

    private Integer numberOfUniqueVisitor;
    private Integer numberOfSession;
    private Integer numberOfPageView;
    private Integer numberOfMobileUniqueVisitor;
    private Integer numberOfMobileSession;
    private Integer numberOfMobilePageView;
    private Integer numberOfVideo;
    private Integer numberOfMobileVideo;

    public Integer getNumberOfMobilePageView() {
        return numberOfMobilePageView;
    }

    public void setNumberOfMobilePageView(Integer numberOfMobilePageView) {
        this.numberOfMobilePageView = numberOfMobilePageView;
    }

    public Integer getNumberOfMobileSession() {
        return numberOfMobileSession;
    }

    public void setNumberOfMobileSession(Integer numberOfMobileSession) {
        this.numberOfMobileSession = numberOfMobileSession;
    }

    public Integer getNumberOfMobileUniqueVisitor() {
        return numberOfMobileUniqueVisitor;
    }

    public void setNumberOfMobileUniqueVisitor(Integer numberOfMobileUniqueVisitor) {
        this.numberOfMobileUniqueVisitor = numberOfMobileUniqueVisitor;
    }

    public Integer getNumberOfPageView() {
        return numberOfPageView;
    }

    public void setNumberOfPageView(Integer numberOfPageView) {
        this.numberOfPageView = numberOfPageView;
    }

    public Integer getNumberOfSession() {
        return numberOfSession;
    }

    public void setNumberOfSession(Integer numberOfSession) {
        this.numberOfSession = numberOfSession;
    }

    public Integer getNumberOfUniqueVisitor() {
        return numberOfUniqueVisitor;
    }

    public void setNumberOfUniqueVisitor(Integer numberOfUniqueVisitor) {
        this.numberOfUniqueVisitor = numberOfUniqueVisitor;
    }

    public Integer getNumberOfVideo() {
        return numberOfVideo;
    }

    public void setNumberOfVideo(Integer numberOfVideo) {
        this.numberOfVideo = numberOfVideo;
    }

    public Integer getNumberOfMobileVideo() {
        return numberOfMobileVideo;
    }

    public void setNumberOfMobileVideo(Integer numberOfMobileVideo) {
        this.numberOfMobileVideo = numberOfMobileVideo;
    }
}

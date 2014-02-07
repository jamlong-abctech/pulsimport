package no.api.pulsimport.app.bean;

import java.util.List;

/**
 *  Represent all site stat data from comscore
 */

public class SiteImportBean {

    private List<SiteBean> sites;
    private ImportSummaryStatisticsBean siteStatistics;

    public List<SiteBean> getSites() {
        return sites;
    }

    public void setSites(List<SiteBean> sites) {
        this.sites = sites;
    }

    public ImportSummaryStatisticsBean getSiteStatistics() {
        return siteStatistics;
    }

    public void setSiteStatistics(ImportSummaryStatisticsBean siteStatistics) {
        this.siteStatistics = siteStatistics;
    }
}

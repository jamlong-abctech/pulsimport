package no.api.pulsimport.app.bean;

import java.util.List;

/**
 *  represent one site stat data
 */

public class SiteBean {

    private String siteCode;
    private List<StatisticByHourBean> hourStatistic;
    private StatisticBean totalStatistic;

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public List<StatisticByHourBean> getHourStatistic() {
        return hourStatistic;
    }

    public void setHourStatistic(List<StatisticByHourBean> hourStatistic) {
        this.hourStatistic = hourStatistic;
    }

    public StatisticBean getTotalStatistic() {
        return totalStatistic;
    }

    public void setTotalStatistic(StatisticBean totalStatistic) {
        this.totalStatistic = totalStatistic;
    }
}

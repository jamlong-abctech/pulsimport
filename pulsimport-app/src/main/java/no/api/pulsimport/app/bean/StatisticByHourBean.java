package no.api.pulsimport.app.bean;

/**
 *  represent site stat for each hour
 */

public class StatisticByHourBean extends StatisticBean {

    //represent hour in xml(1,2,3,..)
    private Integer hour;

    public StatisticByHourBean(Integer hour) {
        this.hour = hour;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }
}

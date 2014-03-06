package no.api.pulsimport.app.mapper;

import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.bean.StatRow;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.model.SiteStatModel;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */

@Component
public class SiteStatMapper {

    public List<SiteStatModel> map(StatResultSet resultset, SiteModel siteModel, DateTime timeLimit) {
        List<StatRow> statRows = resultset.getRows();
        Map<Long, SiteStatModel> siteStatModelMap = new HashMap<>();
        for (StatRow eachRow : statRows) {
            DateTime hour = DateTimeFormatUtil.parseDateTime(eachRow.getField().get(4));
            if (hour.getMillis() < timeLimit.getMillis()) {
                SiteStatModel siteStatModel = new SiteStatModel();
                siteStatModel.setUniqueVisitor(Integer.parseInt(eachRow.getField().get(1)));
                siteStatModel.setPageView(Integer.parseInt(eachRow.getField().get(2)));
                siteStatModel.setVisit(Integer.parseInt(eachRow.getField().get(3)));
                siteStatModel.setHour(hour);
                siteStatModel.setVideo(0); //default
                siteStatModel.setSite(siteModel);

                //siteStatModels.add(siteStatModel);
                if(siteStatModelMap.get(hour.getMillis()) == null) {
                    siteStatModelMap.put(hour.getMillis(), siteStatModel);
                } else {
                    siteStatModelMap.remove(hour.getMillis());
                    siteStatModelMap.put(hour.getMillis(), siteStatModel);
                }
            }
        }

        List<SiteStatModel> siteStatModels = new ArrayList<>(siteStatModelMap.values());
        return siteStatModels;
    }
}

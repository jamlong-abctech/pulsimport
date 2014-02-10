package no.api.pulsimport.app.mapper;

import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.bean.StatRow;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.model.SiteStatModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

@Component
public class SiteStatMapper {

    public List<SiteStatModel> map(StatResultSet resultset, SiteModel siteModel) {
        List<SiteStatModel> siteStatModels = new ArrayList<>();
        List<StatRow> statRows = resultset.getRows();
        for(StatRow eachRow : statRows) {
            SiteStatModel siteStatModel = new SiteStatModel();
            siteStatModel.setUniqueVisitor(Integer.parseInt(eachRow.getField().get(1)));
            siteStatModel.setPageView(Integer.parseInt(eachRow.getField().get(2)));
            siteStatModel.setVisit(Integer.parseInt(eachRow.getField().get(3)));
            siteStatModel.setHour(DateTimeFormatUtil.parseDateTime(eachRow.getField().get(4)));
            siteStatModel.setVideo(0); //default
            siteStatModel.setSite(siteModel);

            siteStatModels.add(siteStatModel);
        }

        return siteStatModels;
    }
}

package no.api.pulsimport.app.mapper;

import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.bean.SiteStatResultSet;
import no.api.pulsimport.app.bean.SiteStatRow;
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

    public List<SiteStatModel> map(SiteStatResultSet resultset, SiteModel siteModel) {
        List<SiteStatModel> siteStatModels = new ArrayList<>();
        List<SiteStatRow> siteStatRows = resultset.getRows();
        for(SiteStatRow eachRow : siteStatRows) {
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

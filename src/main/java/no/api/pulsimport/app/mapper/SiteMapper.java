package no.api.pulsimport.app.mapper;

import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.bean.StatRow;
import no.api.pulsimport.app.model.SiteModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SiteMapper {

    public Map<String , String> map(StatResultSet resultset) {
        Map<String, String> siteMap = new HashMap<>();
        List<StatRow> siteRows = resultset.getRows();

        for(StatRow eachRow : siteRows) {
            siteMap.put(eachRow.getField().get(0), eachRow.getField().get(1));
        }
        return siteMap;
    }

}

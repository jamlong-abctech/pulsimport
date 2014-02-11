package no.api.pulsimport.app.mapper;

import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.bean.StatRow;
import no.api.pulsimport.app.model.RecordSiteStatModel;
import no.api.pulsimport.app.model.SiteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RecordSiteStatMapper {

    private static final Logger log = LoggerFactory.getLogger(RecordSiteStatMapper.class);

    private String uniqueVisitor = "unique_visitors_total";
    private String pageView = "page_views_total";
    private String visit = "sessions_total";

    public List<RecordSiteStatModel> map(StatResultSet resultset, SiteModel siteModel) {
        List<RecordSiteStatModel> recordSiteStatModels = new ArrayList<>();
        List<StatRow> recordSiteStatRows = resultset.getRows();
        Map<String, String> uMap = new HashMap<>();
        Map<String, String> pMap = new HashMap<>();
        Map<String, String> vMap = new HashMap<>();
        for (StatRow eachRow : recordSiteStatRows) {
//            RecordSiteStatModel recordSiteStatModel = new RecordSiteStatModel();
//            if(eachRow.getField().get(1).equalsIgnoreCase(uniqueVisitor)){
//                recordSiteStatModel.setUniqueVisitor(Integer.parseInt(eachRow.getField().get(2)));
//                recordSiteStatModel.setUniqueVisitorDate(DateTimeFormatUtil.parseDateTime(eachRow.getField().get(3)));
//            }
//
//            if(eachRow.getField().get(1).equalsIgnoreCase(pageView)){
//                recordSiteStatModel.setPageView(Integer.parseInt(eachRow.getField().get(2)));
//                recordSiteStatModel.setPageViewDate(DateTimeFormatUtil.parseDateTime(eachRow.getField().get(3)));
//            }
//
//            if(eachRow.getField().get(1).equalsIgnoreCase(visit)){
//                recordSiteStatModel.setVisit(Integer.parseInt(eachRow.getField().get(2)));
//                recordSiteStatModel.setVisitDate(DateTimeFormatUtil.parseDateTime(eachRow.getField().get(3)));
//            }
//            recordSiteStatModel.setSite(siteModel);
//            recordSiteStatModels.add(recordSiteStatModel);

            if(eachRow.getField().get(1).equalsIgnoreCase(uniqueVisitor)){
//                recordSiteStatModel.setUniqueVisitor(Integer.parseInt(eachRow.getField().get(2)));
//                recordSiteStatModel.setUniqueVisitorDate(DateTimeFormatUtil.parseDateTime(eachRow.getField().get(3)));
               uMap.put(siteModel.getCode(), eachRow.getField().get(2));
            }

        }
        for(RecordSiteStatModel r: recordSiteStatModels){
            log.debug("uq: {}", r.getUniqueVisitor());
            log.debug("uq date: {}", r.getUniqueVisitorDate());
            log.debug("pg: {}", r.getPageView());
            log.debug("pg date: {}", r.getPageViewDate());
            log.debug("vs: {}", r.getVisit());
            log.debug("vs date: {}", r.getVisitDate());
            log.debug("site: {}", r.getSite().getCode());
        }
        return recordSiteStatModels;
    }
}

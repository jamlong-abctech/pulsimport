package no.api.pulsimport.app.component;

import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.bean.RecordBean;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.dao.RecordArticleStatAllTimeDao;
import no.api.pulsimport.app.dao.RecordArticleStatDayDao;
import no.api.pulsimport.app.dao.RecordSiteStatDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.dao.SiteStatDao;
import no.api.pulsimport.app.mapper.RecordMapper;
import no.api.pulsimport.app.mapper.SiteMapper;
import no.api.pulsimport.app.model.RecordArticleStatAllTimeModel;
import no.api.pulsimport.app.model.RecordArticleStatDayModel;
import no.api.pulsimport.app.model.RecordSiteStatModel;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.parser.ResultSetXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 */

@Component
public class ImportRecordComponent {

    Logger log = LoggerFactory.getLogger(ImportRecordComponent.class);

    @Autowired
    private SiteStatDao siteStatDao;

    @Autowired
    private RecordSiteStatDao recordSiteStatDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private ResultSetXmlParser parser;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private RecordArticleStatDayDao recordArticleStatDayDao;

    @Autowired
    private RecordArticleStatAllTimeDao recordArticleStatAllTimeDao;

    public void importRecord(String exportFileLocation) throws IOException {
        String exportedRecordFileName = exportFileLocation + "records.xml";
        String siteFileName = exportFileLocation + "site.xml";
        StatResultSet resultSetRecord = parser.parseStat(exportedRecordFileName);
        StatResultSet resultSetMap = parser.parseStat(siteFileName);
        Map<String , String> siteMap = siteMapper.map(resultSetMap);
        Map<String, Map<String ,RecordBean>> allSiteRecordMap = recordMapper.map(resultSetRecord, siteMap);

        List<SiteModel> sites = siteDao.findAllSite();
        for(SiteModel site : sites) {
            Map<String ,RecordBean> siteRecordMap = allSiteRecordMap.get(site.getCode());
            if(siteRecordMap == null) {
                log.debug("Not found export for site {}", site.getCode());
                continue;
            }

            //Import site stat record
            RecordSiteStatModel recordSiteStatModel = recordSiteStatDao.findBySiteId(site.getId());
            if(recordSiteStatModel == null) {
                recordSiteStatModel = new RecordSiteStatModel();
            }

            RecordBean uniqueVisitorRecord = siteRecordMap.get("unique_visitors_total");
            if(uniqueVisitorRecord.getNumber() > recordSiteStatModel.getUniqueVisitor()) {
                recordSiteStatModel.setUniqueVisitor(uniqueVisitorRecord.getNumber());
                recordSiteStatModel.setUniqueVisitorDate(DateTimeFormatUtil.parseDateTime(uniqueVisitorRecord.getDate()));
            }

            RecordBean pageViewRecord = siteRecordMap.get("page_views_total");
            if(pageViewRecord.getNumber() > recordSiteStatModel.getPageView()) {
                recordSiteStatModel.setPageView(pageViewRecord.getNumber());
                recordSiteStatModel.setPageViewDate(DateTimeFormatUtil.parseDateTime(pageViewRecord.getDate()));
            }

            RecordBean visitRecord = siteRecordMap.get("sessions_total");
            if(visitRecord.getNumber() > recordSiteStatModel.getVisit()) {
                recordSiteStatModel.setVisit(visitRecord.getNumber());
                recordSiteStatModel.setVisitDate(DateTimeFormatUtil.parseDateTime(visitRecord.getDate()));
            }

            recordSiteStatModel.setSite(site);
            recordSiteStatDao.save(recordSiteStatModel);
        }
    }
}

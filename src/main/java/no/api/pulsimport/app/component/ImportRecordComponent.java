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

            //Import article daly record
            RecordArticleStatDayModel recordArticleStatDayModel = recordArticleStatDayDao.findBySiteId(site.getId());
            if(recordArticleStatDayModel == null) {
                recordArticleStatDayModel = new RecordArticleStatDayModel();
            }

            RecordBean uniqueVisitorsArticleDaily = siteRecordMap.get("unique_visitors_article_daily");
            if(uniqueVisitorsArticleDaily.getNumber() > recordArticleStatDayModel.getUniqueVisitor()) {
                recordArticleStatDayModel.setUniqueVisitor(uniqueVisitorsArticleDaily.getNumber());
                recordArticleStatDayModel.setUniqueVisitorDate(DateTimeFormatUtil.parseDateTime(uniqueVisitorsArticleDaily.getDate()));
                recordArticleStatDayModel.setUniqueVisitorArticleId(uniqueVisitorsArticleDaily.getArticalId());
                recordArticleStatDayModel.setUniqueVisitorArticleTitle(uniqueVisitorsArticleDaily.getArticleTitle());
                recordArticleStatDayModel.setUniqueVisitorArticleUrl(uniqueVisitorsArticleDaily.getUrl());
                recordArticleStatDayModel.setUniqueVisitorArticleId(uniqueVisitorsArticleDaily.getArticalId());
            }

            RecordBean pageViewArticleDaily = siteRecordMap.get("page_views_article_daily");
            if(pageViewArticleDaily.getNumber() > recordArticleStatDayModel.getPageView()) {
                recordArticleStatDayModel.setPageView(pageViewArticleDaily.getNumber());
                recordArticleStatDayModel.setPageViewDate(DateTimeFormatUtil.parseDateTime(pageViewArticleDaily.getDate()));
                recordArticleStatDayModel.setPageViewArticleId(pageViewArticleDaily.getArticalId());
                recordArticleStatDayModel.setPageViewArticleTitle(pageViewArticleDaily.getArticleTitle());
                recordArticleStatDayModel.setPageViewArticleUrl(pageViewArticleDaily.getUrl());
            }

            RecordBean visitArticleDaily = siteRecordMap.get("sessions_article_daily");
            if(visitArticleDaily.getNumber() > recordArticleStatDayModel.getVisit()) {
                recordArticleStatDayModel.setVisit(visitArticleDaily.getNumber());
                recordArticleStatDayModel.setVisitDate(DateTimeFormatUtil.parseDateTime(visitArticleDaily.getDate()));
                recordArticleStatDayModel.setVisitArticleId(visitArticleDaily.getArticalId());
                recordArticleStatDayModel.setVisitArticleTitle(visitArticleDaily.getArticleTitle());
                recordArticleStatDayModel.setVisitArticleUrl(visitArticleDaily.getUrl());
            }

            recordArticleStatDayDao.save(recordArticleStatDayModel);

            //import article alltime
            RecordArticleStatAllTimeModel recordArticleStatAllTimeModel = recordArticleStatAllTimeDao.findBySiteId(site.getId());
            if(recordArticleStatAllTimeModel == null) {
                recordArticleStatAllTimeModel = new RecordArticleStatAllTimeModel();
            }

            RecordBean uniqueVisitorsArticleAlltime = siteRecordMap.get("unique_visitors_article_total");
            if(uniqueVisitorsArticleAlltime.getNumber() > recordArticleStatAllTimeModel.getUniqueVisitor()) {
                recordArticleStatAllTimeModel.setUniqueVisitor(uniqueVisitorsArticleAlltime.getNumber());
                recordArticleStatAllTimeModel.setUniqueVisitorArticleId(uniqueVisitorsArticleAlltime.getArticalId());
                recordArticleStatAllTimeModel.setUniqueVisitorArticleUrl(uniqueVisitorsArticleAlltime.getUrl());
                recordArticleStatAllTimeModel.setUniqueVisitorArticleTitle(uniqueVisitorsArticleAlltime.getArticleTitle());
            }

            RecordBean pageViewsArticleAlltime = siteRecordMap.get("page_views_article_total");
            if(pageViewsArticleAlltime.getNumber() > recordArticleStatAllTimeModel.getPageView()) {
                recordArticleStatAllTimeModel.setPageView(pageViewsArticleAlltime.getNumber());
                recordArticleStatAllTimeModel.setPageViewArticleTitle(pageViewsArticleAlltime.getArticleTitle());
                recordArticleStatAllTimeModel.setPageViewArticleId(pageViewsArticleAlltime.getArticalId());
                recordArticleStatAllTimeModel.setPageViewArticleUrl(pageViewsArticleAlltime.getUrl());
            }

            RecordBean visitArticleAlltime = siteRecordMap.get("sessions_article_total");
            if(visitArticleAlltime.getNumber() > recordArticleStatAllTimeModel.getVisit()) {
                recordArticleStatAllTimeModel.setVisit(visitArticleAlltime.getNumber());
                recordArticleStatAllTimeModel.setVisitArticleId(visitArticleAlltime.getArticalId());
                recordArticleStatAllTimeModel.setVisitArticleUrl(visitArticleAlltime.getUrl());
                recordArticleStatAllTimeModel.setVisitArticleTitle(visitArticleAlltime.getArticleTitle());
            }

            recordArticleStatAllTimeDao.save(recordArticleStatAllTimeModel);
        }
    }
}

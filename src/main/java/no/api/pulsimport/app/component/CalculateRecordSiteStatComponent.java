package no.api.pulsimport.app.component;

import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.dao.RecordSiteStatDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.dao.SiteStatDao;
import no.api.pulsimport.app.model.RecordSiteStatModel;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.model.SiteStatModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CalculateRecordSiteStatComponent {


    @Autowired
    private SiteStatDao siteStatDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private RecordSiteStatDao recordSiteStatDao;

    private static final Logger log = LoggerFactory.getLogger(CalculateRecordSiteStatComponent.class);

    public void calculateRecordForSiteStat(){
        List<SiteModel> siteModelList = siteDao.findAllSite();
        DateTime todayHour = new DateTime().toDateMidnight().toDateTime();
        DateTime yesterday = todayHour.minusDays(1);

        for(SiteModel siteModel: siteModelList){
            SiteStatModel latestHourModel = siteStatDao.findLatestHourByDate(siteModel.getId(), yesterday);
            List<SiteStatModel> yesterdaySiteStat = siteStatDao.findByDateAndSiteId(yesterday, siteModel.getId());

            if (latestHourModel == null || yesterdaySiteStat.size() == 0) {
                log.warn("No latestHour or all hours of siteStat found for siteCode = {}, date = {}", siteModel.getCode(), yesterday);
            }

            if(latestHourModel != null && yesterdaySiteStat.size() != 0){
                // pageView cannot be used from the latest hour, have to sum the page view from every hour of stat
                int pageViewSum = 0;
                for (SiteStatModel each : yesterdaySiteStat) {
                    pageViewSum += each.getPageView();
                }

                log.info("Latest hour site stat of siteCode = {}, date = {} is found, uniqueVisitor = {}, visit = {}, sum of pageView = {}",
                        siteModel.getCode(), yesterday, latestHourModel.getUniqueVisitor(),
                        latestHourModel.getVisit(), pageViewSum);

                RecordSiteStatModel recordSiteStatTotal = recordSiteStatDao.findBySiteId(siteModel.getId());

                if(recordSiteStatTotal!=null){
                    boolean shouldUpdateRecord = false;

                    if(latestHourModel.getUniqueVisitor() > recordSiteStatTotal.getUniqueVisitor()){
                        shouldUpdateRecord = true;
                        recordSiteStatTotal.setUniqueVisitor(latestHourModel.getUniqueVisitor());
                        recordSiteStatTotal.setUniqueVisitorDate(yesterday);
                        log.info("Site code = {} got new record for unique visitor", siteModel.getCode());
                    }
                    if(pageViewSum > recordSiteStatTotal.getPageView()){
                        shouldUpdateRecord = true;
                        recordSiteStatTotal.setPageView(pageViewSum);
                        recordSiteStatTotal.setPageViewDate(yesterday);
                        log.info("Site code = {} got new record for page view", siteModel.getCode());
                    }
                    if(latestHourModel.getVisit() > recordSiteStatTotal.getVisit()){
                        shouldUpdateRecord = true;
                        recordSiteStatTotal.setVisit(latestHourModel.getVisit());
                        recordSiteStatTotal.setVisitDate(yesterday);
                        log.info("Site code = {} got new record for visit", siteModel.getCode());
                    }

                    if (shouldUpdateRecord) {
                        log.info("Saving recordSiteStat for siteCode = {}", siteModel.getCode());
                        recordSiteStatDao.save(recordSiteStatTotal);
                    }
                }else {
                    log.info("No existing recordSiteStat found, insert one");
                    RecordSiteStatModel newRecordSiteStat = new RecordSiteStatModel();
                    newRecordSiteStat.setUniqueVisitor(latestHourModel.getUniqueVisitor());
                    newRecordSiteStat.setUniqueVisitorDate(yesterday);
                    newRecordSiteStat.setPageView(pageViewSum);
                    newRecordSiteStat.setPageViewDate(yesterday);
                    newRecordSiteStat.setVisit(latestHourModel.getVisit());
                    newRecordSiteStat.setVisitDate(yesterday);
                    newRecordSiteStat.setSite(siteModel);
                    recordSiteStatDao.save(newRecordSiteStat);
                }
            }
        }

    }

    public RecordSiteStatModel calculateHighRecordForSiteStatAsPreviousDate(Long siteId,DateTime currentDate){
        SiteModel siteModel = siteDao.findById(siteId);
        RecordSiteStatModel recordSiteStatTotal=new RecordSiteStatModel();
        List<SiteStatModel> siteStatAsPreviousDate=siteStatDao.findPreviousDate(siteModel.getId(),currentDate);
        if(siteStatAsPreviousDate.size()==0){
            log.warn("No record found for siteStat which siteCode={} and date before={}", siteId, currentDate);
            return null;
        }else{
            List<DateTime> PreviousDates = findUniqueDate(siteStatAsPreviousDate);
            for(DateTime prevDate: PreviousDates){
                SiteStatModel latestHourModel = siteStatDao.findLatestHourByDate(siteModel.getId(), prevDate);
                List<SiteStatModel> yesterdaySiteStat = siteStatDao.findByDateAndSiteId(prevDate, siteModel.getId());
                if(latestHourModel != null && yesterdaySiteStat.size() != 0){
                    // pageView cannot be used from the latest hour, have to sum the page view from every hour of stat
                    int pageViewSum = 0;
                    for (SiteStatModel each : yesterdaySiteStat) {
                        pageViewSum += each.getPageView();
                    }

                    if(recordSiteStatTotal.getId()!=null){
                        if(latestHourModel.getUniqueVisitor() > recordSiteStatTotal.getUniqueVisitor()){
                            recordSiteStatTotal.setUniqueVisitor(latestHourModel.getUniqueVisitor());
                            recordSiteStatTotal.setUniqueVisitorDate(prevDate);
                        }
                        if(pageViewSum > recordSiteStatTotal.getPageView()){
                            recordSiteStatTotal.setPageView(pageViewSum);
                            recordSiteStatTotal.setPageViewDate(prevDate);
                        }
                        if(latestHourModel.getVisit() > recordSiteStatTotal.getVisit()){
                            recordSiteStatTotal.setVisit(latestHourModel.getVisit());
                            recordSiteStatTotal.setVisitDate(prevDate);
                        }

                    }else{
                        recordSiteStatTotal.setId(siteId);
                        recordSiteStatTotal.setUniqueVisitor(latestHourModel.getUniqueVisitor());
                        recordSiteStatTotal.setUniqueVisitorDate(prevDate);
                        recordSiteStatTotal.setPageView(pageViewSum);
                        recordSiteStatTotal.setPageViewDate(prevDate);
                        recordSiteStatTotal.setVisit(latestHourModel.getVisit());
                        recordSiteStatTotal.setVisitDate(prevDate);
                        recordSiteStatTotal.setSite(siteModel);
                    }
                }

            }
            log.info("The highest record for site stat of siteCode = {} and date before = {} is found with uniqueVisitor = {}, visit = {}, sum of pageView = {}",
                    siteId, currentDate,recordSiteStatTotal.getUniqueVisitor(),recordSiteStatTotal.getVisit(), recordSiteStatTotal.getPageView());
            return recordSiteStatTotal;
        }
    }
    private List<DateTime> findUniqueDate(List<SiteStatModel> sites){
        List<DateTime> dateList = new ArrayList<DateTime>();
        for (SiteStatModel each : sites) {
            dateList.add(DateTimeFormatUtil.parseDataDate((each.getHour().toDateTime()).toString("yyyy-MM-dd")));
        }
        Set<DateTime> uniqueDateSet = new HashSet<DateTime>(dateList);
        List<DateTime> uniqueDate = new ArrayList(uniqueDateSet);
        Collections.sort(uniqueDate);
        return uniqueDate;
    }

}

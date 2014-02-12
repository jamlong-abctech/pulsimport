package no.api.pulsimport.app.component;

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
import java.util.List;

/**
 * Created by tum on 2/11/14.
 */

@Component
public class CalculateRecordSiteStatComponent {

    @Autowired
    private SiteStatDao siteStatDao;

    @Autowired
    private RecordSiteStatDao recordSiteStatDao;

    @Autowired
    private SiteDao siteDao;

    private static final Logger log = LoggerFactory.getLogger(CalculateRecordSiteStatComponent.class);

    public void calculateSiteStatRecord() {
        Long firstDayInLong = siteStatDao.findFirstDateTime();
        Long lastDayInLong = siteStatDao.findLastDateTime();
        DateTime firstDay = new DateTime(firstDayInLong);
        DateTime lastDay = new DateTime(lastDayInLong);
        DateTime currentDay = firstDay;
        boolean shouldContinue = true;
        while (shouldContinue) {
            calculateRecordForSiteStat(currentDay);
            currentDay = currentDay.plusDays(1);
            if (currentDay.getMillis() == lastDay.toDateMidnight().toDateTime().getMillis()) {
                shouldContinue = false;
            }
        }
    }

    public void calculateRecordForSiteStat(DateTime aDay){

        List<SiteModel> siteModelList = siteDao.findAllSite();

        for(SiteModel siteModel: siteModelList){
            log.debug("Calculating site stats for site : {}, date : {}", siteModel.getCode(), aDay);
            SiteStatModel latestHourModel = siteStatDao.findLatestHourByDate(siteModel.getId(), aDay);
            List<SiteStatModel> yesterdaySiteStat = siteStatDao.findByDateAndSiteId(aDay, siteModel.getId());

            if (latestHourModel == null || yesterdaySiteStat.size() == 0) {
                log.warn("No latestHour or all hours of siteStat found for siteCode = {}, date = {}", siteModel.getCode(), aDay);
            }

            if(latestHourModel != null && yesterdaySiteStat.size() != 0){

                // pageView cannot be used from the latest hour, have to sum the page view from every hour of stat
                int pageViewSum = 0;
                for (SiteStatModel each : yesterdaySiteStat) {
                    pageViewSum += each.getPageView();
                }

                log.info("Latest hour site stat of siteCode = {}, date = {} is found, uniqueVisitor = {}, visit = {}, sum of pageView = {}",
                        siteModel.getCode(), aDay, latestHourModel.getUniqueVisitor(),
                        latestHourModel.getVisit(), pageViewSum);

                RecordSiteStatModel recordSiteStatTotal = recordSiteStatDao.findBySiteId(siteModel.getId());

                if(recordSiteStatTotal!=null){
                    boolean shouldUpdateRecord = false;

                    if(latestHourModel.getUniqueVisitor() > recordSiteStatTotal.getUniqueVisitor()){
                        shouldUpdateRecord = true;
                        recordSiteStatTotal.setUniqueVisitor(latestHourModel.getUniqueVisitor());
                        recordSiteStatTotal.setUniqueVisitorDate(aDay);
                        log.info("Site code = {} got new record for unique visitor", siteModel.getCode());
                    }
                    if(pageViewSum > recordSiteStatTotal.getPageView()){
                        shouldUpdateRecord = true;
                        recordSiteStatTotal.setPageView(pageViewSum);
                        recordSiteStatTotal.setPageViewDate(aDay);
                        log.info("Site code = {} got new record for page view", siteModel.getCode());
                    }
                    if(latestHourModel.getVisit() > recordSiteStatTotal.getVisit()){
                        shouldUpdateRecord = true;
                        recordSiteStatTotal.setVisit(latestHourModel.getVisit());
                        recordSiteStatTotal.setVisitDate(aDay);
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
                    newRecordSiteStat.setUniqueVisitorDate(aDay);
                    newRecordSiteStat.setPageView(pageViewSum);
                    newRecordSiteStat.setPageViewDate(aDay);
                    newRecordSiteStat.setVisit(latestHourModel.getVisit());
                    newRecordSiteStat.setVisitDate(aDay);
                    newRecordSiteStat.setSite(siteModel);
                    recordSiteStatDao.save(newRecordSiteStat);
                }
            }
        }
    }
}

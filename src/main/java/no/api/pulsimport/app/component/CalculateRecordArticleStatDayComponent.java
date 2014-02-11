package no.api.pulsimport.app.component;

import no.api.pulsimport.app.dao.ArticleStatDao;
import no.api.pulsimport.app.dao.RecordArticleStatDayDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.model.ArticleStatModel;
import no.api.pulsimport.app.model.RecordArticleStatDayModel;
import no.api.pulsimport.app.model.SiteModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalculateRecordArticleStatDayComponent {

    private static final Logger log = LoggerFactory.getLogger(CalculateRecordArticleStatDayComponent.class);

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private ArticleStatDao articleStatDao;

    @Autowired
    private RecordArticleStatDayDao recordArticleStatDayDao;

    public void calculateArticleStatDauRecord() {
        Long firstDayInLong = articleStatDao.findFirstDateTime();
        Long lastDayInLong = articleStatDao.findLastDateTime();
        DateTime firstDay = new DateTime(firstDayInLong);
        DateTime lastDay = new DateTime(lastDayInLong);
        DateTime currentDay = firstDay;
        boolean shouldContinue = true;
        while (shouldContinue) {
            calculateRecordForArticleStat(currentDay);
            currentDay = currentDay.plusDays(1);
            if (currentDay == lastDay.toDateMidnight().toDateTime()) {
                shouldContinue = false;
            }
        }
    }

    public void calculateRecordForArticleStat(DateTime aDay){
        List<SiteModel> siteModelList = siteDao.findAllSite();

        for(SiteModel siteModel: siteModelList){
        //for(int i=0;i<=3; i++){
            //SiteModel siteModel = siteModelList.get(i);
            ArticleStatModel uniqueVisitorModel = articleStatDao.findHighestUniqueVisitorByDateAndSite(
                    siteModel.getId(), aDay);
            ArticleStatModel pageViewModel = articleStatDao.findHighestPageViewByDateAndSite(
                    siteModel.getId(), aDay);
            ArticleStatModel visitModel = articleStatDao.findHighestVisitByDateAndSite(
                    siteModel.getId(), aDay);

            boolean isHighestArticleModelsFound = uniqueVisitorModel != null && pageViewModel != null && visitModel != null;

            if (! isHighestArticleModelsFound) {
                log.warn("There is no highest article models found for siteCode = {}, skipped", siteModel.getCode());
                continue;
            }

            RecordArticleStatDayModel recordArticleStatDay = recordArticleStatDayDao.findBySiteId(siteModel.getId());

            log.info("Found articleStatModels for siteCode = {}", siteModel.getCode());
            log.info("Found uniqueVisitorModel, uniqueVisitor = {}, articleUrl = {}, articleTitle = {}, articleId = {}",
                    uniqueVisitorModel.getUniqueVisitor(), uniqueVisitorModel.getArticleUrl(),
                    uniqueVisitorModel.getArticleTitle(), uniqueVisitorModel.getArticleId());
            log.info("Found pageViewModel, pageView = {}, articleUrl = {}, articleTitle = {}, articleId = {}",
                    pageViewModel.getPageView(), pageViewModel.getArticleUrl(), pageViewModel.getArticleTitle(),
                    pageViewModel.getArticleId());
            log.info("Found visitModel, visit = {}, articleUrl = {}, articleTitle = {}, articleId = {}",
                    visitModel.getVisit(), visitModel.getArticleUrl(), visitModel.getArticleTitle(),
                    visitModel.getArticleId());

            if(recordArticleStatDay!=null){
                if(visitModel.getVisit() > recordArticleStatDay.getVisit()){
                    log.info("Found higher record for article stat day for 'visit'");
                    recordArticleStatDay.setVisit(visitModel.getVisit());
                    recordArticleStatDay.setVisitDate(aDay);
                    recordArticleStatDay.setVisitArticleUrl(visitModel.getArticleUrl());
                    recordArticleStatDay.setVisitArticleTitle(visitModel.getArticleTitle());
                    recordArticleStatDay.setVisitArticleId(visitModel.getArticleId());
                }
                if(pageViewModel.getPageView() > recordArticleStatDay.getPageView()){
                    log.info("Found higher record for article stat day for 'pageView'");
                    recordArticleStatDay.setPageView(pageViewModel.getPageView());
                    recordArticleStatDay.setPageViewDate(aDay);
                    recordArticleStatDay.setPageViewArticleUrl(pageViewModel.getArticleUrl());
                    recordArticleStatDay.setPageViewArticleTitle(pageViewModel.getArticleTitle());
                    recordArticleStatDay.setPageViewArticleId(pageViewModel.getArticleId());
                }
                if(uniqueVisitorModel.getUniqueVisitor() > recordArticleStatDay.getUniqueVisitor()){
                    log.info("Found higher record for article stat day for 'uniqueVisitor'");
                    recordArticleStatDay.setUniqueVisitor(uniqueVisitorModel.getUniqueVisitor());
                    recordArticleStatDay.setUniqueVisitorDate(aDay);
                    recordArticleStatDay.setUniqueVisitorArticleUrl(uniqueVisitorModel.getArticleUrl());
                    recordArticleStatDay.setUniqueVisitorArticleTitle(uniqueVisitorModel.getArticleTitle());
                    recordArticleStatDay.setUniqueVisitorArticleId(uniqueVisitorModel.getArticleId());
                }

                log.info("Save recordArticleStatDay for siteCode = {}", siteModel.getCode());
                recordArticleStatDayDao.save(recordArticleStatDay);
            }else {
                log.info("No existing record found, will insert one");

                RecordArticleStatDayModel newRecordArticleStat = new RecordArticleStatDayModel();
                newRecordArticleStat.setUniqueVisitor(uniqueVisitorModel.getUniqueVisitor());
                newRecordArticleStat.setUniqueVisitorDate(aDay);
                newRecordArticleStat.setUniqueVisitorArticleUrl(uniqueVisitorModel.getArticleUrl());
                newRecordArticleStat.setUniqueVisitorArticleTitle(uniqueVisitorModel.getArticleTitle());
                newRecordArticleStat.setUniqueVisitorArticleId(uniqueVisitorModel.getArticleId());
                newRecordArticleStat.setVisit(visitModel.getVisit());
                newRecordArticleStat.setVisitDate(aDay);
                newRecordArticleStat.setVisitArticleUrl(visitModel.getArticleUrl());
                newRecordArticleStat.setVisitArticleTitle(visitModel.getArticleTitle());
                newRecordArticleStat.setVisitArticleId(visitModel.getArticleId());
                newRecordArticleStat.setPageView(pageViewModel.getPageView());
                newRecordArticleStat.setPageViewDate(aDay);
                newRecordArticleStat.setPageViewArticleUrl(pageViewModel.getArticleUrl());
                newRecordArticleStat.setPageViewArticleTitle(pageViewModel.getArticleTitle());
                newRecordArticleStat.setPageViewArticleId(pageViewModel.getArticleId());
                newRecordArticleStat.setSite(siteModel);
                recordArticleStatDayDao.save(newRecordArticleStat);
            }
        }
    }
}

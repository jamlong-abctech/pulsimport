package no.api.pulsimport.app.component;

import no.api.pulsimport.app.bean.TotalOfArticleBean;
import no.api.pulsimport.app.dao.ArticleStatDao;
import no.api.pulsimport.app.dao.RecordArticleStatAllTimeDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.dao.SiteStatDao;
import no.api.pulsimport.app.model.RecordArticleStatAllTimeModel;
import no.api.pulsimport.app.model.SiteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalculateRecordArticleStatAllTimeComponent {

    private static final Logger log = LoggerFactory.getLogger(CalculateRecordArticleStatAllTimeComponent.class);

    @Autowired
    private ArticleStatDao articleStatDao;

    @Autowired
    private RecordArticleStatAllTimeDao recordArticleStatAllTimeDao;


    @Autowired
    private SiteDao siteDao;

    public void calculateRecordForArticleStatAllTime() {
        log.info("Starting calculateRecordForArticleStatAllTime");
        List<SiteModel> siteModelList = siteDao.findAllSite();

        for (SiteModel siteModel : siteModelList) {


            TotalOfArticleBean totalOfUniqueVisitor = articleStatDao.getTotalOfUniqueVisitor(siteModel.getId());
            TotalOfArticleBean totalOfVisitor = articleStatDao.getTotalOfVisitor(siteModel.getId());
            TotalOfArticleBean totalOfPageView = articleStatDao.getTotalOfPageView(siteModel.getId());

            RecordArticleStatAllTimeModel existedRecord = recordArticleStatAllTimeDao.findBySiteId(siteModel.getId());

            boolean isTopArticleFound = totalOfUniqueVisitor != null && totalOfVisitor != null && totalOfPageView != null;

            if (! isTopArticleFound) {
                log.warn("No any all time highest article found in articlestat for siteCode = {}, skipped", siteModel.getCode());
                continue;
            }

            log.info("The all time highest article found for siteCode = {}", siteModel.getCode());
            log.info("Top article for uniqueVisitor, articleId = {}, articleTitle = {}, articleUrl = {}, total = {}",
                    totalOfUniqueVisitor.getArticleId(), totalOfUniqueVisitor.getArticleTitle(),
                    totalOfUniqueVisitor.getArticleUrl(), totalOfUniqueVisitor.getTotal());
            log.info("Top article for visit, articleId = {}, articleTitle = {}, articleUrl = {}, total = {}",
                    totalOfVisitor.getArticleId(), totalOfVisitor.getArticleTitle(),
                    totalOfVisitor.getArticleUrl(), totalOfVisitor.getTotal());
            log.info("Top article for pageView, articleId = {}, articleTitle = {}, articleUrl = {}, total = {}",
                    totalOfPageView.getArticleId(), totalOfPageView.getArticleTitle(),
                    totalOfPageView.getArticleUrl(), totalOfPageView.getTotal());

            if(isTopArticleFound){
                if (existedRecord != null) {
                    boolean isChanged = false;

                    if (totalOfUniqueVisitor.getTotal() > existedRecord.getUniqueVisitor()) {
                        isChanged = true;
                        log.info("Got new highest uniqueVisitor");
                        existedRecord.setUniqueVisitor(totalOfUniqueVisitor.getTotal());
                        existedRecord.setUniqueVisitorArticleId(totalOfUniqueVisitor.getArticleId());
                        existedRecord.setUniqueVisitorArticleUrl(totalOfUniqueVisitor.getArticleUrl());
                        existedRecord.setUniqueVisitorArticleTitle(totalOfUniqueVisitor.getArticleTitle());
                    }
                    if (totalOfVisitor.getTotal() > existedRecord.getVisit()) {
                        isChanged = true;
                        log.info("Got new highest visit");
                        existedRecord.setVisit(totalOfVisitor.getTotal());
                        existedRecord.setVisitArticleId(totalOfVisitor.getArticleId());
                        existedRecord.setVisitArticleUrl(totalOfVisitor.getArticleUrl());
                        existedRecord.setVisitArticleTitle(totalOfVisitor.getArticleTitle());
                    }
                    if (totalOfPageView.getTotal() > existedRecord.getPageView()) {
                        isChanged = true;
                        log.info("Got new highest pageView");
                        existedRecord.setPageView(totalOfPageView.getTotal());
                        existedRecord.setPageViewArticleId(totalOfPageView.getArticleId());
                        existedRecord.setPageViewArticleUrl(totalOfPageView.getArticleUrl());
                        existedRecord.setPageViewArticleTitle(totalOfPageView.getArticleTitle());
                    }

                    if (isChanged) {
                        log.info("Saving RecordArticleStatAllTime for siteCode = {}", siteModel.getCode());
                        recordArticleStatAllTimeDao.save(existedRecord);
                    }
                } else {
                    log.info("Create new row in RecordArticleStatAllTime table with  site code = {} ", siteModel.getCode());
                    RecordArticleStatAllTimeModel newRecord = new RecordArticleStatAllTimeModel();
                    newRecord.setUniqueVisitor(totalOfUniqueVisitor.getTotal());
                    newRecord.setUniqueVisitorArticleId(totalOfUniqueVisitor.getArticleId());
                    newRecord.setUniqueVisitorArticleUrl(totalOfUniqueVisitor.getArticleUrl());
                    newRecord.setUniqueVisitorArticleTitle(totalOfUniqueVisitor.getArticleTitle());
                    newRecord.setPageView(totalOfPageView.getTotal());
                    newRecord.setPageViewArticleId(totalOfPageView.getArticleId());
                    newRecord.setPageViewArticleUrl(totalOfPageView.getArticleUrl());
                    newRecord.setPageViewArticleTitle(totalOfPageView.getArticleTitle());
                    newRecord.setVisit(totalOfVisitor.getTotal());
                    newRecord.setVisitArticleId(totalOfVisitor.getArticleId());
                    newRecord.setVisitArticleUrl(totalOfVisitor.getArticleUrl());
                    newRecord.setVisitArticleTitle(totalOfVisitor.getArticleTitle());
                    newRecord.setSite(siteModel);
                    recordArticleStatAllTimeDao.save(newRecord);
                }
            }
        }
        log.info("calculateRecordForArticleStatAllTime finish");
    }
}

package no.api.pulsimport.app.component;

import no.api.pulsimport.app.bean.ArticleStatResultSet;
import no.api.pulsimport.app.dao.ArticleStatDao;
import no.api.pulsimport.app.dao.ReportSiteDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import no.api.pulsimport.app.mapper.ArticleStatMapper;
import no.api.pulsimport.app.model.ArticleStatModel;
import no.api.pulsimport.app.model.ReportSiteModel;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.parser.ArticleStatXmlParser;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */

@Component
public class ArticleImportComponent {

    private static final Logger log = LoggerFactory.getLogger(ArticleImportComponent.class);

    //private static String baseExportedPath = "/home/jamlong/puls/import/xml/";


    private static String pulsTotalDesktopSiteCode = "pulstotal";
    private static String pulsTotalMobileSiteCode = "m-pulstotal";
    private static String pulsTotalCombineSiteCode = "c-pulstotal";

    private static String amediaTotalDesktopSiteCode = "amediatotal";
    private static String amediaTotalMobileSiteCode = "m-amediatotal";
    private static String amediaTotalCombineSiteCode = "c-amediatotal";

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private ArticleStatDao articleStatDao;

    @Autowired
    private ArticleStatXmlParser parser;

    @Autowired
    private ArticleStatMapper mapper;

    @Autowired
    private ReportSiteDao reportSiteDao;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void importArticleStat(String exportFileLocation) throws IOException {
        log.debug("Import siteStat started");
        DateTime startTime = DateTime.now();

        Map<String, ArticleStatModel> amediaTotalDesktopMap = new HashMap<>();
        Map<String, ArticleStatModel> amediaTotalMobileMap = new HashMap<>();

        Map<String, ArticleStatModel> pulsTotalDesktopMap = new HashMap<>();
        Map<String, ArticleStatModel> pulsTotalMobileMap = new HashMap<>();

        List<SiteModel> sites = siteDao.findByDevice(SiteDeviceEnum.DESKTOP);

        SiteModel pulsTotalDesktopSite = siteDao.findByCode(pulsTotalDesktopSiteCode);
        SiteModel pulsTotalMobileSite = siteDao.findByCode(pulsTotalMobileSiteCode);
        SiteModel pulsTotalCombineleSite = siteDao.findByCode(pulsTotalCombineSiteCode);

        SiteModel amediaTotalDesktopSite = siteDao.findByCode(amediaTotalDesktopSiteCode);
        SiteModel amediaTotalMobileSite = siteDao.findByCode(amediaTotalMobileSiteCode);
        SiteModel amediaTotalCombineleSite = siteDao.findByCode(amediaTotalCombineSiteCode);

        int ii=0;
        for(SiteModel site : sites) {
            if(ii<=10){
            log.debug("Importing sitestat for {}", site.getCode());
            SiteModel desktopSite = siteDao.findByCode(site.getCode());
            SiteModel desktopPlusSite = siteDao.findByCode(site.getCode()+"+");
            SiteModel mobileSite = siteDao.findByCode("m-"+site.getCode());
            SiteModel mobilePlusSite = siteDao.findByCode("m-"+site.getCode()+"+");
            SiteModel combineSite = siteDao.findByCode("c-"+site.getCode());
            SiteModel combinePlusSite = siteDao.findByCode("c-"+site.getCode()+"+");
            try {
                String desktopExportName = exportFileLocation + "stats_article_" + site.getCode() + ".xml";
                String desktopPlusExportName = exportFileLocation + "stats_article_" + site.getCode()+"+" + ".xml";
                String mobileExportedName = exportFileLocation + "stats_article_m-" + site.getCode() + ".xml";
                String mobilePlusExportedName = exportFileLocation + "stats_article_m-" + site.getCode()+"+" + ".xml";


                ArticleStatResultSet resultSetDesktop = parser.parseArticleStat(desktopExportName);
                ArticleStatResultSet resultSetMobile = parser.parseArticleStat(mobileExportedName);

                List<ArticleStatModel> articleStatDesktopModels =  mapper.map(resultSetDesktop, desktopSite);
                List<ArticleStatModel> articleStatMobileModels =  mapper.map(resultSetMobile, mobileSite);

                List<ArticleStatModel> combineStats = calculateCombineStat(articleStatDesktopModels, articleStatMobileModels, combineSite);


                articleStatDao.batchInsert(articleStatDesktopModels);
                articleStatDao.batchInsert(articleStatMobileModels);
                articleStatDao.batchInsert(combineStats);

                //Calculate total report for desktop
                List<ReportSiteModel> reportSiteModelList = reportSiteDao.findBySiteId(site.getId());
                boolean isIncludeBothReport = reportSiteModelList.size() > 1;
                for(ArticleStatModel eachStat : articleStatDesktopModels) {
                    ArticleStatModel statInMap = pulsTotalDesktopMap.get(eachStat.getDate().getMillis()+","+eachStat.getArticleId());
                    if(statInMap == null) {
                        ArticleStatModel newStat = new ArticleStatModel();
                        newStat.setDate(eachStat.getDate());
                        newStat.setUniqueVisitor(eachStat.getUniqueVisitor());
                        newStat.setPageView(eachStat.getPageView());
                        newStat.setVisit(eachStat.getVisit());
                        newStat.setArticleId(eachStat.getArticleId());
                        newStat.setArticleTitle(eachStat.getArticleTitle());
                        newStat.setArticleUrl(eachStat.getArticleUrl());
                        newStat.setSite(pulsTotalDesktopSite);

                        pulsTotalDesktopMap.put(eachStat.getDate().getMillis()+","+eachStat.getArticleId(), newStat);
                    } else {
                        statInMap.setUniqueVisitor(statInMap.getUniqueVisitor() + eachStat.getUniqueVisitor());
                        statInMap.setPageView(statInMap.getPageView()+ eachStat.getPageView());
                        statInMap.setVisit(statInMap.getVisit() + eachStat.getVisit());
                        statInMap.setArticleId(statInMap.getArticleId());
                        statInMap.setArticleTitle(statInMap.getArticleTitle());
                        statInMap.setArticleUrl(statInMap.getArticleUrl());
                    }
                    if(isIncludeBothReport) {
                        ArticleStatModel statInAmediaMap = amediaTotalDesktopMap.get(eachStat.getDate().getMillis()+","+eachStat.getArticleId());
                        if(statInAmediaMap == null) {
                            ArticleStatModel newStat = new ArticleStatModel();
                            newStat.setDate(eachStat.getDate());
                            newStat.setUniqueVisitor(eachStat.getUniqueVisitor());
                            newStat.setPageView(eachStat.getPageView());
                            newStat.setVisit(eachStat.getVisit());
                            newStat.setArticleId(eachStat.getArticleId());
                            newStat.setArticleTitle(eachStat.getArticleTitle());
                            newStat.setArticleUrl(eachStat.getArticleUrl());
                            newStat.setSite(amediaTotalDesktopSite);

                            amediaTotalDesktopMap.put(eachStat.getDate().getMillis()+","+eachStat.getArticleId(), newStat);
                        } else {
                            statInAmediaMap.setUniqueVisitor(statInAmediaMap.getUniqueVisitor() + eachStat.getUniqueVisitor());
                            statInAmediaMap.setPageView(statInAmediaMap.getPageView()+ eachStat.getPageView());
                            statInAmediaMap.setVisit(statInAmediaMap.getVisit() + eachStat.getVisit());
                            statInMap.setArticleId(statInMap.getArticleId());
                            statInMap.setArticleTitle(statInMap.getArticleTitle());
                            statInMap.setArticleUrl(statInMap.getArticleUrl());
                        }
                    }
                }
                //END Calculate total report for desktop

                //Calculate total report for mobile
                for(ArticleStatModel eachStat : articleStatMobileModels) {
                    ArticleStatModel statInMap = pulsTotalMobileMap.get(eachStat.getDate().getMillis()+","+eachStat.getArticleId());
                    if(statInMap == null) {
                        ArticleStatModel newStat = new ArticleStatModel();
                        newStat.setDate(eachStat.getDate());
                        newStat.setUniqueVisitor(eachStat.getUniqueVisitor());
                        newStat.setPageView(eachStat.getPageView());
                        newStat.setVisit(eachStat.getVisit());
                        newStat.setArticleId(eachStat.getArticleId());
                        newStat.setArticleTitle(eachStat.getArticleTitle());
                        newStat.setArticleUrl(eachStat.getArticleUrl());
                        newStat.setSite(pulsTotalMobileSite);

                        pulsTotalMobileMap.put(eachStat.getDate().getMillis()+","+eachStat.getArticleId(), newStat);
                    } else {
                        statInMap.setUniqueVisitor(statInMap.getUniqueVisitor() + eachStat.getUniqueVisitor());
                        statInMap.setPageView(statInMap.getPageView()+ eachStat.getPageView());
                        statInMap.setVisit(statInMap.getVisit() + eachStat.getVisit());
                        statInMap.setArticleId(statInMap.getArticleId());
                        statInMap.setArticleTitle(statInMap.getArticleTitle());
                        statInMap.setArticleUrl(statInMap.getArticleUrl());
                    }
                    if(isIncludeBothReport) {
                        ArticleStatModel statInAmediaMap = amediaTotalMobileMap.get(eachStat.getDate().getMillis()+","+eachStat.getArticleId());
                        if(statInAmediaMap == null) {
                            ArticleStatModel newStat = new ArticleStatModel();
                            newStat.setDate(eachStat.getDate());
                            newStat.setUniqueVisitor(eachStat.getUniqueVisitor());
                            newStat.setPageView(eachStat.getPageView());
                            newStat.setVisit(eachStat.getVisit());
                            newStat.setArticleId(eachStat.getArticleId());
                            newStat.setArticleTitle(eachStat.getArticleTitle());
                            newStat.setArticleUrl(eachStat.getArticleUrl());
                            newStat.setSite(amediaTotalMobileSite);

                            amediaTotalMobileMap.put(eachStat.getDate().getMillis()+","+eachStat.getArticleId(), newStat);
                        } else {
                            statInAmediaMap.setUniqueVisitor(statInAmediaMap.getUniqueVisitor() + eachStat.getUniqueVisitor());
                            statInAmediaMap.setPageView(statInAmediaMap.getPageView()+ eachStat.getPageView());
                            statInAmediaMap.setVisit(statInAmediaMap.getVisit() + eachStat.getVisit());
                            statInMap.setArticleId(statInMap.getArticleId());
                            statInMap.setArticleTitle(statInMap.getArticleTitle());
                            statInMap.setArticleUrl(statInMap.getArticleUrl());
                        }
                    }
                }
                //END Calculate total report for mobile

                // Case of this site has paid content
                if(desktopPlusSite != null) {
                    ArticleStatResultSet resultSetDesktopPlus = parser.parseArticleStat(desktopPlusExportName);
                    ArticleStatResultSet resultSetMobilePlus = parser.parseArticleStat(mobilePlusExportedName);

                    List<ArticleStatModel> articleStatDesktopPlusModels =  mapper.map(resultSetDesktopPlus, desktopPlusSite);
                    List<ArticleStatModel> articleStatMobilePlusModels =  mapper.map(resultSetMobilePlus, mobilePlusSite);

                    List<ArticleStatModel> combinePlusStats = calculateCombineStat(articleStatDesktopPlusModels, articleStatMobilePlusModels, combinePlusSite);
                    articleStatDao.batchInsert(articleStatDesktopPlusModels);
                    articleStatDao.batchInsert(articleStatMobilePlusModels);
                    articleStatDao.batchInsert(combinePlusStats);
                }
            } catch (ExportedDataNotFoundException e) {
                log.warn("Not found exported data for site {} ", site.getCode());
            }

            }
            ii++;
        }

        List<ArticleStatModel> pulsTotalDesktopStatList = new ArrayList<>(pulsTotalDesktopMap.values());
        List<ArticleStatModel> pulsTotalMobileStatList = new ArrayList<>(pulsTotalMobileMap.values());
        List<ArticleStatModel> pulsTotalCombineStatList = calculateCombineStat(pulsTotalDesktopStatList, pulsTotalMobileStatList, pulsTotalCombineleSite);

        List<ArticleStatModel> amediaTotalDesktopStatList = new ArrayList<>(amediaTotalDesktopMap.values());
        List<ArticleStatModel> amediaTotalMobileStatList = new ArrayList<>(amediaTotalMobileMap.values());
        List<ArticleStatModel> amediaTotalCombineStatList = calculateCombineStat(amediaTotalDesktopStatList, amediaTotalMobileStatList, amediaTotalCombineleSite);

        articleStatDao.batchInsert(pulsTotalDesktopStatList);
        articleStatDao.batchInsert(pulsTotalMobileStatList);
        articleStatDao.batchInsert(pulsTotalCombineStatList);

        articleStatDao.batchInsert(amediaTotalDesktopStatList);
        articleStatDao.batchInsert(amediaTotalMobileStatList);
        articleStatDao.batchInsert(amediaTotalCombineStatList);

        log.debug("import articlestat finished in {} mil", DateTime.now().getMillis() - startTime.getMillis());

    }

    private List<ArticleStatModel> calculateCombineStat(List<ArticleStatModel> desktopList, List<ArticleStatModel> mobileList,
                                                        SiteModel combineSite) {
        Map<String, ArticleStatModel> combinedMap = new HashMap<>();
        List<ArticleStatModel> combinedStat = new ArrayList<>();
        for(ArticleStatModel statModel : desktopList) {
            combinedMap.put(statModel.getDate().getMillis()+","+statModel.getArticleId(), statModel);
        }

        for(ArticleStatModel statModel : mobileList) {
            if(combinedMap.get(statModel.getDate().getMillis()+","+statModel.getArticleId()) == null) {
                ArticleStatModel combineStat = new ArticleStatModel();
                combineStat.setSite(combineSite);
                combineStat.setDate(statModel.getDate());
                combineStat.setUniqueVisitor(statModel.getUniqueVisitor());
                combineStat.setPageView(statModel.getPageView());
                combineStat.setVisit(statModel.getVisit());
                combineStat.setArticleId(statModel.getArticleId());
                combineStat.setArticleTitle(statModel.getArticleTitle());
                combineStat.setArticleUrl(statModel.getArticleUrl());
                combinedStat.add(combineStat);
            } else {
                ArticleStatModel statFromMap = combinedMap.get(statModel.getDate().getMillis()+","+statModel.getArticleId());
                ArticleStatModel combineStat = new ArticleStatModel();
                combineStat.setSite(combineSite);
                combineStat.setDate(statFromMap.getDate());
                combineStat.setUniqueVisitor(statModel.getUniqueVisitor() + statFromMap.getUniqueVisitor());
                combineStat.setPageView(statModel.getPageView() + statFromMap.getPageView());
                combineStat.setVisit(statModel.getVisit() + statFromMap.getVisit());
                combineStat.setArticleId(statModel.getArticleId());
                combineStat.setArticleTitle(statModel.getArticleTitle());
                combineStat.setArticleUrl(statModel.getArticleUrl());

                combinedStat.add(combineStat);
            }
        }
        return combinedStat;
    }
}

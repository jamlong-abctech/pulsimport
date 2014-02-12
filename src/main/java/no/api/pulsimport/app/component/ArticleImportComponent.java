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

    //@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void importArticleStat(String exportFileLocation) throws IOException {
        log.debug("Import Article Stat Started");
        DateTime startTime = DateTime.now();


        List<SiteModel> sites = siteDao.findByDevice(SiteDeviceEnum.DESKTOP);

        SiteModel pulsTotalDesktopSite = siteDao.findByCode(pulsTotalDesktopSiteCode);
        SiteModel pulsTotalMobileSite = siteDao.findByCode(pulsTotalMobileSiteCode);
        SiteModel pulsTotalCombineleSite = siteDao.findByCode(pulsTotalCombineSiteCode);

        SiteModel amediaTotalDesktopSite = siteDao.findByCode(amediaTotalDesktopSiteCode);
        SiteModel amediaTotalMobileSite = siteDao.findByCode(amediaTotalMobileSiteCode);
        SiteModel amediaTotalCombineleSite = siteDao.findByCode(amediaTotalCombineSiteCode);

        for(SiteModel site : sites) {
            log.debug("Importing articlestat for {}", site.getCode());
            SiteModel desktopSite = siteDao.findByCode(site.getCode());
            SiteModel desktopPlusSite = siteDao.findByCode(site.getCode()+"+");
            SiteModel mobileSite = siteDao.findByCode("m-"+site.getCode());
            SiteModel mobilePlusSite = siteDao.findByCode("m-"+site.getCode()+"+");
            SiteModel combineSite = siteDao.findByCode("c-"+site.getCode());
            SiteModel combinePlusSite = siteDao.findByCode("c-"+site.getCode()+"+");

            Map<String, ArticleStatModel> amediaTotalDesktopMap = new HashMap<>();
            Map<String, ArticleStatModel> amediaTotalMobileMap = new HashMap<>();

            Map<String, ArticleStatModel> pulsTotalDesktopMap = new HashMap<>();
            Map<String, ArticleStatModel> pulsTotalMobileMap = new HashMap<>();


            try {
                String desktopExportName = exportFileLocation + "stats_article_" + site.getCode() + ".xml";
                String desktopPlusExportName = exportFileLocation + "stats_article_" + site.getCode()+"+" + ".xml";
                String mobileExportedName = exportFileLocation + "stats_article_m-" + site.getCode() + ".xml";
                String mobilePlusExportedName = exportFileLocation + "stats_article_m-" + site.getCode()+"+" + ".xml";

                int mb = 1024*1024;
                //Getting the runtime reference from system
                Runtime runtime = Runtime.getRuntime();
                log.info("##### Heap utilization statistics [MB] #####");
                //Print used memory
                log.info("Used Memory:"
                        + (runtime.totalMemory() - runtime.freeMemory()) / mb);
                //Print free memory
                log.info("Free Memory:"
                        + runtime.freeMemory() / mb);
                //Print total available memory
                log.info("Total Memory:" + runtime.totalMemory() / mb);
                //Print Maximum available memory
                log.info("Max Memory:" + runtime.maxMemory() / mb);

                System.gc();
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
                    log.debug("Importing articlestat for TotalDesktopSite in article{}", eachStat.getArticleId());
                    ArticleStatModel statInDB=articleStatDao.findByDateArticleIdAndSiteId(eachStat.getDate(), eachStat.getArticleId(),  pulsTotalDesktopSite.getId());
                    //ArticleStatModel statInMap = pulsTotalDesktopMap.get(eachStat.getDate().getMillis()+","+eachStat.getArticleId());
                    if(statInDB == null) {
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
                        statInDB.setUniqueVisitor(statInDB.getUniqueVisitor() + eachStat.getUniqueVisitor());
                        statInDB.setPageView(statInDB.getPageView()+ eachStat.getPageView());
                        statInDB.setVisit(statInDB.getVisit() + eachStat.getVisit());
                        statInDB.setArticleId(statInDB.getArticleId());
                        statInDB.setArticleTitle(statInDB.getArticleTitle());
                        statInDB.setArticleUrl(statInDB.getArticleUrl());
                        articleStatDao.update(statInDB);
                        //pulsTotalDesktopMap.put(eachStat.getDate().getMillis()+","+eachStat.getArticleId(), statInDB);
                    }



                    if(isIncludeBothReport) {
                        log.debug("Importing articlestat for pulsTotalDesktopSite in article{}", eachStat.getArticleId());
                        ArticleStatModel statInAmediaDB=articleStatDao.findByDateArticleIdAndSiteId(eachStat.getDate(), eachStat.getArticleId(),  amediaTotalDesktopSite.getId());
                        //ArticleStatModel statInAmediaMap = amediaTotalDesktopMap.get(eachStat.getDate().getMillis()+","+eachStat.getArticleId());
                        if(statInAmediaDB == null) {
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
                            statInAmediaDB.setUniqueVisitor(statInAmediaDB.getUniqueVisitor() + eachStat.getUniqueVisitor());
                            statInAmediaDB.setPageView(statInAmediaDB.getPageView()+ eachStat.getPageView());
                            statInAmediaDB.setVisit(statInAmediaDB.getVisit() + eachStat.getVisit());
                            statInAmediaDB.setArticleId(statInAmediaDB.getArticleId());
                            statInAmediaDB.setArticleTitle(statInAmediaDB.getArticleTitle());
                            statInAmediaDB.setArticleUrl(statInAmediaDB.getArticleUrl());
                            articleStatDao.update(statInAmediaDB);
                        }
                    }
                }
                //END Calculate total report for desktop

                //Calculate total report for mobile

                for(ArticleStatModel eachStat : articleStatMobileModels) {
                    log.debug("Importing articlestat for TotalMobileSite in article{}", eachStat.getArticleId());
                    ArticleStatModel statInMobileDB=articleStatDao.findByDateArticleIdAndSiteId(eachStat.getDate(), eachStat.getArticleId(),  pulsTotalMobileSite.getId());
                    //ArticleStatModel statInMap = pulsTotalMobileMap.get(eachStat.getDate().getMillis()+","+eachStat.getArticleId());
                    if(statInMobileDB == null) {
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
                        statInMobileDB.setUniqueVisitor(statInMobileDB.getUniqueVisitor() + eachStat.getUniqueVisitor());
                        statInMobileDB.setPageView(statInMobileDB.getPageView()+ eachStat.getPageView());
                        statInMobileDB.setVisit(statInMobileDB.getVisit() + eachStat.getVisit());
                        statInMobileDB.setArticleId(statInMobileDB.getArticleId());
                        statInMobileDB.setArticleTitle(statInMobileDB.getArticleTitle());
                        statInMobileDB.setArticleUrl(statInMobileDB.getArticleUrl());
                        articleStatDao.update(statInMobileDB);
                    }
                    if(isIncludeBothReport) {
                        ArticleStatModel statInMobileAmediaDB=articleStatDao.findByDateArticleIdAndSiteId(eachStat.getDate(), eachStat.getArticleId(),  amediaTotalMobileSite.getId()) ;
                        //ArticleStatModel statInAmediaMap = amediaTotalMobileMap.get(eachStat.getDate().getMillis()+","+eachStat.getArticleId());
                        if(statInMobileAmediaDB == null) {
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
                            statInMobileAmediaDB.setUniqueVisitor(statInMobileAmediaDB.getUniqueVisitor() + eachStat.getUniqueVisitor());
                            statInMobileAmediaDB.setPageView(statInMobileAmediaDB.getPageView()+ eachStat.getPageView());
                            statInMobileAmediaDB.setVisit(statInMobileAmediaDB.getVisit() + eachStat.getVisit());
                            statInMobileAmediaDB.setArticleId(statInMobileAmediaDB.getArticleId());
                            statInMobileAmediaDB.setArticleTitle(statInMobileAmediaDB.getArticleTitle());
                            statInMobileAmediaDB.setArticleUrl(statInMobileAmediaDB.getArticleUrl());
                            articleStatDao.update(statInMobileAmediaDB);
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

               resultSetDesktop =null;
               resultSetMobile = null;
               articleStatDesktopModels =  null;
               articleStatMobileModels =  null;
               combineStats = null;
               reportSiteModelList = null;
               System.gc();



            } catch (ExportedDataNotFoundException e) {
                log.warn("Not found exported data for site {} ", site.getCode());
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

            log.debug("Importing articlestat finish in site {}", site.getCode());
            desktopSite = null;
            desktopPlusSite = null;
            mobileSite =null;
            mobilePlusSite = null;
            combineSite = null;
            combinePlusSite = null;
            pulsTotalDesktopMap=null;
            pulsTotalMobileMap=null;
            amediaTotalDesktopMap=null;
            amediaTotalMobileMap=null;
            pulsTotalDesktopStatList=null;
            pulsTotalMobileStatList=null;
            pulsTotalCombineStatList=null;

            amediaTotalDesktopStatList=null;
            amediaTotalMobileStatList=null;
            amediaTotalCombineStatList=null;
            System.gc();

        }



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

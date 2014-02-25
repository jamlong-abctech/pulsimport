package no.api.pulsimport.app.component;

import no.api.pulsimport.app.bean.ArticleStatResultSet;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.dao.ArticleStatDao;
import no.api.pulsimport.app.dao.ReportSiteDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import no.api.pulsimport.app.mapper.ArticleStatMapper;
import no.api.pulsimport.app.model.ArticleStatModel;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.parser.ResultSetXmlParser;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private ArticleStatDao articleStatDao;

    @Autowired
    private ResultSetXmlParser parser;

    @Autowired
    private ArticleStatMapper mapper;

    @Autowired
    private ReportSiteDao reportSiteDao;

    public void importArticleStat(String exportFileLocation) throws IOException {
        log.debug("Import Article Stat Started");
        DateTime startTime = DateTime.now();

        List<SiteModel> sites = siteDao.findByDevice(SiteDeviceEnum.DESKTOP);

        for (SiteModel site : sites) {
            log.debug("Importing articlestat for {}", site.getCode());
            SiteModel desktopSite = siteDao.findByCode(site.getCode());
            SiteModel desktopPlusSite = siteDao.findByCode(site.getCode() + "+");
            SiteModel mobileSite = siteDao.findByCode("m-" + site.getCode());
            SiteModel mobilePlusSite = siteDao.findByCode("m-" + site.getCode() + "+");
            SiteModel combineSite = siteDao.findByCode("c-" + site.getCode());
            SiteModel combinePlusSite = siteDao.findByCode("c-" + site.getCode() + "+");
            try {
                String desktopExportName = exportFileLocation + "stats_article_" + site.getCode() + ".xml";
                String desktopPlusExportName = exportFileLocation + "stats_article_" + site.getCode() + "+" + ".xml";
                String mobileExportedName = exportFileLocation + "stats_article_m-" + site.getCode() + ".xml";
                String mobilePlusExportedName = exportFileLocation + "stats_article_m-" + site.getCode() + "+" + ".xml";


                StatResultSet resultSetDesktop = parser.parseStat(desktopExportName);
                StatResultSet resultSetMobile = parser.parseStat(mobileExportedName);

                log.info("Mapping xml object to data model for desktopSite");
                List<ArticleStatModel> articleStatDesktopModels = mapper.map(resultSetDesktop, desktopSite);
                log.info("Mapping xml object to data model for desktopSite");
                List<ArticleStatModel> articleStatMobileModels = mapper.map(resultSetMobile, mobileSite);

                log.info("Calculating combine site");
                List<ArticleStatModel> combineStats = calculateCombineStat(articleStatDesktopModels, articleStatMobileModels, combineSite);

                log.info("Inserting desktop article statistic size {}", articleStatDesktopModels.size());
                articleStatDao.batchInsert(articleStatDesktopModels);
                log.info("Inserting mobile article statistic size {}", articleStatDesktopModels.size());
                articleStatDao.batchInsert(articleStatMobileModels);
                log.info("Inserting combine article statistic size {}", articleStatDesktopModels.size());
                articleStatDao.batchInsert(combineStats);

                // Case of this site has paid content
                if (desktopPlusSite != null) {
                    StatResultSet resultSetDesktopPlus = parser.parseStat(desktopPlusExportName);
                    StatResultSet resultSetMobilePlus = parser.parseStat(mobilePlusExportedName);

                    List<ArticleStatModel> articleStatDesktopPlusModels = mapper.map(resultSetDesktopPlus, desktopPlusSite);
                    List<ArticleStatModel> articleStatMobilePlusModels = mapper.map(resultSetMobilePlus, mobilePlusSite);

                    List<ArticleStatModel> combinePlusStats = calculateCombineStat(articleStatDesktopPlusModels, articleStatMobilePlusModels, combinePlusSite);

                    log.info("Inserting paid desktop article statistic size {}", articleStatDesktopModels.size());
                    articleStatDao.batchInsert(articleStatDesktopPlusModels);
                    log.info("Inserting paid mobile article statistic size {}", articleStatDesktopModels.size());
                    articleStatDao.batchInsert(articleStatMobilePlusModels);
                    log.info("Inserting paid combine article statistic size {}", articleStatDesktopModels.size());
                    articleStatDao.batchInsert(combinePlusStats);
                }
            } catch (ExportedDataNotFoundException e) {
                log.warn("Not found exported data for site {} ", site.getCode());
            }
        }

        log.debug("import articlestat finished in {} mil", DateTime.now().getMillis() - startTime.getMillis());

    }

    private List<ArticleStatModel> calculateCombineStat(List<ArticleStatModel> desktopList, List<ArticleStatModel> mobileList,
                                                        SiteModel combineSite) {
        Map<String, ArticleStatModel> combinedMap = new HashMap<>();
        List<ArticleStatModel> combinedStat = new ArrayList<>();
        for (ArticleStatModel statModel : desktopList) {
            combinedMap.put(statModel.getDate().getMillis() + "," + statModel.getArticleId(), statModel);
        }

        for (ArticleStatModel statModel : mobileList) {
            if (combinedMap.get(statModel.getDate().getMillis() + "," + statModel.getArticleId()) == null) {
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
                ArticleStatModel statFromMap = combinedMap.get(statModel.getDate().getMillis() + "," + statModel.getArticleId());
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

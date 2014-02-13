package no.api.pulsimport.app.component;

import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.dao.ReportSiteDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.dao.SiteStatDao;
import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import no.api.pulsimport.app.mapper.SiteStatMapper;
import no.api.pulsimport.app.model.ReportSiteModel;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.model.SiteStatModel;
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
public class SiteStatImportComponent {

    private static final Logger log = LoggerFactory.getLogger(SiteStatImportComponent.class);

    private static final String pulsTotalDesktopSiteCode = "pulstotal";
    private static final String pulsTotalMobileSiteCode = "m-pulstotal";
    private static final String pulsTotalCombineSiteCode = "c-pulstotal";

    private static final String amediaTotalDesktopSiteCode = "amediatotal";
    private static final String amediaTotalMobileSiteCode = "m-amediatotal";
    private static final String amediaTotalCombineSiteCode = "c-amediatotal";

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private SiteStatDao siteStatDao;

    @Autowired
    private ResultSetXmlParser parser;

    @Autowired
    private SiteStatMapper mapper;

    @Autowired
    private ReportSiteDao reportSiteDao;

    //@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void importSiteStat(String exportFileLocation) throws IOException {
        log.debug("Import siteStat started");
        DateTime startTime = DateTime.now();

        Map<Long, SiteStatModel> amediaTotalDesktopMap = new HashMap<>();
        Map<Long, SiteStatModel> amediaTotalMobileMap = new HashMap<>();

        Map<Long, SiteStatModel> pulsTotalDesktopMap = new HashMap<>();
        Map<Long, SiteStatModel> pulsTotalMobileMap = new HashMap<>();

        List<SiteModel> sites = siteDao.findByDevice(SiteDeviceEnum.DESKTOP);

        SiteModel pulsTotalDesktopSite = siteDao.findByCode(pulsTotalDesktopSiteCode);
        SiteModel pulsTotalMobileSite = siteDao.findByCode(pulsTotalMobileSiteCode);
        SiteModel pulsTotalCombineleSite = siteDao.findByCode(pulsTotalCombineSiteCode);

        SiteModel amediaTotalDesktopSite = siteDao.findByCode(amediaTotalDesktopSiteCode);
        SiteModel amediaTotalMobileSite = siteDao.findByCode(amediaTotalMobileSiteCode);
        SiteModel amediaTotalCombineleSite = siteDao.findByCode(amediaTotalCombineSiteCode);

        for(SiteModel site : sites) {
            log.debug("Importing sitestat for {}", site.getCode());
            SiteModel desktopSite = siteDao.findByCode(site.getCode());
            SiteModel desktopPlusSite = siteDao.findByCode(site.getCode()+"+");
            SiteModel mobileSite = siteDao.findByCode("m-"+site.getCode());
            SiteModel mobilePlusSite = siteDao.findByCode("m-"+site.getCode()+"+");
            SiteModel combineSite = siteDao.findByCode("c-"+site.getCode());
            SiteModel combinePlusSite = siteDao.findByCode("c-"+site.getCode()+"+");
            try {
                String desktopExportName = exportFileLocation + "stats_total_" + site.getCode() + ".xml";
                String desktopPlusExportName = exportFileLocation + "stats_total_" + site.getCode()+"+" + ".xml";
                String mobileExportedName = exportFileLocation + "stats_total_m-" + site.getCode() + ".xml";
                String mobilePlusExportedName = exportFileLocation + "stats_total_m-" + site.getCode()+"+" + ".xml";

                StatResultSet resultSetDesktop = parser.parseStat(desktopExportName);
                StatResultSet resultSetMobile = parser.parseStat(mobileExportedName);

                log.info("Mapping xml object to data model for desktopSite");
                List<SiteStatModel> siteStatDesktopModels =  mapper.map(resultSetDesktop, desktopSite);
                log.info("Mapping xml object to data model for mobileSite");
                List<SiteStatModel> siteStatMobileModels =  mapper.map(resultSetMobile, mobileSite);

                log.info("Calculating combine site");
                List<SiteStatModel> combineStats = calculateCombineStat(siteStatDesktopModels, siteStatMobileModels, combineSite);

                log.info("Inserting desktop site statistic size {}", siteStatDesktopModels.size());
                siteStatDao.batchInsert(siteStatDesktopModels);
                log.info("Inserting mobile site statistic size {}", siteStatDesktopModels.size());
                siteStatDao.batchInsert(siteStatMobileModels);
                log.info("Inserting combine site statistic size {}", siteStatDesktopModels.size());
                siteStatDao.batchInsert(combineStats);

                //Calculate total report for desktop
                List<ReportSiteModel> reportSiteModelList = reportSiteDao.findBySiteId(site.getId());
                boolean isIncludeBothReport = reportSiteModelList.size() > 1;
                for(SiteStatModel eachStat : siteStatDesktopModels) {
                    SiteStatModel statInMap = pulsTotalDesktopMap.get(eachStat.getHour().getMillis());
                    if(statInMap == null) {
                        SiteStatModel newStat = new SiteStatModel();
                        newStat.setHour(eachStat.getHour());
                        newStat.setUniqueVisitor(eachStat.getUniqueVisitor());
                        newStat.setPageView(eachStat.getPageView());
                        newStat.setVisit(eachStat.getVisit());
                        newStat.setVideo(eachStat.getVideo());
                        newStat.setSite(pulsTotalDesktopSite);

                        pulsTotalDesktopMap.put(eachStat.getHour().getMillis(), newStat);
                    } else {
                        statInMap.setUniqueVisitor(statInMap.getUniqueVisitor() + eachStat.getUniqueVisitor());
                        statInMap.setPageView(statInMap.getPageView()+ eachStat.getPageView());
                        statInMap.setVisit(statInMap.getVisit() + eachStat.getVisit());
                        statInMap.setVideo(statInMap.getVideo() + eachStat.getVideo());
                    }
                    if(isIncludeBothReport) {
                        SiteStatModel statInAmediaMap = amediaTotalDesktopMap.get(eachStat.getHour().getMillis());
                        if(statInAmediaMap == null) {
                            SiteStatModel newStat = new SiteStatModel();
                            newStat.setHour(eachStat.getHour());
                            newStat.setUniqueVisitor(eachStat.getUniqueVisitor());
                            newStat.setPageView(eachStat.getPageView());
                            newStat.setVisit(eachStat.getVisit());
                            newStat.setVideo(eachStat.getVideo());
                            newStat.setSite(amediaTotalDesktopSite);

                            amediaTotalDesktopMap.put(eachStat.getHour().getMillis(), newStat);
                        } else {
                            statInAmediaMap.setUniqueVisitor(statInAmediaMap.getUniqueVisitor() + eachStat.getUniqueVisitor());
                            statInAmediaMap.setPageView(statInAmediaMap.getPageView()+ eachStat.getPageView());
                            statInAmediaMap.setVisit(statInAmediaMap.getVisit() + eachStat.getVisit());
                            statInAmediaMap.setVideo(statInAmediaMap.getVideo() + eachStat.getVideo());
                        }
                    }
                }
                //END Calculate total report for desktop

                //Calculate total report for mobile
                for(SiteStatModel eachStat : siteStatMobileModels) {
                    SiteStatModel statInMap = pulsTotalMobileMap.get(eachStat.getHour().getMillis());
                    if(statInMap == null) {
                        SiteStatModel newStat = new SiteStatModel();
                        newStat.setHour(eachStat.getHour());
                        newStat.setUniqueVisitor(eachStat.getUniqueVisitor());
                        newStat.setPageView(eachStat.getPageView());
                        newStat.setVisit(eachStat.getVisit());
                        newStat.setVideo(eachStat.getVideo());
                        newStat.setSite(pulsTotalMobileSite);

                        pulsTotalMobileMap.put(eachStat.getHour().getMillis(), newStat);
                    } else {
                        statInMap.setUniqueVisitor(statInMap.getUniqueVisitor() + eachStat.getUniqueVisitor());
                        statInMap.setPageView(statInMap.getPageView()+ eachStat.getPageView());
                        statInMap.setVisit(statInMap.getVisit() + eachStat.getVisit());
                        statInMap.setVideo(statInMap.getVideo() + eachStat.getVideo());
                    }
                    if(isIncludeBothReport) {
                        SiteStatModel statInAmediaMap = amediaTotalMobileMap.get(eachStat.getHour().getMillis());
                        if(statInAmediaMap == null) {
                            SiteStatModel newStat = new SiteStatModel();
                            newStat.setHour(eachStat.getHour());
                            newStat.setUniqueVisitor(eachStat.getUniqueVisitor());
                            newStat.setPageView(eachStat.getPageView());
                            newStat.setVisit(eachStat.getVisit());
                            newStat.setVideo(eachStat.getVideo());
                            newStat.setSite(amediaTotalMobileSite);

                            amediaTotalMobileMap.put(eachStat.getHour().getMillis(), newStat);
                        } else {
                            statInAmediaMap.setUniqueVisitor(statInAmediaMap.getUniqueVisitor() + eachStat.getUniqueVisitor());
                            statInAmediaMap.setPageView(statInAmediaMap.getPageView()+ eachStat.getPageView());
                            statInAmediaMap.setVisit(statInAmediaMap.getVisit() + eachStat.getVisit());
                            statInAmediaMap.setVideo(statInAmediaMap.getVideo() + eachStat.getVideo());
                        }
                    }
                }
                //END Calculate total report for mobile

                // ** paid contect(+site) NOT include in total report **


                // Case of this site has paid content
                if(desktopPlusSite != null) {
                    StatResultSet resultSetDesktopPlus = parser.parseStat(desktopPlusExportName);
                    StatResultSet resultSetMobilePlus = parser.parseStat(mobilePlusExportedName);

                    List<SiteStatModel> siteStatDesktopPlusModels =  mapper.map(resultSetDesktopPlus, desktopPlusSite);
                    List<SiteStatModel> siteStatMobilePlusModels =  mapper.map(resultSetMobilePlus, mobilePlusSite);

                    List<SiteStatModel> combinePlusStats = calculateCombineStat(siteStatDesktopPlusModels, siteStatMobilePlusModels, combinePlusSite);

                    log.info("Inserting pad desktop site statistic size {}", siteStatDesktopPlusModels.size());
                    siteStatDao.batchInsert(siteStatDesktopPlusModels);
                    log.info("Inserting pad mobile site statistic size {}", siteStatMobilePlusModels.size());
                    siteStatDao.batchInsert(siteStatMobilePlusModels);
                    log.info("Inserting pad combine site statistic size {}", combinePlusStats.size());
                    siteStatDao.batchInsert(combinePlusStats);
                }
            } catch (ExportedDataNotFoundException e) {
                log.warn("Not found exported data for site {} ", site.getCode());
            }
        }

        List<SiteStatModel> pulsTotalDesktopStatList = new ArrayList<>(pulsTotalDesktopMap.values());
        List<SiteStatModel> pulsTotalMobileStatList = new ArrayList<>(pulsTotalMobileMap.values());
        List<SiteStatModel> pulsTotalCombineStatList = calculateCombineStat(pulsTotalDesktopStatList, pulsTotalMobileStatList, pulsTotalCombineleSite);

        List<SiteStatModel> amediaTotalDesktopStatList = new ArrayList<>(amediaTotalDesktopMap.values());
        List<SiteStatModel> amediaTotalMobileStatList = new ArrayList<>(amediaTotalMobileMap.values());
        List<SiteStatModel> amediaTotalCombineStatList = calculateCombineStat(amediaTotalDesktopStatList, amediaTotalMobileStatList, amediaTotalCombineleSite);

        log.info("Inserting puls total desktop site statistic size {}", pulsTotalDesktopStatList.size());
        siteStatDao.batchInsert(pulsTotalDesktopStatList);
        log.info("Inserting puls total mobile site statistic size {}", pulsTotalMobileStatList.size());
        siteStatDao.batchInsert(pulsTotalMobileStatList);
        log.info("Inserting puls total combine site statistic size {}", pulsTotalCombineStatList.size());
        siteStatDao.batchInsert(pulsTotalCombineStatList);

        log.info("Inserting amedia total desktop site statistic size {}", pulsTotalDesktopStatList.size());
        siteStatDao.batchInsert(amediaTotalDesktopStatList);
        log.info("Inserting amedia total mobile site statistic size {}", pulsTotalDesktopStatList.size());
        siteStatDao.batchInsert(amediaTotalMobileStatList);
        log.info("Inserting amedia total combine site statistic size {}", pulsTotalDesktopStatList.size());
        siteStatDao.batchInsert(amediaTotalCombineStatList);

        log.debug("import sitestat finished in {} mil", DateTime.now().getMillis() - startTime.getMillis());
    }

    private List<SiteStatModel> calculateCombineStat(List<SiteStatModel> desktopList, List<SiteStatModel> mobileList,
                                                     SiteModel combineSite) {
        Map<Long, SiteStatModel> combinedMap = new HashMap<>();
        List<SiteStatModel> combinedStat = new ArrayList<>();
        for(SiteStatModel statModel : desktopList) {
            combinedMap.put(statModel.getHour().getMillis(), statModel);
        }

        for(SiteStatModel statModel : mobileList) {
            if(combinedMap.get(statModel.getHour().getMillis()) == null) {
                SiteStatModel combineStat = new SiteStatModel();
                combineStat.setSite(combineSite);
                combineStat.setHour(statModel.getHour());
                combineStat.setUniqueVisitor(statModel.getUniqueVisitor());
                combineStat.setPageView(statModel.getPageView());
                combineStat.setVisit(statModel.getVisit());
                combineStat.setVideo(statModel.getVideo());

                combinedStat.add(combineStat);
            } else {
                SiteStatModel statFromMap = combinedMap.get(statModel.getHour().getMillis());
                SiteStatModel combineStat = new SiteStatModel();
                combineStat.setSite(combineSite);
                combineStat.setHour(statFromMap.getHour());
                combineStat.setUniqueVisitor(statModel.getUniqueVisitor() + statFromMap.getUniqueVisitor());
                combineStat.setPageView(statModel.getPageView() + statFromMap.getPageView());
                combineStat.setVisit(statModel.getVisit() + statFromMap.getVisit());
                combineStat.setVideo(statModel.getVideo() + statFromMap.getVideo());

                combinedStat.add(combineStat);
            }
        }
        return combinedStat;
    }
}

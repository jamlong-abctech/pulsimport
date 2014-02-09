package no.api.pulsimport.app.component;

import no.api.pulsimport.app.bean.SiteStatResultSet;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.dao.SiteStatDao;
import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import no.api.pulsimport.app.mapper.SiteStatMapper;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.model.SiteStatModel;
import no.api.pulsimport.app.parser.SiteStatXmlParser;
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
public class SiteStatImportComponent {

    private static final Logger log = LoggerFactory.getLogger(SiteStatImportComponent.class);

    private static String baseExportedPath = "/usr/puls/exported/";

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private SiteStatDao siteStatDao;

    @Autowired
    private SiteStatXmlParser parser;

    @Autowired
    private SiteStatMapper mapper;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void importSiteStat() throws IOException {
        log.debug("Import siteStat started");
        DateTime startTime = DateTime.now();

        List<SiteModel> sites = siteDao.findByDevice(SiteDeviceEnum.DESKTOP);

        // for testing
//        sites.clear();
//        sites.add(siteDao.findByCode("glomdalen"));
//        sites.add(siteDao.findByCode("rb"));
        // end

        for(SiteModel site : sites) {
            List<SiteStatModel> combineStats = new ArrayList<>();

            SiteModel desktopSite = siteDao.findByCode(site.getCode());
            SiteModel desktopPlusSite = siteDao.findByCode(site.getCode()+"+");
            SiteModel mobileSite = siteDao.findByCode("m-"+site.getCode());
            SiteModel mobilePlusSite = siteDao.findByCode("m-"+site.getCode()+"+");
            SiteModel combineSite = siteDao.findByCode("c-"+site.getCode());
            SiteModel combinePlusSite = siteDao.findByCode("c-"+site.getCode()+"+");
            try {
                String desktopExportName = baseExportedPath + "stats_total_" + site.getCode() + ".xml";
                String desktopPlusExportName = baseExportedPath + "stats_total_" + site.getCode()+"+" + ".xml";
                String mobileExportedName = baseExportedPath + "stats_total_m-" + site.getCode() + ".xml";
                String mobilePlusExportedName = baseExportedPath + "stats_total_m-" + site.getCode()+"+" + ".xml";
                Map<Long, SiteStatModel> combinedMap = new HashMap<>();
                Map<Long, SiteStatModel> combinePlusMap = new HashMap<>();
                SiteStatResultSet resultSetDesktop = parser.parseSiteStat(desktopExportName);

                SiteStatResultSet resultSetMobile = parser.parseSiteStat(mobileExportedName);


                List<SiteStatModel> siteStatDesktopModels =  mapper.map(resultSetDesktop, desktopSite);

                List<SiteStatModel> siteStatMobileModels =  mapper.map(resultSetMobile, mobileSite);

                for(SiteStatModel statModel : siteStatDesktopModels) {
                    combinedMap.put(statModel.getHour().getMillis(), statModel);
                }

                for(SiteStatModel statModel : siteStatMobileModels) {
                    log.debug("Saving site stat data siteCode {}, hour {}", site.getCode(), statModel.getHour());
                    if(combinedMap.get(statModel.getHour().getMillis()) == null) {
                        SiteStatModel combineStat = new SiteStatModel();
                        combineStat.setSite(combineSite);
                        combineStat.setHour(statModel.getHour());
                        combineStat.setUniqueVisitor(statModel.getUniqueVisitor());
                        combineStat.setPageView(statModel.getPageView());
                        combineStat.setVisit(statModel.getVisit());
                        combineStat.setVideo(statModel.getVideo());

                        combineStats.add(combineStat);
                    } else {
                        SiteStatModel statFromMap = combinedMap.get(statModel.getHour().getMillis());
                        SiteStatModel combineStat = new SiteStatModel();
                        combineStat.setSite(combineSite);
                        combineStat.setHour(statFromMap.getHour());
                        combineStat.setUniqueVisitor(statModel.getUniqueVisitor() + statFromMap.getUniqueVisitor());
                        combineStat.setPageView(statModel.getPageView() + statFromMap.getPageView());
                        combineStat.setVisit(statModel.getVisit() + statFromMap.getVisit());
                        combineStat.setVideo(statModel.getVideo() + statFromMap.getVideo());

                        combineStats.add(combineStat);
                    }
                }


                siteStatDao.batchInsert(siteStatDesktopModels);
                siteStatDao.batchInsert(siteStatMobileModels);
                siteStatDao.batchInsert(combineStats);

                // Case of this site has paid content
                if(desktopPlusSite != null) {
                    List<SiteStatModel> combinePlusStats = new ArrayList<>();
                    SiteStatResultSet resultSetDesktopPlus = parser.parseSiteStat(desktopPlusExportName);
                    SiteStatResultSet resultSetMobilePlus = parser.parseSiteStat(mobilePlusExportedName);

                    List<SiteStatModel> siteStatDesktopPlusModels =  mapper.map(resultSetDesktopPlus, desktopPlusSite);
                    List<SiteStatModel> siteStatMobilePlusModels =  mapper.map(resultSetMobilePlus, mobilePlusSite);

                    for(SiteStatModel statModel : siteStatDesktopPlusModels) {
                        combinePlusMap.put(statModel.getHour().getMillis(), statModel);
                    }

                    for(SiteStatModel statModel : siteStatMobilePlusModels) {
                        log.debug("Saving site stat data siteCode {}, hour {}", site.getCode(), statModel.getHour());
                        if(combinePlusMap.get(statModel.getHour().getMillis()) == null) {
                            SiteStatModel combineStat = new SiteStatModel();
                            combineStat.setSite(combinePlusSite);
                            combineStat.setHour(statModel.getHour());
                            combineStat.setUniqueVisitor(statModel.getUniqueVisitor());
                            combineStat.setPageView(statModel.getPageView());
                            combineStat.setVisit(statModel.getVisit());
                            combineStat.setVideo(statModel.getVideo());

                            combineStats.add(combineStat);
                        } else {
                            SiteStatModel statFromMap = combinePlusMap.get(statModel.getHour().getMillis());
                            SiteStatModel combineStat = new SiteStatModel();
                            combineStat.setSite(combinePlusSite);
                            combineStat.setHour(statFromMap.getHour());
                            combineStat.setUniqueVisitor(statModel.getUniqueVisitor() + statFromMap.getUniqueVisitor());
                            combineStat.setPageView(statModel.getPageView() + statFromMap.getPageView());
                            combineStat.setVisit(statModel.getVisit() + statFromMap.getVisit());
                            combineStat.setVideo(statModel.getVideo() + statFromMap.getVideo());

                            combinePlusStats.add(combineStat);
                        }
                    }
                    siteStatDao.batchInsert(siteStatDesktopPlusModels);
                    siteStatDao.batchInsert(siteStatMobilePlusModels);
                    siteStatDao.batchInsert(combinePlusStats);
                }
            } catch (ExportedDataNotFoundException e) {
                log.warn("Not found exported data for site {} ", site.getCode());
            }

        }
        log.debug("import sitestat finished in {} mil", DateTime.now().getMillis() - startTime.getMillis());
    }
}
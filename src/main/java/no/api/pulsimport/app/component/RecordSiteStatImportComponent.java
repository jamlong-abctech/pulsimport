package no.api.pulsimport.app.component;

import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.dao.RecordSiteStatDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.enumeration.SiteDeviceEnum;
import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import no.api.pulsimport.app.mapper.RecordSiteStatMapper;
import no.api.pulsimport.app.mapper.SiteMapper;
import no.api.pulsimport.app.model.RecordSiteStatModel;
import no.api.pulsimport.app.model.SiteModel;
import no.api.pulsimport.app.parser.ResultSetXmlParser;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class RecordSiteStatImportComponent{

    private static final Logger log = LoggerFactory.getLogger(RecordSiteStatImportComponent.class);

    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private RecordSiteStatMapper recordSiteStatMapper;

    @Autowired
    private ResultSetXmlParser parser;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private RecordSiteStatDao recordSiteStatDao;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void importRecordSiteStat(String exportFileLocation) throws IOException {
        log.debug("Import siteStat started");
        DateTime startTime = DateTime.now();

        try {
            String siteExportName = exportFileLocation + "site" + ".xml";
            String recordSiteStatExportName = exportFileLocation + "records" + ".xml";
            StatResultSet siteResultSet = parser.parseSiteStat(siteExportName);
            Map<String, String> siteMap = siteMapper.map(siteResultSet);

            for(String siteCode: siteMap.values()){
                SiteModel siteModel = siteDao.findByCode(siteCode);
                StatResultSet recordSiteStatResultSet = parser.parseSiteStat(recordSiteStatExportName);
                List<RecordSiteStatModel> recordSiteStatModels = recordSiteStatMapper.map(recordSiteStatResultSet, siteModel);
                for(RecordSiteStatModel record: recordSiteStatModels){
                    if(record.getUniqueVisitor() != null && record.getUniqueVisitorDate() != null ||
                            record.getPageView() != null && record.getPageViewDate() != null ||
                            record.getVisit() != null && record.getVisitDate() != null){
                        recordSiteStatDao.batchInsert(recordSiteStatModels);
                    }
                }
            }

        } catch (ExportedDataNotFoundException e) {
            log.warn("Not found exported site data");
        }
    }



}

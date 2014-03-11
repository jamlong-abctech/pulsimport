package no.api.pulsimport.app.component;

import no.api.pulsimport.app.bean.RecordBean;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.dao.RecordSiteStatDao;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.dao.SiteStatDao;
import no.api.pulsimport.app.mapper.RecordMapper;
import no.api.pulsimport.app.mapper.SiteMapper;
import no.api.pulsimport.app.parser.ResultSetXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 */

@Component
public class ImportRecordComponent {

    Logger log = LoggerFactory.getLogger(ImportRecordComponent.class);

    @Autowired
    private SiteStatDao siteStatDao;

    @Autowired
    private RecordSiteStatDao recordSiteStatDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private ResultSetXmlParser parser;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private SiteMapper siteMapper;

    public void importRecord(String exportFileLocation) throws IOException {
        String exportedRecordFileName = exportFileLocation + "records.xml";
        String siteFileName = exportFileLocation + "site.xml";
        StatResultSet resultSetRecord = parser.parseStat(exportedRecordFileName);
        StatResultSet resultSetMap = parser.parseStat(siteFileName);
        Map<String , String> siteMap = siteMapper.map(resultSetMap);
        Map<String, Map<String ,RecordBean>> recordMap = recordMapper.map(resultSetRecord, siteMap);


    }
}

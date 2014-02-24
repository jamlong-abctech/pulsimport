package no.api.pulsimport.app.component;

import no.api.pulsimport.app.bean.RecordBean;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.mapper.RecordMapper;
import no.api.pulsimport.app.mapper.SiteMapper;
import no.api.pulsimport.app.parser.ResultSetXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *
 */

@Component
public class ImportRecordComponent {

    private static final Logger log = LoggerFactory.getLogger(ImportRecordComponent.class);

    private final String exportedRecordFileName = "records.xml";

    @Autowired
    private ResultSetXmlParser parser;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private SiteMapper siteMapper;

    public void importRecords(String fileLocation) throws Exception {

        StatResultSet siteResult = parser.parseStat(fileLocation + "site.xml");
        StatResultSet recordStatResultSet = parser.parseStat(fileLocation + exportedRecordFileName);
        Map<String , String> siteMap =  siteMapper.map(siteResult);
        Map<String, List<RecordBean>> recordMap = recordMapper.map(recordStatResultSet, siteMap);

        List<RecordBean> ba = recordMap.get("ba");
        List<RecordBean> mba = recordMap.get("m-ba");

        log.debug("record map : " + recordMap.values().size());
    }
}

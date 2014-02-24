package no.api.pulsimport.app;

import no.api.pulsimport.app.component.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(SiteStatImportComponent.class);

    /**
     *
     * @param args args[0] : A location of exported file.
     */
    public static void main(String[] args) {

        DateTime startTime = DateTime.now();
        log.info("Starting main class for importing puls data");
        String defaultExportedPath = "/opt/puls/exported/";
        String exportedFileLocation = "";
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");
        SiteStatImportComponent siteStatImportComponent = (SiteStatImportComponent) context.getBean("siteStatImportComponent");
        ArticleImportComponent articleComponent = (ArticleImportComponent) context.getBean("articleImportComponent");
        CalculateRecordSiteStatComponent calculateRecordSiteStatComponent = (CalculateRecordSiteStatComponent) context.getBean("calculateRecordSiteStatComponent");
        CalculateRecordArticleStatAllTimeComponent articleRecordArticleAllTimeComponent = (CalculateRecordArticleStatAllTimeComponent) context.getBean("calculateRecordArticleStatAllTimeComponent");
        CalculateRecordArticleStatDayComponent calculateRecordArticleStatDayComponent = (CalculateRecordArticleStatDayComponent) context.getBean("calculateRecordArticleStatDayComponent");
        ImportRecordComponent importRecordComponent = (ImportRecordComponent) context.getBean("importRecordComponent");
        if (args == null || args.length == 0) {
            exportedFileLocation = defaultExportedPath;
        } else {
            exportedFileLocation = args[0];
            if (!exportedFileLocation.endsWith(File.separator)) {
                exportedFileLocation = exportedFileLocation + File.separator;
            }
        }
        try {
            //importRecordComponent.importRecords(exportedFileLocation);
            //siteStatImportComponent.importSiteStat(exportedFileLocation);
            //articleComponent.importArticleStat(exportedFileLocation);
            //calculateRecordSiteStatComponent.calculateSiteStatRecord();
            calculateRecordArticleStatDayComponent.calculateArticleStatDauRecord();
            //articleRecordArticleAllTimeComponent.calculateRecordForArticleStatAllTime();
        } catch (Exception e) {
            log.error("Importing error ", e);
        }
        log.info("Import ALL data finished in {} mil", DateTime.now().getMillis() - startTime.getMillis());
    }
}

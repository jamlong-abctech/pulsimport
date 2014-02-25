package no.api.pulsimport.app;

import no.api.pulsimport.app.component.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

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
        if(args == null || args.length != 5) {
            System.err.println("Need 4 parameter for running import (exported file location, db host, db name, db user, db password)");
            System.exit(1);
        }

        String exportedFileLocation = args[0];
        Properties props = System.getProperties();
        String host = args[1];
        String daName = args[2];
        props.setProperty("db.user", args[3]);
        props.setProperty("db.password", args[4]);
        String jdbcUrl = "jdbc:mysql://"+host+"/"+daName+"?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true";
        props.setProperty("db.jdbcUrl", jdbcUrl);

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");
        SiteStatImportComponent siteStatImportComponent = (SiteStatImportComponent) context.getBean("siteStatImportComponent");
        ArticleImportComponent articleComponent = (ArticleImportComponent) context.getBean("articleImportComponent");
        CalculateRecordSiteStatComponent calculateRecordSiteStatComponent = (CalculateRecordSiteStatComponent) context.getBean("calculateRecordSiteStatComponent");
        CalculateRecordArticleStatAllTimeComponent articleRecordArticleAllTimeComponent = (CalculateRecordArticleStatAllTimeComponent) context.getBean("calculateRecordArticleStatAllTimeComponent");
        CalculateRecordArticleStatDayComponent calculateRecordArticleStatDayComponent = (CalculateRecordArticleStatDayComponent) context.getBean("calculateRecordArticleStatDayComponent");
        //ImportRecordComponent importRecordComponent = (ImportRecordComponent) context.getBean("importRecordComponent");

        try {
            siteStatImportComponent.importSiteStat(exportedFileLocation);
            articleComponent.importArticleStat(exportedFileLocation);
            calculateRecordSiteStatComponent.calculateSiteStatRecord();
            calculateRecordArticleStatDayComponent.calculateArticleStatDauRecord();
            articleRecordArticleAllTimeComponent.calculateRecordForArticleStatAllTime();
        } catch (Exception e) {
            log.error("Importing error ", e);
        }
        log.info("Import ALL data finished in {} mil", DateTime.now().getMillis() - startTime.getMillis());
    }
}

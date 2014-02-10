package no.api.pulsimport.app;

import no.api.pulsimport.app.component.SiteStatImportComponent;
import org.apache.commons.lang.StringUtils;
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
    public static void main(String [] args) {

        DateTime startTime = DateTime.now();
        log.debug("Starting main class for importing puls data");
        String defaultExportedPath = "/opt/puls/exported/";
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");
        SiteStatImportComponent component = (SiteStatImportComponent) context.getBean("siteStatImportComponent");
        String exportedFileLocation = args[0];
        if(StringUtils.isBlank(exportedFileLocation)) {
            exportedFileLocation = defaultExportedPath;
        } else {
            if(!exportedFileLocation.endsWith(File.separator)) {
                exportedFileLocation = exportedFileLocation + File.separator;
            }
        }
        try {
            component.importSiteStat(exportedFileLocation);
            //TODO : Import article
        } catch (IOException e) {
            log.error("Importing error ", e);
        }
        log.debug("Import ALL data finished in {} mil", DateTime.now().getMillis() - startTime.getMillis());
    }
}

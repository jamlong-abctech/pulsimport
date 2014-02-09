package no.api.pulsimport.app;

import no.api.pulsimport.app.component.SiteStatImportComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main(String [] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");
        SiteStatImportComponent component = (SiteStatImportComponent) context.getBean("siteStatImportComponent");
        try {
            component.importSiteStat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package no.api.pulsimport.app;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import no.api.pulsimport.app.bean.ArticleImportBean;
import no.api.pulsimport.app.parser.StatArticleXmlParserComponent;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.exception.ComscoreXMLParseException;
import no.api.pulsimport.app.model.SiteModel;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.apache.commons.io.IOUtils;

public class PulsImporter {
    public static void main(String[] args) {
        System.out.println("Main code here");

        SiteDao dao=new SiteDao();

        List<SiteModel> siteModelList = dao.findAllSite();

        for (SiteModel siteModel : siteModelList) {

            System.out.println(siteModel.getName());
        }


        String mockFileClassPath = "stats_article_an.xml";
        InputStream is = new PulsImporter().getClass().getClassLoader().getResourceAsStream(mockFileClassPath);

        if (is == null) {
            String errorMsg = "Unknown report type, no mock file presented, " + mockFileClassPath;
            return;
        }

        try {
            String siteCode="an";
            String responseStr = IOUtils.toString(is, "UTF-8");
            StatArticleXmlParserComponent xmlParserComponent=new StatArticleXmlParserComponent();
            ArticleImportBean articleImportBean=xmlParserComponent.retrieveArticleStatFromXml(responseStr,siteCode);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComscoreXMLParseException e) {
            e.printStackTrace();
        }

    }
}

package no.api.pulsimport.app;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import no.api.pulsimport.app.dao.SiteDao;
import no.api.pulsimport.app.model.SiteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.apache.commons.io.IOUtils;

public class PulsImporter {
    public static void main(String[] args) {
        System.out.println("Main code here");

        SiteDao dao=new SiteDao();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/puls?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true");
        dataSource.setUsername("puls");
        dataSource.setPassword("pingu123");

        dao.setDataSource(dataSource);
        List<SiteModel> siteModelList = dao.findAllSite();

        for (SiteModel siteModel : siteModelList) {

            System.out.println(siteModel.getName());
        }


        String mockFileClassPath = "table1.xml";
        InputStream is = new PulsImporter().getClass().getClassLoader().getResourceAsStream(mockFileClassPath);

        if (is == null) {
            String errorMsg = "Unknown report type, no mock file presented, " + mockFileClassPath;
            return;
        }

        try {
            String responseStr = IOUtils.toString(is, "UTF-8");
            System.out.println(responseStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

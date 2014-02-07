package no.api.pulsimport.app;
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

    }
}

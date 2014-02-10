package no.api.pulsimport.app.parser;

import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */

@Component
public class ResultSetXmlParser {

    public StatResultSet parseSiteStat(String exportedName) throws IOException {

        //InputStream is = this.getClass().getClassLoader().getResourceAsStream(exportedName);
        StatResultSet resultSet = null;
        try (InputStream is = new FileInputStream(exportedName)) {
            JAXBContext jaxbContext = JAXBContext.newInstance(StatResultSet.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            resultSet = (StatResultSet) jaxbUnmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new ExportedDataNotFoundException("export file not found");
        }

        return resultSet;
    }
}

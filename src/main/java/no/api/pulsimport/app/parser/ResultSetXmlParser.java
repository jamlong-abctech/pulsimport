package no.api.pulsimport.app.parser;

import no.api.pulsimport.app.bean.ArticleStatResultSet;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;

/**
 *
 */

@Component
public class ResultSetXmlParser {

    public StatResultSet parseSiteStat(String exportedName) throws IOException {

        StatResultSet resultSet = null;
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = null;
        try {
            xsr = xif.createXMLStreamReader(new FileReader(exportedName));
            //xsr.nextTag(); // Advance to statements element
            JAXBContext jaxbContext = JAXBContext.newInstance(StatResultSet.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            resultSet = (StatResultSet) jaxbUnmarshaller.unmarshal(xsr);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new ExportedDataNotFoundException("export file not found");
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            try {
                if(xsr != null) {
                    xsr.close();
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        return resultSet;
    }
}

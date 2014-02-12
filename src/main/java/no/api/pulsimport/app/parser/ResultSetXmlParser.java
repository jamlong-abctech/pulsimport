package no.api.pulsimport.app.parser;

import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.bean.StatRow;
import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */

@Component
public class ResultSetXmlParser {

    private static final Logger log = LoggerFactory.getLogger(ResultSetXmlParser.class);

    public StatResultSet parseStat(String exportedName) throws IOException {

        log.info("Starting parse xml : {}", exportedName);
        StatResultSet resultSet = new StatResultSet();
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = null;
        try {
            xsr = xif.createXMLStreamReader(new FileReader(exportedName));
            xsr.nextTag(); // Advance to statements element
            JAXBContext jaxbContext = JAXBContext.newInstance(StatRow.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            List<StatRow> rowList = new ArrayList<>();
            while(xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
                StatRow aRow = (StatRow) jaxbUnmarshaller.unmarshal(xsr);
                rowList.add(aRow);
            }
            log.info("Parsing is done got {} rows", rowList.size());
            resultSet.setRows(rowList);
        } catch (XMLStreamException e) {
            log.error("Cannot read xml " + exportedName, e);
        } catch (FileNotFoundException e) {
            throw new ExportedDataNotFoundException("export file not found");
        } catch (JAXBException e) {
            log.error("Cannot parse xml "+exportedName, e);
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

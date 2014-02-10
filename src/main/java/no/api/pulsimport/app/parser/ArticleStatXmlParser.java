package no.api.pulsimport.app.parser;


import no.api.pulsimport.app.DateTimeFormatUtil;
import no.api.pulsimport.app.bean.ArticleStatResultSet;
import no.api.pulsimport.app.exception.ComscoreXMLParseException;

import no.api.pulsimport.app.exception.ExportedDataNotFoundException;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
public class ArticleStatXmlParser {
    public ArticleStatResultSet parseArticleStat(String exportedName) throws IOException {

        ArticleStatResultSet resultSet = null;
        try (InputStream is = new FileInputStream(exportedName)) {
            JAXBContext jaxbContext = JAXBContext.newInstance(ArticleStatResultSet.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            resultSet = (ArticleStatResultSet) jaxbUnmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new ExportedDataNotFoundException("export file not found");
        }

        return resultSet;
    }
    /*
    private static final String R_TAG = "row";
    private static final String C_TAG = "field";

    public ArticleImportBean parseSiteStat(String xmlContent,String siteCode) throws ComscoreXMLParseException {




        boolean isInitial = true;
        boolean isNewArticle = false;
        ArticleImportBean articleImportBean = new ArticleImportBean();
        List<ArticleBean> articleBeanList = new ArrayList<ArticleBean>();
        ArticleBean articleBean = null;
        Stack<String> cStack = new Stack<String>();

        try {
            InputStream is = new PulsImporter().getClass().getClassLoader().getResourceAsStream(mockFileClassPath);

            if (is == null) {
                String errorMsg = "Unknown report type, no mock file presented, " + mockFileClassPath;
                return;
            }

            try {
                String siteCode="an";
                String responseStr = IOUtils.toString(is, "UTF-8");



            byte[] byteArray = xmlContent.getBytes("UTF-8");
            ByteArrayInputStream xmlStream = new ByteArrayInputStream(byteArray);

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader inputEventReader = inputFactory.createXMLEventReader(xmlStream);

            while (inputEventReader.hasNext()) {

                XMLEvent event = inputEventReader.nextEvent();

                if (event.isStartElement()) {

                    StartElement startElement = event.asStartElement();
                    String startElementName = startElement.getName().getLocalPart();
                    if (!isNewArticle) {
                        if (startElementName.equals(C_TAG)) {
                            cStack.push(inputEventReader.getElementText());
                        }

                    }
                }
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    String endElementName = endElement.asEndElement().getName().getLocalPart();

                    if (endElementName.equals(R_TAG)) {
                        List<String> cList = new ArrayList<String>(cStack);
                        if (isInitial) {
                            articleBean = new ArticleBean();
                            articleBean.setId(Long.parseLong(cList.get(0)));
                            articleBean.setUniqueVisitor(Integer.parseInt(cList.get(1)));
                            articleBean.setPageView(Integer.parseInt(cList.get(2)));
                            articleBean.setSession(Integer.parseInt(cList.get(3)));

                            DateTime date;
                            date = DateTimeFormatUtil.parseDateTime(cList.get(4));

                            articleBean.setDate(date);
                            articleBean.setArticleId(cList.get(5));
                            articleBean.setArticleTitle(cList.get(6));
                            articleBean.setArticleUrl(cList.get(7));
                            articleBean.setSiteCode(siteCode);
                            if(articleBean!=null){
                                articleBeanList.add(articleBean);
                            }
                        }

                        // Clear stack to get new one
                        cStack.clear();
                    }
                }
            }

        } catch (XMLStreamException e) {
            throw new ComscoreXMLParseException("Problem while parsing comscore XML", e);
        } catch (UnsupportedEncodingException e) {
            throw new ComscoreXMLParseException("Problem while parsing comscore XML", e);
        }

        if (articleBeanList.size() == 0) {
            throw new ComscoreXMLParseException("There is no article stat found in xml, xml content = " +
                    xmlContent);
        }

        articleImportBean.setArticleBeans(articleBeanList);
        return articleImportBean;

    }
    */


}

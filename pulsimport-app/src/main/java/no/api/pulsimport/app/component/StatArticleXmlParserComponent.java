package no.api.pulsimport.app.component;


import no.api.pulsimport.app.StringCleaningUtil;
import no.api.pulsimport.app.bean.ArticleBean;
import no.api.pulsimport.app.bean.ArticleImportBean;
import no.api.pulsimport.app.bean.StatisticBean;
import no.api.pulsimport.app.bean.StatisticByUrlBean;
import no.api.pulsimport.app.exception.ComscoreXMLParseException;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component


public class StatArticleXmlParserComponent {
    private static final String REST = "Rest";
    private static final String TOTAL = "Total";
    private static final String R_TAG = "r";
    private static final String C_TAG = "c";

    //TODO Tone.4/12/13, revise the business logic, improve unit test
    public ArticleImportBean retrieveArticleStatFromXml(String xmlContent) throws ComscoreXMLParseException {

        boolean isInitial = true;
        boolean isNewArticle = false;
        ArticleImportBean articleImportBean = new ArticleImportBean();
        List<ArticleBean> articleBeanList = new ArrayList<ArticleBean>();
        ArticleBean articleBean = null;
        List<StatisticByUrlBean> statisticByUrlBeanList = new ArrayList<StatisticByUrlBean>();
        StatisticBean statisticBean = new StatisticBean();
        Stack<String> cStack = new Stack<String>();

        try {

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
                            // Keep CList data to create articleBean
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
                            articleBean.setSiteCode(cList.get(0));
                            articleBean.setArticleId(cList.get(1));
                        }

                        if (!cList.get(0).equals(TOTAL)) {
                            if (cList.get(1).equals(articleBean.getArticleId())) {
                                isInitial = false;
                                isNewArticle = false;
                                createStatisticByUrl(articleBeanList, articleBean, statisticByUrlBeanList, statisticBean, cList);
                            } else if (!cList.get(1).equals(TOTAL) && (!cList.get(1).equals(REST))) {
                                articleBean = new ArticleBean();
                                articleBean.setArticleId(cList.get(1));
                                articleBean.setSiteCode(cList.get(0));
                                statisticByUrlBeanList = new ArrayList<StatisticByUrlBean>();
                                statisticBean = new StatisticBean();
                                isNewArticle = true;
                                continue;
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

    private void createStatisticByUrl(List<ArticleBean> articleBeanList, ArticleBean articleBean, List<StatisticByUrlBean> statisticByUrlBeanList, StatisticBean statisticBean, List<String> cList) {
        if (!cList.get(3).equals(TOTAL)) {
            StatisticByUrlBean statisticByUrlBean = new StatisticByUrlBean(
                    StringEscapeUtils.unescapeHtml(cList.get(2)));

            statisticByUrlBean.setTitle(StringCleaningUtil.unescapeHtmlAndCapitalizeAndReplaceDash(
                    cList.get(3)));
            statisticByUrlBean.setNumberOfUniqueVisitor(Integer.parseInt(cList.get(4)));
            statisticByUrlBean.setNumberOfSession(Integer.parseInt((cList.get(5))));
            statisticByUrlBean.setNumberOfPageView(Integer.parseInt((cList.get(6))));
            statisticByUrlBean.setNumberOfMobileUniqueVisitor(Integer.parseInt(
                    cList.get(7)));
            statisticByUrlBean.setNumberOfMobileSession(Integer.parseInt((cList.get(8))));
            statisticByUrlBean.setNumberOfMobilePageView(Integer.parseInt((cList.get(9))));
            statisticByUrlBeanList.add(statisticByUrlBean);

        } else if ((cList.get(2).equals(TOTAL) && (cList.get(3).equals(TOTAL)))) {
            statisticBean.setNumberOfUniqueVisitor(Integer.parseInt((cList.get(4))));
            statisticBean.setNumberOfSession(Integer.parseInt((cList.get(5))));
            statisticBean.setNumberOfPageView(Integer.parseInt((cList.get(6))));
            statisticBean.setNumberOfMobileUniqueVisitor(Integer.parseInt((cList.get(7))));
            statisticBean.setNumberOfMobileSession(Integer.parseInt((cList.get(8))));
            statisticBean.setNumberOfMobilePageView(Integer.parseInt((cList.get(9))));
            articleBean.setTotalStatisticOfArticle(statisticBean);
            articleBean.setStatisticByUrls(statisticByUrlBeanList);
            articleBeanList.add(articleBean);
        }
    }

}

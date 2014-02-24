package no.api.pulsimport.app.mapper;

import no.api.pulsimport.app.bean.RecordBean;
import no.api.pulsimport.app.bean.StatResultSet;
import no.api.pulsimport.app.bean.StatRow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */

@Component
public class RecordMapper {

    public Map<String, List<RecordBean>> map(StatResultSet statResultSet, Map<String, String> siteMap) {
        Map<String , List<RecordBean>> result = new HashMap<>();
        List<StatRow> rows = statResultSet.getRows();
        for(StatRow eachRow : rows) {
            String siteCode = siteMap.get(eachRow.getField().get(7));
            String type = eachRow.getField().get(1);
            int number = Integer.parseInt(eachRow.getField().get(2));
            String date = eachRow.getField().get(3);
            String url = eachRow.getField().get(4);
            String articleTitle = eachRow.getField().get(5);
            String articleId = eachRow.getField().get(6);
            RecordBean recordBean = new RecordBean();
            recordBean.setType(type);
            recordBean.setNumber(number);
            recordBean.setDate(date);
            recordBean.setUrl(url);
            recordBean.setArticleTitle(articleTitle);
            recordBean.setArticalId(articleId);
            recordBean.setSiteCode(siteCode);

            if(result.get(siteCode) == null) {
                List<RecordBean> recordBeans = new ArrayList<>();
                recordBeans.add(recordBean);
                result.put(siteCode, recordBeans);
            } else {
                result.get(siteCode).add(recordBean);
            }
        }
        return result;
    }
}

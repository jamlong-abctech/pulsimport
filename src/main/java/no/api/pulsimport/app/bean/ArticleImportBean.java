package no.api.pulsimport.app.bean;

import java.util.List;

/**
 * represent article stat data from socscore
 */

public class ArticleImportBean {

    private List<ArticleBean> articleBeans;

    public List<ArticleBean> getArticleBeans() {
        return articleBeans;
    }

    public void setArticleBeans(List<ArticleBean> articleBeans) {
        this.articleBeans = articleBeans;
    }
}

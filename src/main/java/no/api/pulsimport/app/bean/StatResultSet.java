package no.api.pulsimport.app.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by tum on 2/9/2014 AD.
 */

@XmlRootElement(name = "resultset")
public class StatResultSet {
    private List<StatRow> rows;

    public List<StatRow> getRows() {
        return rows;
    }

    @XmlElement(name = "row")
    public void setRows(List<StatRow> rows) {
        this.rows = rows;
    }
}

package no.api.pulsimport.app.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by tum on 2/9/2014 AD.
 */

@XmlRootElement(name = "row")
public class SiteStatRow {
    private List<String> field;

    public List<String> getField() {
        return field;
    }

    @XmlElement(name = "field")
    public void setField(List<String> field) {
        this.field = field;
    }
}

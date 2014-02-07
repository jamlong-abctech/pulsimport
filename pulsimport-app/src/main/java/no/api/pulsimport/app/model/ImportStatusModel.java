package no.api.pulsimport.app.model;

import no.api.pulsimport.app.enumeration.ImportNameEnum;
import org.joda.time.DateTime;

/**
 *
 */

public class ImportStatusModel {
    private Long id;
    private ImportNameEnum name;
    private DateTime updatedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImportNameEnum getName() {
        return name;
    }

    public void setName(ImportNameEnum name) {
        this.name = name;
    }

    public DateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(DateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}

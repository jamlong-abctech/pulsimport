package no.api.pulsimport.app.model;

import no.api.pulsimport.app.enumeration.SiteDeviceEnum;

public class SiteModel {
    private Long id;
    private String code;
    private String name;
    private SiteDeviceEnum device;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SiteDeviceEnum getDevice() {
        return device;
    }

    public void setDevice(SiteDeviceEnum device) {
        this.device = device;
    }


}

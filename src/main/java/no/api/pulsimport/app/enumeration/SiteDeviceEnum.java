package no.api.pulsimport.app.enumeration;

public enum SiteDeviceEnum {
    DESKTOP("www"),
    MOBILE("mobile"),
    COMBINE("combine"),
    DESKTOP_PLUS("www+"),
    MOBILE_PLUS("mobile+"),
    COMBINE_PLUS("combine+")
    ;

    private String textValue;
    private SiteDeviceEnum(String textValue) {
        this.textValue = textValue;
    }

    public String toTextValue() {
        return this.textValue;
    }

    public static SiteDeviceEnum fromText(String text) {
        for (SiteDeviceEnum eachSite : SiteDeviceEnum.values()) {
            if (eachSite.toTextValue().equals(text)) {
                return eachSite;
            }
        }

        return null;
    }

}

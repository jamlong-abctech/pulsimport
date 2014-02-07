package no.api.pulsimport.app.enumeration;

public enum ImportNameEnum {

    SITE(0, "site"),
    ARTICLE(1, "article"),
    PAID_SITE(3, "site+"),
    PAID_ARTICLE(4, "article+");

    private int numberValue = 0;
    private String textValue = null;

    ImportNameEnum(int numberValue, String textValue) {
        this.numberValue = numberValue;
        this.textValue = textValue;
    }

    public String toTextValue() {
        return this.textValue;
    }

    public int toNumberValue() {
        return this.numberValue;
    }

    public static ImportNameEnum fromText(String text) {
        for (ImportNameEnum each : ImportNameEnum.values()) {
            if (each.toTextValue().equals(text)) {
                return each;
            }
        }

        return null;
    }
}

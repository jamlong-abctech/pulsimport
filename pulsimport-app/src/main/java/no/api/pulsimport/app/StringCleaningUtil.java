package no.api.pulsimport.app;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Utility to clean text
 */
public final class StringCleaningUtil {

    private StringCleaningUtil() {
        // intention, Util should NOT able to instantiate
    }

    /**
     * Un-escape HTML code and capitalize the first LETTER and also replace the "-" (dash) with space,
     * mostly used to clean the article title
     */
    public static String unescapeHtmlAndCapitalizeAndReplaceDash(String inputText) {

        if (StringUtils.isEmpty(inputText)) {
            return inputText;
        }

        String cleanText;
        cleanText = StringEscapeUtils.unescapeHtml(inputText);
        cleanText = cleanText.replaceAll("-", " ");
        //FIXME Tone.4/10/13, please add this case in unit test, StringCleaningUtilTest
        cleanText = cleanText.replaceFirst("^\\s", ""); // remove space only at beginning of title
        if (Character.isLetter(cleanText.charAt(0))) {
            cleanText = StringUtils.capitalize(cleanText);
        } else {
            char[] stringArray = cleanText.toCharArray();

            for(int i = 0 ; i < cleanText.length(); i++){
                if(Character.isLetter(cleanText.charAt(i))){
                    stringArray[i] = Character.toUpperCase(stringArray[i]);
                    cleanText = new String(stringArray);
                    break;
                }
            }
        }

        return cleanText;
    }

}

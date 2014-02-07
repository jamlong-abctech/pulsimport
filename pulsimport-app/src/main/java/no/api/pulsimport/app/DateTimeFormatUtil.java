package no.api.pulsimport.app;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public final class DateTimeFormatUtil {

 //   private static final Logger log = LoggerFactory.getLogger(DateTimeFormatUtil.class);

    private DateTimeFormatUtil() {
        // intention, Util should NOT able to instantiate
    }

    /**
     * The date format used in query parameter and other data's related
     */
    public static String formatDataDate(DateTime date) {
        return date.toString(ISODateTimeFormat.date());
    }

    public static DateTime parseDataDate(String dateText) {
        try {
            return DateTime.parse(dateText, ISODateTimeFormat.date());
        } catch (IllegalArgumentException e) {
          //  log.warn("Cannot parse date " + dateText + ", error = " + e.getMessage());
            return null;
        }
    }
}

package no.api.pulsimport.app;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Locale;
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
//    public static DateTime parseDateTime(String dateText){
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//        DateTime dateTime = formatter.parseDateTime(dateText);
//
//        return dateTime;
//    }

    public static DateTime parseDateTime(String dateText) {
        try{
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.forID("Europe/Oslo"));
       // formatter = formatter.withChronology(LenientChronology.getInstance(ISOChronology.getInstance(DateTimeZone.UTC)));
            return formatter.parseDateTime(dateText);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

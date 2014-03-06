package no.api.pulsimport.app;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 *
 */
public class DateTimeFormatUtilTest {

    private Logger log = LoggerFactory.getLogger(DateTimeFormatUtilTest.class);

    @Test
    public void testDayLigthSaving() {
        String dateString = "2012-03-25 00:00:00";
//        DateTime dateTime = DateTimeFormatUtil.parseDateTime(dateString);
//        log.debug("dateTime : "+dateTime);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime startOfDay = dtf.parseDateTime(dateString);

    /* Obtained from new DateTime() in code in practice */
        DateTime actualTimeWhenStoring = startOfDay.plusHours(2);

        int hourOfDay = actualTimeWhenStoring.getHourOfDay();
        int hourOffset = startOfDay.plusHours(hourOfDay).getHourOfDay();

        System.out.println("Hour of day:" + hourOfDay);
        System.out.println("Offset hour:" + hourOffset);

        int timeToSave = hourOfDay;
        if (hourOffset != hourOfDay) {
            timeToSave = (hourOfDay + (hourOfDay - hourOffset));
        }
        System.out.println("Time to save:" + timeToSave);

    /* When obtaining from db: */
        DateTime recalculatedTime = startOfDay.plusHours(timeToSave);

        System.out.println("Hour of time 'read' from db:" + recalculatedTime.getHourOfDay());
    }

    @Test
    public void testDetect() {
        String dateString = "2012-03-25 02:00:00";
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);
        DateTime dateTimeInUtc = dtf.parseDateTime(dateString);
        long milInUtc = dateTimeInUtc.getMillis();
        log.debug("UTC : "+milInUtc);
        log.debug("UTC : "+dateTimeInUtc);

        DateTime dateTime = new DateTime(milInUtc, DateTimeZone.forID("Europe/Oslo"));
        log.debug(dateTime.toDate().toString());
    }

    @Test
    public void test() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.getDefault()).withZone(DateTimeZone.UTC);
        formatter = formatter.withChronology(LenientChronology.getInstance(ISOChronology.getInstance(DateTimeZone.UTC)));
        DateTime dateTime = formatter.parseDateTime("2012-03-25 02:00:00");
        System.out.println(dateTime);
        DateTime dt = new LocalDateTime(dateTime).toDateTime(DateTimeZone.forID("Europe/Oslo"));
        System.out.println(dt);
    }
}

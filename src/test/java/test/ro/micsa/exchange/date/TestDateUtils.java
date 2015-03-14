/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.date;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.utils.*;
import static org.fest.assertions.api.Assertions.*;

/**
 *
 * @author georgian
 */
public class TestDateUtils {
    
    @Test
    public void testIsWeekend() {
        Calendar cal = new GregorianCalendar();
        cal.set(2012, 9, 19);
        Date friday = cal.getTime();
        cal.set(2012, 9, 20);
        Date saturday = cal.getTime();
        cal.set(2012, 9, 21);
        Date sunday = cal.getTime();
        
        assertThat(DateUtils.isWeekend(friday)).isFalse();
        assertThat(DateUtils.isWeekend(saturday)).isTrue();
        assertThat(DateUtils.isWeekend(sunday)).isTrue();
    }
    
    @Test
    public void testGetPreviousBusinessDate() {
        Calendar cal = new GregorianCalendar();
        cal.set(2012, 9, 19);
        Date friday = cal.getTime();
        cal.set(2012, 9, 20);
        Date saturday = cal.getTime();
        cal.set(2012, 9, 21);
        Date sunday = cal.getTime();
        cal.set(2012, 9, 22);
        Date monday = cal.getTime();
        assertThat(friday).isEqualTo(DateUtils.getPreviousBusinessDate(saturday));
        assertThat(friday).isEqualTo(DateUtils.getPreviousBusinessDate(sunday));
        assertThat(friday).isEqualTo(DateUtils.getPreviousBusinessDate(monday));
    }

    @Test
    public void testLast10Days() throws Exception {
        List<String> days = DateUtils.getLastNDays("2012-03-03", 10);
        assertThat(days).hasSize(10);
        days = DateUtils.getLastNDays("2012-03-xx", 10);
        assertThat(days).isEmpty();
    }
}

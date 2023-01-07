package cn.tuyucheng.taketoday.regexp.datepattern.gregorian;

import cn.tuyucheng.taketoday.regexp.datepattern.DateMatcher;
import cn.tuyucheng.taketoday.regexp.datepattern.gregorian.testhelper.GregorianDateTestHelper;
import org.junit.Test;

public class February29thMatcherUnitTest {

    private DateMatcher matcher = new February29thMatcher();

    private GregorianDateTestHelper testHelper = new GregorianDateTestHelper(matcher);

    @Test
    public void whenYearIsLeap_thenYearHasFebruary29th() {
        testHelper.assertFebruary29th();
    }
}

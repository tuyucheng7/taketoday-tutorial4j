package cn.tuyucheng.taketoday.regexp.datepattern.gregorian;

import cn.tuyucheng.taketoday.regexp.datepattern.DateMatcher;
import cn.tuyucheng.taketoday.regexp.datepattern.gregorian.testhelper.GregorianDateTestHelper;
import org.junit.Test;

public class FebruaryGeneralMatcherUnitTest {

    private DateMatcher matcher = new FebruaryGeneralMatcher();

    private GregorianDateTestHelper testHelper = new GregorianDateTestHelper(matcher);

    @Test
    public void whenMonthIsFebruary_thenMonthContainsUpTo28Days() {
        testHelper.assertFebruaryGeneralDates();
    }
}
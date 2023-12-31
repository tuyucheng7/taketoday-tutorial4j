package cn.tuyucheng.taketoday.caseinsensitiveenum;

import cn.tuyucheng.taketoday.caseinsensitiveenum.providers.WeekDayHolderArgumentsProvider;
import cn.tuyucheng.taketoday.caseinsensitiveenum.week.WeekDays;
import cn.tuyucheng.taketoday.caseinsensitiveenum.week.WeekDaysHolder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
      "monday=Mon-Day!",
      "tuesday=TuesDAY#",
      "wednesday=Wednes@day",
      "thursday=THURSday^",
      "friday=Fri:Day_%",
      "saturday=Satur_DAY*",
      "sunday=Sun+Day",
}, classes = WeekDaysHolder.class)
class LenientStringToEnumConverterUnitTest {

   @Autowired
   private WeekDaysHolder holder;

   @ParameterizedTest
   @ArgumentsSource(WeekDayHolderArgumentsProvider.class)
   void givenPropertiesWhenInjectEnumThenValueIsPresent(
         Function<WeekDaysHolder, WeekDays> methodReference, WeekDays expected) {
      WeekDays actual = methodReference.apply(holder);
      assertThat(actual).isEqualTo(expected);
   }
}
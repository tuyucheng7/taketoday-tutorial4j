package cn.tuyucheng.taketoday.caseinsensitiveenum;

import cn.tuyucheng.taketoday.caseinsensitiveenum.nonconventionalweek.NonConventionalWeekDays;
import cn.tuyucheng.taketoday.caseinsensitiveenum.nonconventionalweek.NonConventionalWeekDaysHolder;
import cn.tuyucheng.taketoday.caseinsensitiveenum.providers.NonConventionalWeekDayHolderArgumentsProvider;
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
}, classes = NonConventionalWeekDaysHolder.class)
class NonConventionalStringToEnumLenientConverterUnitTest {

   @Autowired
   private NonConventionalWeekDaysHolder holder;

   @ParameterizedTest
   @ArgumentsSource(NonConventionalWeekDayHolderArgumentsProvider.class)
   void givenPropertiesWhenInjectEnumThenValueIsPresent(
         Function<NonConventionalWeekDaysHolder, NonConventionalWeekDays> methodReference, NonConventionalWeekDays expected) {
      NonConventionalWeekDays actual = methodReference.apply(holder);
      assertThat(actual).isEqualTo(expected);
   }
}
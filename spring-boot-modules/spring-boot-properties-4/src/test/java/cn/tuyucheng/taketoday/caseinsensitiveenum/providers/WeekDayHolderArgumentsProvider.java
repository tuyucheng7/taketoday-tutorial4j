package cn.tuyucheng.taketoday.caseinsensitiveenum.providers;

import cn.tuyucheng.taketoday.caseinsensitiveenum.week.WeekDays;
import cn.tuyucheng.taketoday.caseinsensitiveenum.week.WeekDaysHolder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.function.Function;
import java.util.stream.Stream;

import static cn.tuyucheng.taketoday.caseinsensitiveenum.week.WeekDays.*;

public class WeekDayHolderArgumentsProvider implements ArgumentsProvider {

   @Override
   public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
      return Stream.of(
            Arguments.of(((Function<WeekDaysHolder, WeekDays>) WeekDaysHolder::getMonday), MONDAY),
            Arguments.of(((Function<WeekDaysHolder, WeekDays>) WeekDaysHolder::getTuesday), TUESDAY),
            Arguments.of(((Function<WeekDaysHolder, WeekDays>) WeekDaysHolder::getWednesday), WEDNESDAY),
            Arguments.of(((Function<WeekDaysHolder, WeekDays>) WeekDaysHolder::getThursday), THURSDAY),
            Arguments.of(((Function<WeekDaysHolder, WeekDays>) WeekDaysHolder::getFriday), FRIDAY),
            Arguments.of(((Function<WeekDaysHolder, WeekDays>) WeekDaysHolder::getSaturday), SATURDAY),
            Arguments.of(((Function<WeekDaysHolder, WeekDays>) WeekDaysHolder::getSunday), SUNDAY)
      );
   }
}
package cn.tuyucheng.taketoday.caseinsensitiveenum.providers;

import cn.tuyucheng.taketoday.caseinsensitiveenum.week.SpELWeekDaysHolder;
import cn.tuyucheng.taketoday.caseinsensitiveenum.week.WeekDays;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.function.Function;
import java.util.stream.Stream;

import static cn.tuyucheng.taketoday.caseinsensitiveenum.week.WeekDays.*;

public class SpELWeekDayHolderArgumentsProvider implements ArgumentsProvider {

   @Override
   public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
      return Stream.of(
            Arguments.of(((Function<SpELWeekDaysHolder, WeekDays>) SpELWeekDaysHolder::getMonday), MONDAY),
            Arguments.of(((Function<SpELWeekDaysHolder, WeekDays>) SpELWeekDaysHolder::getTuesday), TUESDAY),
            Arguments.of(((Function<SpELWeekDaysHolder, WeekDays>) SpELWeekDaysHolder::getWednesday), WEDNESDAY),
            Arguments.of(((Function<SpELWeekDaysHolder, WeekDays>) SpELWeekDaysHolder::getThursday), THURSDAY),
            Arguments.of(((Function<SpELWeekDaysHolder, WeekDays>) SpELWeekDaysHolder::getFriday), FRIDAY),
            Arguments.of(((Function<SpELWeekDaysHolder, WeekDays>) SpELWeekDaysHolder::getSaturday), SATURDAY),
            Arguments.of(((Function<SpELWeekDaysHolder, WeekDays>) SpELWeekDaysHolder::getSunday), SUNDAY)
      );
   }
}
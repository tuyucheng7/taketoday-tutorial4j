package cn.tuyucheng.taketoday.caseinsensitiveenum.providers;

import cn.tuyucheng.taketoday.caseinsensitiveenum.nonconventionalweek.NonConventionalWeekDays;
import cn.tuyucheng.taketoday.caseinsensitiveenum.nonconventionalweek.NonConventionalWeekDaysHolder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.function.Function;
import java.util.stream.Stream;

import static cn.tuyucheng.taketoday.caseinsensitiveenum.nonconventionalweek.NonConventionalWeekDays.*;

public class NonConventionalWeekDayHolderArgumentsProvider implements ArgumentsProvider {

   @Override
   public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
      return Stream.of(
            Arguments.of(((Function<NonConventionalWeekDaysHolder, NonConventionalWeekDays>) NonConventionalWeekDaysHolder::getMonday), Mon$Day),
            Arguments.of(((Function<NonConventionalWeekDaysHolder, NonConventionalWeekDays>) NonConventionalWeekDaysHolder::getTuesday), Tues$DAY_),
            Arguments.of(((Function<NonConventionalWeekDaysHolder, NonConventionalWeekDays>) NonConventionalWeekDaysHolder::getWednesday), Wednes$day),
            Arguments.of(((Function<NonConventionalWeekDaysHolder, NonConventionalWeekDays>) NonConventionalWeekDaysHolder::getThursday), THURS$day_),
            Arguments.of(((Function<NonConventionalWeekDaysHolder, NonConventionalWeekDays>) NonConventionalWeekDaysHolder::getFriday), Fri$Day$_$),
            Arguments.of(((Function<NonConventionalWeekDaysHolder, NonConventionalWeekDays>) NonConventionalWeekDaysHolder::getSaturday), Satur$DAY_),
            Arguments.of(((Function<NonConventionalWeekDaysHolder, NonConventionalWeekDays>) NonConventionalWeekDaysHolder::getSunday), Sun$Day)
      );
   }
}
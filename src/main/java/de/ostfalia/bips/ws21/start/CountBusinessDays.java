package de.ostfalia.bips.ws21.start;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CountBusinessDays {

    public static List<LocalDate> countBusinessDaysBetween(final LocalDate startDate,
                                                           final LocalDate endDate,
                                                           final Optional<List<LocalDate>> holidays)
    {
        // Validate method arguments
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween (" + startDate
                    + "," + endDate + "," + holidays + ")");
        }

        // Predicate 1: Is a given date is a holiday
        Predicate<LocalDate> isHoliday = date -> holidays.isPresent() && holidays.get().contains(date);

        // Predicate 2: Is a given date is a weekday
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        // Get all days between two dates
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;


        // Iterate over stream of all dates and check each day against any weekday or
        // holiday
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isHoliday.or(isWeekend).negate())
                .collect(Collectors.toList());
    }
}
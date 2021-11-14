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

// Klasse, die eine Liste mit Daten der Werktage zurückgibt, als Parameter werden das Start-Datum und Enddatum des
// Urlaubs übergeben, sowie eine Optional List, die die Feiertage enthält
public class CountBusinessDays {

    public static List<LocalDate> countBusinessDaysBetween(final LocalDate startDate,
                                                           final LocalDate endDate,
                                                           final Optional<List<LocalDate>> holidays)
    {
        // Überprüfung, ob die Daten Werte enthalten
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween (" + startDate
                    + "," + endDate + "," + holidays + ")");
        }

        // Predicate 1: ist das gegebene Datum ein Feiertag
        Predicate<LocalDate> isHoliday = date -> holidays.isPresent() && holidays.get().contains(date);

        // Predicate 2: ist das gegebene Datum ein Wochenende
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        // alle Tage zwischen den beiden Daten erhalten
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;


        // Iteration über den Stream aller Daten und check ob Wochenende oder Feiertag
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isHoliday.or(isWeekend).negate())
                .collect(Collectors.toList());
    }
}
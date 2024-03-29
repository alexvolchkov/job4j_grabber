package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.util.Map.entry;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final Map<String, String> MONTHS = Map.ofEntries(
            entry("янв", "1"),
            entry("фев", "2"),
            entry("мар", "3"),
            entry("апр", "4"),
            entry("май", "5"),
            entry("июн", "6"),
            entry("июл", "7"),
            entry("авг", "8"),
            entry("сен", "9"),
            entry("окт", "10"),
            entry("ноя", "11"),
            entry("дек", "12")
    );
    private static final String TODAY = "сегодня";
    private static final String YESTERDAY = "вчера";
    private static final int DAY_TO_YESTERDAY = 1;
    private static final int ARRAY_LENGTH_DATE = 3;
    private static final int ARRAY_LENGTH_DAY = 1;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("d-M-yy");

    @Override
    public LocalDateTime parse(String parse) {
        if (parse == null) {
            throw new IllegalArgumentException("Не передана строка для парсинга даты");
        }
        String[] arrString = parseString(parse, ",");
        validation(arrString, 2);
        return LocalDateTime.of(
                parseDay(arrString[0]),
                parseTime(arrString[1]));
    }

    private void validation(String[] arrString, int length) {
        if (arrString.length != length) {
            throw new IllegalArgumentException("Не верный формат строки для парсинга даты.");
        }
    }

    private LocalTime parseTime(String parse) {
        String[] arrString = parseString(parse, ":");
        validation(arrString, 2);
        return LocalTime.of(Integer.parseInt(arrString[0]), Integer.parseInt(arrString[1]));
    }

    private LocalDate parseDay(String parse) {
        LocalDate rsl;
        String[] arrString = parseString(parse, "\s");
        if (arrString.length == ARRAY_LENGTH_DAY && YESTERDAY.equals(arrString[0])) {
            rsl = LocalDate.now().minusDays(DAY_TO_YESTERDAY);
        } else if (arrString.length == ARRAY_LENGTH_DAY && TODAY.equals(arrString[0])) {
            rsl = LocalDate.now();
        } else {
            validation(arrString, ARRAY_LENGTH_DATE);
            rsl = LocalDate.parse(
                    String.format("%s-%s-%s", arrString[0], MONTHS.get(arrString[1]), arrString[2]),
                    DTF);
        }
        return rsl;
    }

    private static String[] parseString(String parse, String regex) {
        String[] arrayString = parse.split(regex);
        for (int i = 0; i < arrayString.length; i++) {
            arrayString[i] = arrayString[i].trim();
        }
        return arrayString;
    }
}

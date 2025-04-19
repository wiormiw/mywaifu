package com.silvermaiden.mywaifu.common.utilities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.silvermaiden.mywaifu.common.constants.UtilitiesConstant.DEFAULT_ZONE_ID;

    public class DateUtil {
        public static final ZoneId DEFAULT_ZONE = ZoneId.of(DEFAULT_ZONE_ID);

        // Default LocalDateTime format
        public static DateTimeFormatter dateTimeFormatter() {
            return DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        }

        // ISO format
        public static DateTimeFormatter isoFormatter() {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }

        // Date to LocaleDateTime
        public static LocalDateTime toLocaleDateTime(Date date) {
            return date.toInstant().atZone(DEFAULT_ZONE).toLocalDateTime();
        }

        // LocaleDateTime to Date
        public static Date toDate(LocalDateTime localDateTime) {
            return Date.from(localDateTime.atZone(DEFAULT_ZONE).toInstant());
        }

        // Instant to LocaleDateTime
        public static LocalDateTime toLocaleDateTimeFromInstant(Instant instant) {
            return instant.atZone(DEFAULT_ZONE).toLocalDateTime();
        }

        // Format LocaleDateTime
        public static String formatLocaleDateTime(LocalDateTime localDateTime) {
            return localDateTime.format(dateTimeFormatter());
        }

        // Get current time in millis
        public static long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

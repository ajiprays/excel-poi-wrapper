package com.aji_prayitno.excel.importer.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.DateUtil;

public final class Converter {

    private static final Map<Class<?>, Function<String, ?>> CONVERTERS = new HashMap<>();

    private Converter() {}
    static {
        CONVERTERS.put(String.class, s -> s);
        Function<String, Character> toChar = s -> s.isEmpty() ? '\0' : s.charAt(0);
        CONVERTERS.put(char.class, toChar);
        CONVERTERS.put(Character.class, toChar);

        Function<String, Integer> toInt = s -> s.contains(".") ? (int) Double.parseDouble(s) : Integer.parseInt(s);
        CONVERTERS.put(int.class, toInt);
        CONVERTERS.put(Integer.class, toInt);

        Function<String, Long> toLong = s -> s.contains(".") ? (long) Double.parseDouble(s) : Long.parseLong(s);
        CONVERTERS.put(long.class, toLong);
        CONVERTERS.put(Long.class, toLong);

        Function<String, Short> toShort = s -> s.contains(".") ? (short) Double.parseDouble(s) : Short.parseShort(s);
        CONVERTERS.put(short.class, toShort);
        CONVERTERS.put(Short.class, toShort);

        Function<String, Byte> toByte = s -> s.contains(".") ? (byte) Double.parseDouble(s) : Byte.parseByte(s);
        CONVERTERS.put(byte.class, toByte);
        CONVERTERS.put(Byte.class, toByte);

        Function<String, Double> toDouble = Double::parseDouble;
        CONVERTERS.put(double.class, toDouble);
        CONVERTERS.put(Double.class, toDouble);

        Function<String, Float> toFloat = Float::parseFloat;
        CONVERTERS.put(float.class, toFloat);
        CONVERTERS.put(Float.class, toFloat);

        Function<String, Boolean> toBoolean = BooleanUtils::toBoolean;
        CONVERTERS.put(boolean.class, toBoolean);
        CONVERTERS.put(Boolean.class, toBoolean);

        CONVERTERS.put(BigDecimal.class, BigDecimal::new);
        CONVERTERS.put(BigInteger.class, s -> s.contains(".") ? BigInteger.valueOf((long) Double.parseDouble(s)) : new BigInteger(s));

        CONVERTERS.put(UUID.class, UUID::fromString);

        CONVERTERS.put(LocalDate.class, s -> parseToJavaDate(s).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        CONVERTERS.put(LocalDateTime.class, s -> parseToJavaDate(s).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        CONVERTERS.put(LocalTime.class, LocalTime::parse);
        CONVERTERS.put(ZonedDateTime.class, s -> parseToJavaDate(s).toInstant().atZone(ZoneId.systemDefault()));
        CONVERTERS.put(Instant.class, s -> parseToJavaDate(s).toInstant());
        CONVERTERS.put(Date.class, Converter::parseToJavaDate);
    }

    private static Date parseToJavaDate(String value) {
        if (value.matches("^\\d+(\\.\\d+)?$")) {
            return DateUtil.getJavaDate(Double.parseDouble(value));
        }
        DateTimeFormatter formatter = value.contains("/") ? DateTimeFormatter.ofPattern("d/M/yyyy") : DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate localDate = LocalDate.parse(value, formatter);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <V> V convert(String value, Class<V> targetType) {
        if (value == null || value.trim().isEmpty()) {
            return (V) getDefaultValue(targetType);
        }

        String trimmed = value.trim();

        Function<String, ?> converter = CONVERTERS.get(targetType);
        if (converter != null) {
            return (V) converter.apply(trimmed);
        }

        if (targetType.isEnum()) {
            return (V) Enum.valueOf((Class<Enum>) targetType, trimmed);
        }
        return (V) trimmed;
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type == int.class || type == long.class || type == short.class || type == byte.class) return 0;
        if (type == double.class || type == float.class) return 0.0;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        return null;
    }
}

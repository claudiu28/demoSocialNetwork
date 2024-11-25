package ubb.scs.map.demosocialnetwork.utils.format;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Format {
    public static String convert(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

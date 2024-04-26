package cn.raindropair.easysocket.serialize;

import cn.raindropair.easysocket.message.MessageBody;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * java 8 时间默认序列化
 *
 * @author L.cm
 * @author lishanbu
 */
public class JavaTimeModule extends SimpleModule {

    public JavaTimeModule() {
        super(PackageVersion.VERSION);

        // ======================= 时间序列化规则 ===============================
        // LocalDate
        // yyyy-MM-dd HH:mm:ss
        this.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // yyyy-MM-dd
        this.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // HH:mm:ss
        this.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        // yyyy-MM-dd HH:mm:ss
        this.addSerializer(Date.class,
                new DateSerializer());


        // ======================= 时间反序列化规则 ==============================
        // yyyy-MM-dd HH:mm:ss
        this.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // yyyy-MM-dd
        this.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // HH:mm:ss
        this.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        this.addDeserializer(Date.class, new DateDeSerializer());

    }
}

package cn.raindropair.easysocket.serialize;
import cn.raindropair.easysocket.constants.BaseCons;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {

    public DateSerializer() {
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String output = StringUtils.EMPTY;

        if (date != null) {
            output = new SimpleDateFormat(BaseCons.DATE_FORMATE).format(date);
        }
        jsonGenerator.writeString(output);
    }

}

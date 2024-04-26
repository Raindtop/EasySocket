package cn.raindropair.easysocket.serialize;
import cn.raindropair.easysocket.constants.BaseCons;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateDeSerializer extends JsonDeserializer {

    public DateDeSerializer() {
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        try{
            return new SimpleDateFormat(BaseCons.DATE_FORMATE).parse(text);
        }catch (Exception e){
            e.printStackTrace();

            return null;
        }
    }
}

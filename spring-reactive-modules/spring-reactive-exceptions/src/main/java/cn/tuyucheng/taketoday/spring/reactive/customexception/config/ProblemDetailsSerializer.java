package cn.tuyucheng.taketoday.spring.reactive.customexception.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.http.ProblemDetail;

import java.io.IOException;

public class ProblemDetailsSerializer extends JsonSerializer<ProblemDetail> {

   @Override
   public void serialize(ProblemDetail value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeStartObject();
      gen.writeObjectField("type", value.getType());
      gen.writeObjectField("title", value.getTitle());
      gen.writeObjectField("status", value.getStatus());
      gen.writeObjectField("detail", value.getDetail());
      gen.writeObjectField("instance", value.getInstance());
      gen.writeObjectField("errors", value.getProperties().get("errors"));
      gen.writeEndObject();
   }
}
package cn.tuyucheng.taketoday.spring.reactive.customexception.config;

import cn.tuyucheng.taketoday.spring.reactive.customexception.model.ErrorDetails;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ErrorDetailsSerializer extends JsonSerializer<ErrorDetails> {
   @Override
   public void serialize(ErrorDetails value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeStartObject();
      gen.writeStringField("code", value.getErrorCode()
            .toString());
      gen.writeStringField("message", value.getErrorMessage());
      gen.writeStringField("reference", value.getReferenceUrl());
      gen.writeEndObject();
   }
}
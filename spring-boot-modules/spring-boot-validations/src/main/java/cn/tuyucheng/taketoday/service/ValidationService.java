package cn.tuyucheng.taketoday.service;

import cn.tuyucheng.taketoday.dto.BooleanObject;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ValidationService {

   public void processBoolean(@Valid BooleanObject booleanObj) {
      // further processing
   }
}
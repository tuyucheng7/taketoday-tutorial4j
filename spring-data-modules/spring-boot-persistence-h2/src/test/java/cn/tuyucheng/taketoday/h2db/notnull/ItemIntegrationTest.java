package cn.tuyucheng.taketoday.h2db.notnull;

import cn.tuyucheng.taketoday.h2db.notnull.daos.ItemRepository;
import cn.tuyucheng.taketoday.h2db.notnull.models.Item;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = NotNullVsNullableApplication.class)
class ItemIntegrationTest {

   @Autowired
   private ItemRepository itemRepository;

   @Test
   void shouldNotAllowToPersistNullItemsPrice() {
      Assertions.assertThatThrownBy(() -> itemRepository.save(new Item())).hasRootCauseInstanceOf(ConstraintViolationException.class)
            .hasStackTraceContaining("propertyPath=price")
            .hasStackTraceContaining("null");
   }
}
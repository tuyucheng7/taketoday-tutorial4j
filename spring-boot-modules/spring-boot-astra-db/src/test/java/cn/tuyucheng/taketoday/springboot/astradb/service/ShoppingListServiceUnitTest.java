package cn.tuyucheng.taketoday.springboot.astradb.service;

import cn.tuyucheng.taketoday.springboot.astradb.entity.ShoppingList;
import cn.tuyucheng.taketoday.springboot.astradb.repository.ShoppingListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShoppingListServiceUnitTest {

   @Mock
   private ShoppingListRepository repository;

   @Mock
   private Slice<ShoppingList> slice;

   @InjectMocks
   private ShoppingListService service;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   @Test
   void whenFindAll_thenInvokesRepository() {
      when(repository.findAll(any(Pageable.class))).thenReturn(slice);

      service.findAll();
      verify(repository).findAll(any(Pageable.class));
   }

   @Test
   void whenFindByTitle_thenInvokesRepository() {
      service.findByTitle("title");

      Mockito.verify(repository)
            .findByTitleAllIgnoreCase("title");
   }
}
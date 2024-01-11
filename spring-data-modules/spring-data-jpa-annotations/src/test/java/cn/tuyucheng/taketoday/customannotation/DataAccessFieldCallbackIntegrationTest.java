package cn.tuyucheng.taketoday.customannotation;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CustomAnnotationConfiguration.class})
class DataAccessFieldCallbackIntegrationTest {

   @Autowired
   private ConfigurableListableBeanFactory configurableListableBeanFactory;

   @Autowired
   private BeanWithGenericDAO beanWithGenericDAO;

   @Rule
   ExpectedException ex = ExpectedException.none();

   @Test
   void whenObjectCreated_thenObjectCreationIsSuccessful() {
      final DataAccessFieldCallback dataAccessFieldCallback = new DataAccessFieldCallback(configurableListableBeanFactory, beanWithGenericDAO);
      assertThat(dataAccessFieldCallback, is(notNullValue()));
   }

   @Test
   void whenMethodGenericTypeIsValidCalled_thenReturnCorrectValue() throws NoSuchFieldException, SecurityException {
      final DataAccessFieldCallback callback = new DataAccessFieldCallback(configurableListableBeanFactory, beanWithGenericDAO);
      final Type fieldType = BeanWithGenericDAO.class.getDeclaredField("personGenericDAO").getGenericType();
      final boolean result = callback.genericTypeIsValid(Person.class, fieldType);
      assertThat(result, is(true));
   }

   @Test
   void whenMethodGetBeanInstanceCalled_thenReturnCorrectInstance() {
      final DataAccessFieldCallback callback = new DataAccessFieldCallback(configurableListableBeanFactory, beanWithGenericDAO);
      final Object result = callback.getBeanInstance("personGenericDAO", GenericDAO.class, Person.class);
      assertThat((result instanceof GenericDAO), is(true));
   }
}

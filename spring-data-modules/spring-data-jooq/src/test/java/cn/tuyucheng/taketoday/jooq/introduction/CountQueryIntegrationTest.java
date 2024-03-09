package cn.tuyucheng.taketoday.jooq.introduction;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.tuyucheng.taketoday.jooq.introduction.db.public_.tables.Author.AUTHOR;

@ContextConfiguration(classes = PersistenceContextIntegrationTest.class)
@Transactional(transactionManager = "transactionManager")
@ExtendWith(SpringExtension.class)
class CountQueryIntegrationTest {

   @Autowired
   private DSLContext dsl;

   @Test
   void givenValidData_whenSimpleSelect_thenSucceed() {
      int count = dsl.select().from(AUTHOR).execute();
      Assertions.assertEquals(3, count);
   }

   @Test
   void givenValidData_whenSelectCount_thenSucceed() {
      int count = dsl.selectCount().from(AUTHOR)
            .where(AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan"))
            .fetchOne(0, int.class);
      Assertions.assertEquals(1, count);
   }

   @Test
   void givenValidData_whenCount_thenSucceed() {
      int count = dsl.select(DSL.count())
            .from(AUTHOR).fetchOne(0, int.class);
      Assertions.assertEquals(3, count);
   }

   @Test
   void givenValidData_whenFetchCount_thenSucceed() {
      int count = dsl.fetchCount(DSL.selectFrom(AUTHOR)
            .where(AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan")));
      Assertions.assertEquals(1, count);
   }

   @Test
   void givenValidData_whenFetchCountWithoutCondition_thenSucceed() {
      int count = dsl.fetchCount(DSL.selectFrom(AUTHOR));
      Assertions.assertEquals(3, count);
   }

   @Test
   void givenValidData_whenFetchCountWithSingleCondition_thenSucceed() {
      int count = dsl.fetchCount(AUTHOR, AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan"));
      Assertions.assertEquals(1, count);
   }

   @Test
   void givenValidData_whenFetchCountWithMultipleConditions_thenSucceed() {
      Condition firstCond = AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan");
      Condition secondCond = AUTHOR.ID.notEqual(1);
      List<Condition> conditions = new ArrayList<>();
      conditions.add(firstCond);
      conditions.add(secondCond);
      int count = dsl.fetchCount(AUTHOR, conditions);
      Assertions.assertEquals(1, count);
   }


   @Test
   void givenValidData_whenFetchCountWithMultipleConditionsUsingAndOperator_thenSucceed() {
      int count = dsl.fetchCount(AUTHOR, AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan").and(AUTHOR.ID.notEqual(1)));
      Assertions.assertEquals(1, count);
   }

   @Test
   void givenValidData_whenFetchCountWithConditionsInVarargs_thenSucceed() {
      Condition firstCond = AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan");
      Condition secondCond = AUTHOR.ID.notEqual(1);
      int count = dsl.fetchCount(AUTHOR, firstCond, secondCond);
      Assertions.assertEquals(1, count);
   }

   @Test
   void givenValidData_whenCountwithGroupBy_thenSucceed() {
      final Result<Record2<String, Integer>> result = dsl.select(AUTHOR.FIRST_NAME, DSL.count())
            .from(AUTHOR).groupBy(AUTHOR.FIRST_NAME).fetch();
      Assertions.assertEquals(3, result.size());
      Assertions.assertEquals(result.get(0).get(0), "Bert");
      Assertions.assertEquals(result.get(0).get(1), 1);
   }
}
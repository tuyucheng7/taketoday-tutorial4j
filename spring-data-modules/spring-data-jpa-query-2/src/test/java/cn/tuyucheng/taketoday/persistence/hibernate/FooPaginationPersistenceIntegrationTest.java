package cn.tuyucheng.taketoday.persistence.hibernate;

import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.persistence.service.IFooService;
import cn.tuyucheng.taketoday.spring.config.PersistenceTestConfig;
import com.google.common.collect.Lists;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceTestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class FooPaginationPersistenceIntegrationTest {

   @Autowired
   private IFooService fooService;

   @Autowired
   private SessionFactory sessionFactory;

   private Session session;

   @BeforeEach
   final void before() {
      final int minimalNumberOfEntities = 25;
      if (fooService.findAll().size() <= minimalNumberOfEntities) {
         for (int i = 0; i < minimalNumberOfEntities; i++) {
            fooService.create(new Foo(randomAlphabetic(6)));
         }
      }

      session = sessionFactory.openSession();
   }

   @AfterEach
   final void after() {
      session.close();
   }

   @Test
   final void whenContextIsBootstrapped_thenNoExceptions() {
      //
   }

   @Test
   final void whenRetrievingPaginatedEntities_thenCorrectSize() {
      final int pageNumber = 1;
      final int pageSize = 10;

      final Query<Foo> query = session.createQuery("From Foo", Foo.class);
      query.setFirstResult((pageNumber - 1) * pageSize);
      query.setMaxResults(pageSize);
      final List<Foo> fooList = query.list();

      assertThat(fooList, hasSize(pageSize));
   }

   @Test
   final void whenRetrievingAllPages_thenCorrect() {
      int pageNumber = 1;
      final int pageSize = 10;

      final String countQ = "Select count (f.id) from Foo f";
      final Query<Long> countQuery = session.createQuery(countQ, Long.class);
      final Long countResult = (Long) countQuery.uniqueResult();

      final List<Foo> fooList = Lists.newArrayList();
      int totalEntities = 0;
      final Query<Foo> query = session.createQuery("From Foo", Foo.class);
      while (totalEntities < countResult) {
         query.setFirstResult((pageNumber - 1) * pageSize);
         query.setMaxResults(pageSize);
         fooList.addAll(query.list());
         totalEntities = fooList.size();
         pageNumber++;
      }
   }

   @Test
   final void whenRetrievingLastPage_thenCorrectSize() {
      final int pageSize = 10;

      final String countQ = "Select count (f.id) from Foo f";
      final Query<Long> countQuery = session.createQuery(countQ, Long.class);
      final Long countResults = countQuery.uniqueResult();
      final int lastPageNumber = (int) (Math.ceil(countResults / pageSize));

      final Query<Foo> selectQuery = session.createQuery("From Foo", Foo.class);
      selectQuery.setFirstResult((lastPageNumber - 1) * pageSize);
      selectQuery.setMaxResults(pageSize);
      final List<Foo> lastPage = selectQuery.list();

      assertThat(lastPage, hasSize(lessThan(pageSize + 1)));
   }

   // testing - scrollable

   @Test
   final void givenUsingTheScrollableApi_whenRetrievingPaginatedData_thenCorrect() {
      final int pageSize = 10;
      final String hql = "FROM Foo f order by f.name";
      final Query<Foo> query = session.createQuery(hql, Foo.class);

      final ScrollableResults<Foo> resultScroll = query.scroll(ScrollMode.FORWARD_ONLY);

      // resultScroll.last();
      // final int totalResults = resultScroll.getRowNumber() + 1;

      resultScroll.first();
      resultScroll.scroll(0);
      final List<Foo> fooPage = Lists.newArrayList();
      int i = 0;
      while (pageSize > i++) {
         fooPage.add((Foo) resultScroll.get());
         if (!resultScroll.next()) {
            break;
         }
      }

      assertThat(fooPage, hasSize(lessThan(10 + 1)));
   }

   @Test
   final void givenUsingTheCriteriaApi_whenRetrievingFirstPage_thenCorrect() {
      final int pageSize = 10;

      CriteriaQuery<Foo> selectQuery = session.getCriteriaBuilder().createQuery(Foo.class);
      selectQuery.from(Foo.class);

      SelectionQuery<Foo> query = session.createQuery(selectQuery);
      query.setFirstResult(0);
      query.setMaxResults(pageSize);
      final List<Foo> firstPage = query.list();

      assertThat(firstPage, hasSize(pageSize));
   }

   @Test
   final void givenUsingTheCriteriaApi_whenRetrievingPaginatedData_thenCorrect() {
      HibernateCriteriaBuilder qb = session.getCriteriaBuilder();
      CriteriaQuery<Long> cq = qb.createQuery(Long.class);
      cq.select(qb.count(cq.from(Foo.class)));
      final Long count = session.createQuery(cq).getSingleResult();

      int pageNumber = 1;
      final int pageSize = 10;
      final List<Foo> fooList = Lists.newArrayList();

      CriteriaQuery<Foo> selectQuery = session.getCriteriaBuilder().createQuery(Foo.class);
      selectQuery.from(Foo.class);
      SelectionQuery<Foo> query = session.createQuery(selectQuery);

      int totalEntities = 0;
      while (totalEntities < count.intValue()) {
         query.setFirstResult((pageNumber - 1) * pageSize);
         query.setMaxResults(pageSize);
         fooList.addAll(query.list());
         totalEntities = fooList.size();
         pageNumber++;
      }
   }
}
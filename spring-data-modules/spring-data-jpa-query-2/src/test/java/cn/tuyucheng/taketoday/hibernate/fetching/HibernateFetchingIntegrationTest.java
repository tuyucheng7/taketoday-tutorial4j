package cn.tuyucheng.taketoday.hibernate.fetching;

import cn.tuyucheng.taketoday.hibernate.fetching.model.OrderDetail;
import cn.tuyucheng.taketoday.hibernate.fetching.view.FetchingAppView;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HibernateFetchingIntegrationTest {

    // this loads sample data in the database
    @Before
    public void addFetchingTestData() {
        FetchingAppView fav = new FetchingAppView();
        fav.createTestData();
    }

    // testLazyFetching() tests the lazy loading Since it lazily loaded so orderDetailSetLazy won't be initialized
    @Test
    public void testLazyFetching() {
        FetchingAppView fav = new FetchingAppView();
        Set<OrderDetail> orderDetailSetLazy = fav.lazyLoaded();
        assertFalse(Hibernate.isInitialized(orderDetailSetLazy));
    }

    // testEagerFetching() tests the eager loading Since it eagerly loaded so orderDetailSetLazy would be initialized
    @Test
    public void testEagerFetching() {
        FetchingAppView fav = new FetchingAppView();
        Set<OrderDetail> orderDetailSetEager = fav.eagerLoaded();
        assertTrue(Hibernate.isInitialized(orderDetailSetEager));
    }
}
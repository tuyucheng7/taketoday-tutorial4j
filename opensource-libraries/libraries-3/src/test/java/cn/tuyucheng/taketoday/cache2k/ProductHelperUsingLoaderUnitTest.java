package cn.tuyucheng.taketoday.cache2k;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProductHelperUsingLoaderUnitTest {

	@Test
	public void whenInvokedGetDiscount_thenPopulateCacheUsingLoader() {
		ProductHelperUsingLoader productHelper = new ProductHelperUsingLoader();
		assertTrue(productHelper.getCacheMissCount() == 0);

		assertTrue(productHelper.getDiscount("Sports") == 20);
		assertTrue(productHelper.getCacheMissCount() == 1);

		assertTrue(productHelper.getDiscount("Electronics") == 10);
		assertTrue(productHelper.getCacheMissCount() == 2);
	}

}

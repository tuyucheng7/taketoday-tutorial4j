package cn.tuyucheng.taketoday.cache2k;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProductHelperUnitTest {

	@Test
	public void whenInvokedGetDiscountTwice_thenGetItFromCache() {
		ProductHelper productHelper = new ProductHelper();
		assertTrue(productHelper.getCacheMissCount() == 0);
		assertTrue(productHelper.getDiscount("Sports") == 20);
		assertTrue(productHelper.getDiscount("Sports") == 20);
		assertTrue(productHelper.getCacheMissCount() == 1);
	}

}

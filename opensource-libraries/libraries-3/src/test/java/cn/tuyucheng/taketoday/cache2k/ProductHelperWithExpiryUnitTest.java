package cn.tuyucheng.taketoday.cache2k;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProductHelperWithExpiryUnitTest {

	@Test
	public void whenInvokedGetDiscountAfterExpiration_thenDiscountCalculatedAgain() throws InterruptedException {
		ProductHelperWithExpiry productHelper = new ProductHelperWithExpiry();
		assertTrue(productHelper.getCacheMissCount() == 0);
		assertTrue(productHelper.getDiscount("Sports") == 20);
		assertTrue(productHelper.getCacheMissCount() == 1);

		Thread.sleep(20);

		assertTrue(productHelper.getDiscount("Sports") == 20);
		assertTrue(productHelper.getCacheMissCount() == 2);
	}

}

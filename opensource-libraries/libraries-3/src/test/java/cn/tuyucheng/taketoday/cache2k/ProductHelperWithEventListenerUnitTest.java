package cn.tuyucheng.taketoday.cache2k;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProductHelperWithEventListenerUnitTest {

	@Test
	public void whenEntryAddedInCache_thenEventListenerCalled() {
		ProductHelperWithEventListener productHelper = new ProductHelperWithEventListener();
		assertTrue(productHelper.getDiscount("Sports") == 20);
	}

}

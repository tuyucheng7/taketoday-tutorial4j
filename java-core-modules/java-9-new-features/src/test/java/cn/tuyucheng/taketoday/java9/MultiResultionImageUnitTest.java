package cn.tuyucheng.taketoday.java9;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MultiResultionImageUnitTest {

	@Test
	void baseMultiResImageTest() {
		int baseIndex = 1;
		int length = 4;
		BufferedImage[] resolutionVariants = new BufferedImage[length];
		for (int i = 0; i < length; i++) {
			resolutionVariants[i] = createImage(i);
		}
		MultiResolutionImage bmrImage = new BaseMultiResolutionImage(baseIndex, resolutionVariants);
		List<Image> rvImageList = bmrImage.getResolutionVariants();
		assertEquals(rvImageList.size(), length, "MultiResoltion Image shoudl contain the same number of resolution variants!");

		for (int i = 0; i < length; i++) {
			int imageSize = getSize(i);
			Image testRVImage = bmrImage.getResolutionVariant(imageSize, imageSize);
			assertSame(testRVImage, resolutionVariants[i], "Images should be the same");
		}

	}

	private static int getSize(int i) {
		return 8 * (i + 1);
	}

	private static BufferedImage createImage(int i) {
		return new BufferedImage(getSize(i), getSize(i), BufferedImage.TYPE_INT_RGB);
	}
}
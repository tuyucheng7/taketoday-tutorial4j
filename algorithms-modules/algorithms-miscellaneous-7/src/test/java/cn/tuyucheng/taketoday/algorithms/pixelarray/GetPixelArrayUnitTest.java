package cn.tuyucheng.taketoday.algorithms.pixelarray;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class GetPixelArrayUnitTest {
   @Test
   public void givenImage_whenGetPixelArray_thenBothMethodsReturnEqualValues() {
      BufferedImage sampleImage = null;
      try {
         sampleImage = ImageIO.read(new File("src/main/resources/images/sampleImage.jpg"));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      int[][] firstResult = GetPixelArray.get2DPixelArraySlow(sampleImage);
      int[][] secondResult = GetPixelArray.get2DPixelArrayFast(sampleImage);

      assertTrue(Arrays.deepEquals(firstResult, secondResult));
   }
}

package cn.tuyucheng.taketoday.implementsvsextends.model;

import cn.tuyucheng.taketoday.implementsvsextends.media.model.Media;
import cn.tuyucheng.taketoday.implementsvsextends.media.model.VideoMedia;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VideoMediaUnitTest {

   @Test
   public void givenVideoMediaInstance_whenCheckedType_thenIsInstanceOfMedia() {
      VideoMedia videoMedia = new VideoMedia();
      Assertions.assertThat(videoMedia, CoreMatchers.<VideoMedia>instanceOf(Media.class));
   }
}
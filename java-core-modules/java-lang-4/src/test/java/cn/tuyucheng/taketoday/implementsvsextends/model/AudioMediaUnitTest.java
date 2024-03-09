package cn.tuyucheng.taketoday.implementsvsextends.model;

import cn.tuyucheng.taketoday.implementsvsextends.media.model.AudioMedia;
import cn.tuyucheng.taketoday.implementsvsextends.media.model.Media;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AudioMediaUnitTest {

   @Test
   public void givenAudioMediaInstance_whenCheckedType_thenIsInstanceOfMedia() {
      AudioMedia audioMedia = new AudioMedia();
      Assertions.assertThat(audioMedia, CoreMatchers.<AudioMedia>instanceOf(Media.class));
   }
}
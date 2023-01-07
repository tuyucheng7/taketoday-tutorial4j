package cn.tuyucheng.taketoday.implementsvsextends;

import cn.tuyucheng.taketoday.implementsvsextends.media.model.AudioMedia;
import cn.tuyucheng.taketoday.implementsvsextends.media.model.Media;
import cn.tuyucheng.taketoday.implementsvsextends.media.model.VideoMedia;
import cn.tuyucheng.taketoday.implementsvsextends.media.player.impl.AudioMediaPlayer;
import cn.tuyucheng.taketoday.implementsvsextends.media.player.impl.CustomMediaPlayer;
import cn.tuyucheng.taketoday.implementsvsextends.media.player.impl.MultiMediaPlayer;
import cn.tuyucheng.taketoday.implementsvsextends.media.player.impl.VideoMediaPlayer;

public class Main {

	public static void main(String[] args) {

		Media media = new Media();
		media.setId(001);
		media.setTitle("Media1");
		media.setArtist("Artist001");

		AudioMedia audioMedia = new AudioMedia();
		audioMedia.setId(101);
		audioMedia.setTitle("Audio1");
		audioMedia.setArtist("Artist101");
		audioMedia.setBitrate(3500);
		audioMedia.setFrequency("256kbps");

		VideoMedia videoMedia = new VideoMedia();
		videoMedia.setId(201);
		videoMedia.setTitle("Video1");
		videoMedia.setArtist("Artist201");
		videoMedia.setResolution("1024x768");
		videoMedia.setAspectRatio("16:9");

		System.out.println(media);
		System.out.println(audioMedia);
		System.out.println(videoMedia);

		AudioMediaPlayer audioMediaPlayer = new AudioMediaPlayer();
		audioMediaPlayer.play();
		audioMediaPlayer.pause();

		VideoMediaPlayer videoMediaPlayer = new VideoMediaPlayer();
		videoMediaPlayer.play();
		videoMediaPlayer.pause();

		MultiMediaPlayer multiMediaPlayer = new MultiMediaPlayer();
		multiMediaPlayer.play();
		multiMediaPlayer.pause();
		multiMediaPlayer.seek();
		multiMediaPlayer.fastForward();

		CustomMediaPlayer customMediaPlayer = new CustomMediaPlayer();
		customMediaPlayer.play();
		customMediaPlayer.pause();
		customMediaPlayer.setId(301);
		customMediaPlayer.setTitle("Custom1");
		customMediaPlayer.setArtist("Artist301");
		System.out.println(customMediaPlayer);
	}
}

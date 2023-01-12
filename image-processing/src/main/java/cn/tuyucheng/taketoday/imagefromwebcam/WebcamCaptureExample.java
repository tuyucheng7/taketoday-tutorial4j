package cn.tuyucheng.taketoday.imagefromwebcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.util.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WebcamCaptureExample {

	public static void main(String[] args) throws IOException, Exception {
		Webcam webcam = Webcam.getDefault();
		webcam.open();

		BufferedImage image = webcam.getImage();

		ImageIO.write(image, ImageUtils.FORMAT_JPG, new File("selfie.jpg"));
	}

	public void captureWithPanel() {
		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setImageSizeDisplayed(true);

		JFrame window = new JFrame("Webcam");
		window.add(panel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}

}

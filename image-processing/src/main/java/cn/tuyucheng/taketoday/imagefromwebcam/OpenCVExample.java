package cn.tuyucheng.taketoday.imagefromwebcam;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.awt.event.WindowEvent;

import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;

public class OpenCVExample {

	public static void main(String[] args) throws Exception {
		CanvasFrame canvas = new CanvasFrame("Web Cam");
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		FrameGrabber grabber = new OpenCVFrameGrabber(0);
		OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

		grabber.start();
		Frame frame = grabber.grab();

		IplImage img = converter.convert(frame);
		cvSaveImage("selfie.jpg", img);

		canvas.showImage(frame);

		Thread.sleep(2000);

		canvas.dispatchEvent(new WindowEvent(canvas, WindowEvent.WINDOW_CLOSING));
	}

}

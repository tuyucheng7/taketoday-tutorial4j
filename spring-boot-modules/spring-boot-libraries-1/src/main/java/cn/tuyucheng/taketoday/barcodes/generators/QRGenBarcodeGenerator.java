package cn.tuyucheng.taketoday.barcodes.generators;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import net.glxn.qrgen.javase.QRCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@ExcludeFromJacocoGeneratedReport
public class QRGenBarcodeGenerator {

	public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
		ByteArrayOutputStream stream = QRCode
			.from(barcodeText)
			.withSize(250, 250)
			.stream();
		ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

		return ImageIO.read(bis);
	}
}
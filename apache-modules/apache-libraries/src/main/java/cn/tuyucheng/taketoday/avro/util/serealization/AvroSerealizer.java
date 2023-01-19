package cn.tuyucheng.taketoday.avro.util.serealization;

import cn.tuyucheng.taketoday.avro.util.model.AvroHttpRequest;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AvroSerealizer {

	private static final Logger logger = LoggerFactory.getLogger(AvroSerealizer.class);

	public byte[] serealizeAvroHttpRequestJSON(AvroHttpRequest request) {
		DatumWriter<AvroHttpRequest> writer = new SpecificDatumWriter<>(AvroHttpRequest.class);
		byte[] data = new byte[0];
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Encoder jsonEncoder = null;
		try {
			jsonEncoder = EncoderFactory.get()
				.jsonEncoder(AvroHttpRequest.getClassSchema(), stream);
			writer.write(request, jsonEncoder);
			jsonEncoder.flush();
			data = stream.toByteArray();
		} catch (IOException e) {
			logger.error("Serialization error " + e.getMessage());
		}
		return data;
	}

	public byte[] serealizeAvroHttpRequestBinary(AvroHttpRequest request) {
		DatumWriter<AvroHttpRequest> writer = new SpecificDatumWriter<>(AvroHttpRequest.class);
		byte[] data = new byte[0];
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Encoder jsonEncoder = EncoderFactory.get()
			.binaryEncoder(stream, null);
		try {
			writer.write(request, jsonEncoder);
			jsonEncoder.flush();
			data = stream.toByteArray();
		} catch (IOException e) {
			logger.error("Serialization error " + e.getMessage());
		}

		return data;
	}

}

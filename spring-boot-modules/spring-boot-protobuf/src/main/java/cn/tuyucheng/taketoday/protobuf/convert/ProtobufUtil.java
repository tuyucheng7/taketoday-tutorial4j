package cn.tuyucheng.taketoday.protobuf.convert;

import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;

public class ProtobufUtil {

	public static String toJson(MessageOrBuilder messageOrBuilder) throws IOException {
		return JsonFormat.printer().print(messageOrBuilder);
	}

	@SuppressWarnings("unchecked")
	public static Message fromJson(String json) throws IOException {
		Builder structBuilder = Struct.newBuilder();
		JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
		return structBuilder.build();
	}

}

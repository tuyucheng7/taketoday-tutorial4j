package cn.tuyucheng.taketoday.grpc.errorhandling;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.protobuf.StatusProto;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommodityServerUnitTest {

	cn.tuyucheng.taketoday.grpc.errorhandling.CommodityPriceProviderGrpc.CommodityPriceProviderBlockingStub blockingStub;

	@Rule
	public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

	@Before
	public void setup() throws Exception {
		String serverName = InProcessServerBuilder.generateName();

		grpcCleanup.register(InProcessServerBuilder.forName(serverName)
			.directExecutor()
			.addService(new CommodityServer.CommodityService())
			.build()
			.start());

		blockingStub = cn.tuyucheng.taketoday.grpc.errorhandling.CommodityPriceProviderGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName)
			.directExecutor()
			.build()));
	}

	@Test
	public void whenUsingValidRequest_thenReturnResponse() throws Exception {
		cn.tuyucheng.taketoday.grpc.errorhandling.CommodityQuote reply = blockingStub.getBestCommodityPrice(cn.tuyucheng.taketoday.grpc.errorhandling.Commodity.newBuilder()
			.setCommodityName("Commodity1")
			.setAccessToken("123validToken")
			.build());

		assertEquals("Commodity1", reply.getCommodityName());
	}

	@Test
	public void whenUsingInvalidRequestToken_thenReturnExceptionGoogleRPCStatus() throws Exception {
		cn.tuyucheng.taketoday.grpc.errorhandling.Commodity request = cn.tuyucheng.taketoday.grpc.errorhandling.Commodity.newBuilder()
			.setAccessToken("invalidToken")
			.setCommodityName("Commodity1")
			.build();

		StatusRuntimeException thrown = Assertions.assertThrows(StatusRuntimeException.class, () -> blockingStub.getBestCommodityPrice(request));

		com.google.rpc.Status status = StatusProto.fromThrowable(thrown);
		assertNotNull(status);
		assertEquals("NOT_FOUND", Code.forNumber(status.getCode()).toString());
		assertEquals("The access token not found", status.getMessage());
		for (Any any : status.getDetailsList()) {
			if (any.is(ErrorInfo.class)) {
				ErrorInfo errorInfo = any.unpack(ErrorInfo.class);
				assertEquals("Invalid Token", errorInfo.getReason());
				assertEquals("cn.tuyucheng.taketoday.grpc.errorhandling", errorInfo.getDomain());
				assertEquals("123validToken", errorInfo.getMetadataMap().get("insertToken"));
			}
		}
	}

	@Test
	public void whenUsingInvalidCommodityName_thenReturnExceptionIoRpcStatus() throws Exception {
		cn.tuyucheng.taketoday.grpc.errorhandling.Commodity request = cn.tuyucheng.taketoday.grpc.errorhandling.Commodity.newBuilder()
			.setAccessToken("123validToken")
			.setCommodityName("Commodity5")
			.build();

		StatusRuntimeException thrown = Assertions.assertThrows(StatusRuntimeException.class, () -> blockingStub.getBestCommodityPrice(request));

		assertEquals("INVALID_ARGUMENT", thrown.getStatus().getCode().toString());
		assertEquals("INVALID_ARGUMENT: The commodity is not supported", thrown.getMessage());
		Metadata metadata = Status.trailersFromThrowable(thrown);
		cn.tuyucheng.taketoday.grpc.errorhandling.ErrorResponse errorResponse = metadata.get(ProtoUtils.keyForProto(cn.tuyucheng.taketoday.grpc.errorhandling.ErrorResponse.getDefaultInstance()));
		assertEquals("Commodity5", errorResponse.getCommodityName());
		assertEquals("123validToken", errorResponse.getAccessToken());
		assertEquals("Only Commodity1, Commodity2 are supported", errorResponse.getExpectedValue());
	}
}
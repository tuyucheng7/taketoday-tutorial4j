package cn.tuyucheng.taketoday.aws.handler;

import cn.tuyucheng.taketoday.aws.Application;
import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AsynchronousLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
	private SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

	public AsynchronousLambdaHandler() throws ContainerInitializationException {
		handler = (SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>) new SpringBootProxyHandlerBuilder().springBootApplication(Application.class)
			.asyncInit()
			.buildAndInitialize();
	}

	@Override
	public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
		return handler.proxy(input, context);
	}
}
package cn.tuyucheng.taketoday.lambda.todo;

import cn.tuyucheng.taketoday.lambda.todo.config.ExecutionContext;
import cn.tuyucheng.taketoday.lambda.todo.service.PostService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestStreamHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	private String environmentName = System.getenv("ENV_NAME");
	private ExecutionContext executionContext = new ExecutionContext();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		context.getLogger().log("App starting\n");
		context.getLogger().log("Environment: "
			+ environmentName + "\n");

		try {
			PostService postService = executionContext.getPostService();
			executionContext.getToDoReaderService()
				.getOldestToDo()
				.ifPresent(postService::makePost);
		} catch (Exception e) {
			LOGGER.error("Failed: {}", e.getMessage(), e);
		}
	}
}
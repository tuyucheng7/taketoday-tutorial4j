package cn.tuyucheng.taketoday.graphql.handler;

import cn.tuyucheng.taketoday.graphql.entity.User;
import cn.tuyucheng.taketoday.graphql.schema.UserSchema;
import cn.tuyucheng.taketoday.graphql.utils.SchemaUtils;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetchingEnvironment;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static ratpack.jackson.Jackson.json;

public class UserHandler implements Handler {

	private static final Logger LOGGER = Logger.getLogger(UserHandler.class.getSimpleName());

	private static final List<User> USERS = new ArrayList<>();

	private final GraphQL graphql;

	public UserHandler() throws Exception {
		graphql = GraphQL.newGraphQL(new UserSchema().getSchema()).build();
	}

	@Override
	public void handle(Context context) {
		context.parse(Map.class)
			.then(payload -> {
				Map<String, Object> parameters = (Map<String, Object>) payload.get("parameters");
				ExecutionResult executionResult = graphql.execute(payload.get(SchemaUtils.QUERY)
					.toString(), null, this, parameters);

				Map<String, Object> result = new LinkedHashMap<>();
				if (executionResult.getErrors().isEmpty()) {
					result.put(SchemaUtils.DATA, executionResult.getData());
				} else {
					result.put(SchemaUtils.ERRORS, executionResult.getErrors());
					LOGGER.warning("Errors: " + executionResult.getErrors());
				}
				context.render(json(result));
			});
	}

	public List<User> getUsers() {
		return USERS;
	}

	public static List<User> getUsers(DataFetchingEnvironment env) {
		return ((UserHandler) env.getSource()).getUsers();
	}
}
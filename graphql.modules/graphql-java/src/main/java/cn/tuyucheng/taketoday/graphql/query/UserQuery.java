package cn.tuyucheng.taketoday.graphql.query;

import cn.tuyucheng.taketoday.graphql.entity.User;
import cn.tuyucheng.taketoday.graphql.handler.UserHandler;
import cn.tuyucheng.taketoday.graphql.utils.SchemaUtils;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@GraphQLName(SchemaUtils.QUERY)
public class UserQuery {

	@GraphQLField
	public static User retrieveUser(final DataFetchingEnvironment env, @NotNull @GraphQLName(SchemaUtils.ID) final String id) {
		final Optional<User> any = UserHandler.getUsers(env).stream()
			.filter(c -> c.getId() == Long.parseLong(id))
			.findFirst();
		return any.orElse(null);
	}

	@GraphQLField
	public static List<User> searchName(final DataFetchingEnvironment env, @NotNull @GraphQLName(SchemaUtils.NAME) final String name) {
		return UserHandler.getUsers(env).stream()
			.filter(c -> c.getName()
				.contains(name))
			.collect(Collectors.toList());
	}

	@GraphQLField
	public static List<User> listUsers(final DataFetchingEnvironment env) {
		return UserHandler.getUsers(env);
	}
}
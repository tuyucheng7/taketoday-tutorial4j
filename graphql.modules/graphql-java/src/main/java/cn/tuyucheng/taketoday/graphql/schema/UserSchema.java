package cn.tuyucheng.taketoday.graphql.schema;

import cn.tuyucheng.taketoday.graphql.mutation.UserMutation;
import cn.tuyucheng.taketoday.graphql.query.UserQuery;
import graphql.annotations.GraphQLAnnotations;
import graphql.schema.GraphQLSchema;

import static graphql.schema.GraphQLSchema.newSchema;

public class UserSchema {

	private final GraphQLSchema schema;

	public UserSchema() throws IllegalAccessException, NoSuchMethodException, InstantiationException {
		schema = newSchema().query(GraphQLAnnotations.object(UserQuery.class))
			.mutation(GraphQLAnnotations.object(UserMutation.class))
			.build();
	}

	public GraphQLSchema getSchema() {
		return schema;
	}
}
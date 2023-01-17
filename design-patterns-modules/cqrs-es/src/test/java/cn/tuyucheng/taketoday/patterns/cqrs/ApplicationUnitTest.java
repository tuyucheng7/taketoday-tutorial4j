package cn.tuyucheng.taketoday.patterns.cqrs;

import cn.tuyucheng.taketoday.patterns.cqrs.aggregates.UserAggregate;
import cn.tuyucheng.taketoday.patterns.cqrs.commands.CreateUserCommand;
import cn.tuyucheng.taketoday.patterns.cqrs.commands.UpdateUserCommand;
import cn.tuyucheng.taketoday.patterns.cqrs.projections.UserProjection;
import cn.tuyucheng.taketoday.patterns.cqrs.projectors.UserProjector;
import cn.tuyucheng.taketoday.patterns.cqrs.queries.AddressByRegionQuery;
import cn.tuyucheng.taketoday.patterns.cqrs.queries.ContactByTypeQuery;
import cn.tuyucheng.taketoday.patterns.cqrs.repository.UserReadRepository;
import cn.tuyucheng.taketoday.patterns.cqrs.repository.UserWriteRepository;
import cn.tuyucheng.taketoday.patterns.domain.Address;
import cn.tuyucheng.taketoday.patterns.domain.Contact;
import cn.tuyucheng.taketoday.patterns.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ApplicationUnitTest {

	private UserWriteRepository writeRepository;
	private UserReadRepository readRepository;
	private UserProjector projector;
	private UserAggregate userAggregate;
	private UserProjection userProjection;

	@BeforeEach
	void setUp() {
		writeRepository = new UserWriteRepository();
		readRepository = new UserReadRepository();
		projector = new UserProjector(readRepository);
		userAggregate = new UserAggregate(writeRepository);
		userProjection = new UserProjection(readRepository);
	}

	@Test
	void givenCQRSApplication_whenCommandRun_thenQueryShouldReturnResult() throws Exception {
		String userId = UUID.randomUUID()
			.toString();
		User user = null;
		CreateUserCommand createUserCommand = new CreateUserCommand(userId, "Tom", "Sawyer");
		user = userAggregate.handleCreateUserCommand(createUserCommand);
		projector.project(user);

		UpdateUserCommand updateUserCommand = new UpdateUserCommand(user.getUserid(), Stream.of(new Address("New York", "NY", "10001"), new Address("Los Angeles", "CA", "90001"))
			.collect(Collectors.toSet()),
			Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("EMAIL", "tom.sawyer@rediff.com"))
				.collect(Collectors.toSet()));
		user = userAggregate.handleUpdateUserCommand(updateUserCommand);
		projector.project(user);

		updateUserCommand = new UpdateUserCommand(userId, Stream.of(new Address("New York", "NY", "10001"), new Address("Housten", "TX", "77001"))
			.collect(Collectors.toSet()),
			Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("PHONE", "700-000-0001"))
				.collect(Collectors.toSet()));
		user = userAggregate.handleUpdateUserCommand(updateUserCommand);
		projector.project(user);

		ContactByTypeQuery contactByTypeQuery = new ContactByTypeQuery(userId, "EMAIL");
		Assertions.assertEquals(Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"))
			.collect(Collectors.toSet()), userProjection.handle(contactByTypeQuery));
		AddressByRegionQuery addressByRegionQuery = new AddressByRegionQuery(userId, "NY");
		Assertions.assertEquals(Stream.of(new Address("New York", "NY", "10001"))
			.collect(Collectors.toSet()), userProjection.handle(addressByRegionQuery));

	}
}
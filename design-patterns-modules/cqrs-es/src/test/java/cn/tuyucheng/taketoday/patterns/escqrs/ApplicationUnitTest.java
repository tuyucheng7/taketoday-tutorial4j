package cn.tuyucheng.taketoday.patterns.escqrs;

import cn.tuyucheng.taketoday.patterns.cqrs.commands.CreateUserCommand;
import cn.tuyucheng.taketoday.patterns.cqrs.commands.UpdateUserCommand;
import cn.tuyucheng.taketoday.patterns.cqrs.projections.UserProjection;
import cn.tuyucheng.taketoday.patterns.cqrs.queries.AddressByRegionQuery;
import cn.tuyucheng.taketoday.patterns.cqrs.queries.ContactByTypeQuery;
import cn.tuyucheng.taketoday.patterns.cqrs.repository.UserReadRepository;
import cn.tuyucheng.taketoday.patterns.domain.Address;
import cn.tuyucheng.taketoday.patterns.domain.Contact;
import cn.tuyucheng.taketoday.patterns.es.events.Event;
import cn.tuyucheng.taketoday.patterns.es.repository.EventStore;
import cn.tuyucheng.taketoday.patterns.escqrs.aggregates.UserAggregate;
import cn.tuyucheng.taketoday.patterns.escqrs.projectors.UserProjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationUnitTest {

    private EventStore writeRepository;
    private UserReadRepository readRepository;
    private UserProjector projector;
    private UserAggregate userAggregate;
    private UserProjection userProjection;

    @BeforeEach
    void setUp() {
        writeRepository = new EventStore();
        readRepository = new UserReadRepository();
        projector = new UserProjector(readRepository);
        userAggregate = new UserAggregate(writeRepository);
        userProjection = new UserProjection(readRepository);
    }

    @Test
    void givenCQRSApplication_whenCommandRun_thenQueryShouldReturnResult() throws Exception {
        String userId = UUID.randomUUID()
            .toString();
        List<Event> events = null;
        CreateUserCommand createUserCommand = new CreateUserCommand(userId, "Kumar", "Chandrakant");
        events = userAggregate.handleCreateUserCommand(createUserCommand);

        projector.project(userId, events);

        UpdateUserCommand updateUserCommand = new UpdateUserCommand(userId, Stream.of(new Address("New York", "NY", "10001"), new Address("Los Angeles", "CA", "90001"))
            .collect(Collectors.toSet()),
            Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("EMAIL", "tom.sawyer@rediff.com"))
                .collect(Collectors.toSet()));
        events = userAggregate.handleUpdateUserCommand(updateUserCommand);
        projector.project(userId, events);

        updateUserCommand = new UpdateUserCommand(userId, Stream.of(new Address("New York", "NY", "10001"), new Address("Housten", "TX", "77001"))
            .collect(Collectors.toSet()),
            Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("PHONE", "700-000-0001"))
                .collect(Collectors.toSet()));
        events = userAggregate.handleUpdateUserCommand(updateUserCommand);
        projector.project(userId, events);

        ContactByTypeQuery contactByTypeQuery = new ContactByTypeQuery(userId, "EMAIL");
        assertEquals(Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"))
            .collect(Collectors.toSet()), userProjection.handle(contactByTypeQuery));
        AddressByRegionQuery addressByRegionQuery = new AddressByRegionQuery(userId, "NY");
        assertEquals(Stream.of(new Address("New York", "NY", "10001"))
            .collect(Collectors.toSet()), userProjection.handle(addressByRegionQuery));

    }

}

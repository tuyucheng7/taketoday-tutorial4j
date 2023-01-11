package cn.tuyucheng.taketoday.boot.composite.key;

import cn.tuyucheng.taketoday.boot.composite.key.data.Ticket;
import cn.tuyucheng.taketoday.boot.composite.key.data.TicketId;
import cn.tuyucheng.taketoday.boot.composite.key.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringBootCompositeKeyApplication.class)
@DirtiesContext
@ExtendWith(SpringExtension.class)
@TestPropertySource("/embedded.properties")
class CustomerServiceIntegrationTest {
    @Autowired
    private CustomerService service;

    @Test
    void givenCompositeId_whenObjectSaved_thenIdMatches() {
        TicketId ticketId = new TicketId();
        ticketId.setDate("2020-01-01");
        ticketId.setVenue("Venue A");

        Ticket ticket = new Ticket(ticketId, "Event A");
        Ticket savedTicket = service.insert(ticket);

        assertEquals(savedTicket.getId(), ticket.getId());
    }

    @Test
    void givenCompositeId_whenSearchingByIdObject_thenFound() {
        TicketId ticketId = new TicketId();
        ticketId.setDate("2020-01-01");
        ticketId.setVenue("Venue B");

        service.insert(new Ticket(ticketId, "Event B"));

        Optional<Ticket> optionalTicket = service.find(ticketId);

        assertThat(optionalTicket.isPresent());
        Ticket savedTicket = optionalTicket.get();

        assertEquals(savedTicket.getId(), ticketId);
    }

    @Test
    void givenCompositeId_whenDupeInsert_thenExceptionIsThrown() {
        TicketId ticketId = new TicketId();
        ticketId.setDate("2020-01-01");
        ticketId.setVenue("V");

        Ticket ticket = new Ticket(ticketId, "Event C");
        service.insert(ticket);

        assertThrows(DuplicateKeyException.class, () -> service.insert(ticket));
    }

    @Test
    void givenCompositeId_whenDupeSave_thenObjectUpdated() {
        TicketId ticketId = new TicketId();
        ticketId.setDate("2020-01-01");
        ticketId.setVenue("Venue");

        Ticket ticketA = new Ticket(ticketId, "A");
        service.save(ticketA);

        Ticket ticketB = new Ticket(ticketId, "B");
        Ticket savedTicket = service.save(ticketB);

        assertEquals(savedTicket.getEvent(), ticketB.getEvent());
    }
}
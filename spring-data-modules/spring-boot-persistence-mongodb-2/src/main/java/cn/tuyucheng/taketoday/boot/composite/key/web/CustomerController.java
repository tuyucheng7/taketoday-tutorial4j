package cn.tuyucheng.taketoday.boot.composite.key.web;

import cn.tuyucheng.taketoday.boot.composite.key.data.Ticket;
import cn.tuyucheng.taketoday.boot.composite.key.data.TicketId;
import cn.tuyucheng.taketoday.boot.composite.key.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/ticket")
    public Optional<Ticket> getTicket(TicketId id) {
        return customerService.find(id);
    }

    @PostMapping("/ticket")
    public Ticket postTicket(@RequestBody Ticket ticket) {
        return customerService.insert(ticket);
    }

    @PutMapping("/ticket")
    public Ticket putTicket(@RequestBody Ticket ticket) {
        return customerService.save(ticket);
    }
}
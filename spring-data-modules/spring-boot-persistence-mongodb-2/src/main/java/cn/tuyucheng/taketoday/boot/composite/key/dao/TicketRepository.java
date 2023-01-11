package cn.tuyucheng.taketoday.boot.composite.key.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.tuyucheng.taketoday.boot.composite.key.data.Ticket;
import cn.tuyucheng.taketoday.boot.composite.key.data.TicketId;

public interface TicketRepository extends MongoRepository<Ticket, TicketId> {

}
package cn.tuyucheng.taketoday.controller;

import cn.tuyucheng.taketoday.controller.repository.AddressRepository;
import cn.tuyucheng.taketoday.controller.repository.UserRepository;
import cn.tuyucheng.taketoday.entity.Address;
import cn.tuyucheng.taketoday.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController {

   @Autowired
   private UserRepository personRepository;
   @Autowired
   private AddressRepository addressRepository;

   @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
   public Iterable<User> queryOverUser(@QuerydslPredicate(root = User.class) Predicate predicate) {
      final BooleanBuilder builder = new BooleanBuilder();
      return personRepository.findAll(builder.and(predicate));
   }

   @GetMapping(value = "/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
   public Iterable<Address> queryOverAddress(@QuerydslPredicate(root = Address.class) Predicate predicate) {
      final BooleanBuilder builder = new BooleanBuilder();
      return addressRepository.findAll(builder.and(predicate));
   }
}
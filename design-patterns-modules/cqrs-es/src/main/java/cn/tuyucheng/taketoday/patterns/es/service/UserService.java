package cn.tuyucheng.taketoday.patterns.es.service;

import java.util.Set;
import java.util.stream.Collectors;

import cn.tuyucheng.taketoday.patterns.domain.Address;
import cn.tuyucheng.taketoday.patterns.domain.Contact;
import cn.tuyucheng.taketoday.patterns.domain.User;
import cn.tuyucheng.taketoday.patterns.es.events.UserAddressAddedEvent;
import cn.tuyucheng.taketoday.patterns.es.events.UserAddressRemovedEvent;
import cn.tuyucheng.taketoday.patterns.es.events.UserContactAddedEvent;
import cn.tuyucheng.taketoday.patterns.es.events.UserContactRemovedEvent;
import cn.tuyucheng.taketoday.patterns.es.events.UserCreatedEvent;
import cn.tuyucheng.taketoday.patterns.es.repository.EventStore;

public class UserService {

    private EventStore repository;

    public UserService(EventStore repository) {
        this.repository = repository;
    }

    public void createUser(String userId, String firstName, String lastName) {
        repository.addEvent(userId, new UserCreatedEvent(userId, firstName, lastName));
    }

    public void updateUser(String userId, Set<Contact> contacts, Set<Address> addresses) throws Exception {
        User user = UserUtility.recreateUserState(repository, userId);
        if (user == null)
            throw new Exception("User does not exist.");

        user.getContacts()
            .stream()
            .filter(c -> !contacts.contains(c))
            .forEach(c -> repository.addEvent(userId, new UserContactRemovedEvent(c.getType(), c.getDetail())));
        contacts.stream()
            .filter(c -> !user.getContacts()
                .contains(c))
            .forEach(c -> repository.addEvent(userId, new UserContactAddedEvent(c.getType(), c.getDetail())));
        user.getAddresses()
            .stream()
            .filter(a -> !addresses.contains(a))
            .forEach(a -> repository.addEvent(userId, new UserAddressRemovedEvent(a.getCity(), a.getState(), a.getPostcode())));
        addresses.stream()
            .filter(a -> !user.getAddresses()
                .contains(a))
            .forEach(a -> repository.addEvent(userId, new UserAddressAddedEvent(a.getCity(), a.getState(), a.getPostcode())));
    }

    public Set<Contact> getContactByType(String userId, String contactType) throws Exception {
        User user = UserUtility.recreateUserState(repository, userId);
        if (user == null)
            throw new Exception("User does not exist.");
        return user.getContacts()
            .stream()
            .filter(c -> c.getType()
                .equals(contactType))
            .collect(Collectors.toSet());
    }

    public Set<Address> getAddressByRegion(String userId, String state) throws Exception {
        User user = UserUtility.recreateUserState(repository, userId);
        if (user == null)
            throw new Exception("User does not exist.");
        return user.getAddresses()
            .stream()
            .filter(a -> a.getState()
                .equals(state))
            .collect(Collectors.toSet());
    }
}

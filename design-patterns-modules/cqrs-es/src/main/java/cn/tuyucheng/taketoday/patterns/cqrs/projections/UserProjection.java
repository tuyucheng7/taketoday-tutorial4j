package cn.tuyucheng.taketoday.patterns.cqrs.projections;

import java.util.Set;

import cn.tuyucheng.taketoday.patterns.cqrs.queries.AddressByRegionQuery;
import cn.tuyucheng.taketoday.patterns.cqrs.queries.ContactByTypeQuery;
import cn.tuyucheng.taketoday.patterns.cqrs.repository.UserReadRepository;
import cn.tuyucheng.taketoday.patterns.domain.Address;
import cn.tuyucheng.taketoday.patterns.domain.Contact;
import cn.tuyucheng.taketoday.patterns.domain.UserAddress;
import cn.tuyucheng.taketoday.patterns.domain.UserContact;

public class UserProjection {

    private UserReadRepository repository;

    public UserProjection(UserReadRepository repository) {
        this.repository = repository;
    }

    public Set<Contact> handle(ContactByTypeQuery query) throws Exception {
        UserContact userContact = repository.getUserContact(query.getUserId());
        if (userContact == null)
            throw new Exception("User does not exist.");
        return userContact.getContactByType()
            .get(query.getContactType());
    }

    public Set<Address> handle(AddressByRegionQuery query) throws Exception {
        UserAddress userAddress = repository.getUserAddress(query.getUserId());
        if (userAddress == null)
            throw new Exception("User does not exist.");
        return userAddress.getAddressByRegion()
            .get(query.getState());
    }

}

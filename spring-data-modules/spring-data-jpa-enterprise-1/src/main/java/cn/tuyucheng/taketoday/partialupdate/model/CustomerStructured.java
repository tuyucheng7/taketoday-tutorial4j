package cn.tuyucheng.taketoday.partialupdate.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class CustomerStructured {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public String name;
    @OneToMany(fetch = FetchType.EAGER, targetEntity = ContactPhone.class, mappedBy = "customerId")
    public List<ContactPhone> contactPhones;

    @Override
    public String toString() {
        return String.format("Customer %s, Phone: %s",
                this.name, this.contactPhones.stream().map(ContactPhone::toString).reduce("", String::concat));
    }
}
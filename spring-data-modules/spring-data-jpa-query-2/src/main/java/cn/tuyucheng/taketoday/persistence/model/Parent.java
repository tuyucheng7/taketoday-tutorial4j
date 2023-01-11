package cn.tuyucheng.taketoday.persistence.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Parent implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "child_fk")
    private Child child;

    public Parent() {
        super();
    }

    public Parent(final Child child) {
        super();

        this.child = child;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(final Child child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "Parent [id=" + id + "]";
    }
}
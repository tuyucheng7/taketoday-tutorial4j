package cn.tuyucheng.taketoday.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
public class Child implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne(mappedBy = "child")
    private Parent parent;

    public Child() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(final Parent parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Child [id=" + id + "]";
    }
}
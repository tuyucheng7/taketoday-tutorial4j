package cn.tuyucheng.taketoday.enumerationiteratordifferences;

import java.util.Iterator;
import java.util.List;

import static cn.tuyucheng.taketoday.enumerationiteratordifferences.DataUtil.getPersons;

public class IteratorExample {
    public static void main(String[] args) {
        List<Person> persons = getPersons();
        Iterator<Person> iterator = persons.iterator();
        while (iterator.hasNext()) {
            System.out.println("First Name = " + iterator.next()
                  .getFirstName());
        }
    }
}

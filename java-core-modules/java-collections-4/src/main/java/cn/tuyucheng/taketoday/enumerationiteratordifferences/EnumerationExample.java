package cn.tuyucheng.taketoday.enumerationiteratordifferences;

import java.util.Enumeration;
import java.util.Vector;

import static cn.tuyucheng.taketoday.enumerationiteratordifferences.DataUtil.getPersons;

public class EnumerationExample {
    public static void main(String[] args) {

        Vector<Person> people = new Vector<>(getPersons());
        Enumeration<Person> enumeration = people.elements();
        while (enumeration.hasMoreElements()) {
            System.out.println("First Name = " + enumeration.nextElement()
                  .getFirstName());
        }
    }

}

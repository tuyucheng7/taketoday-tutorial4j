package cn.tuyucheng.taketoday.java.list;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.assertEquals;

public class WaysToIterateUnitTest {

    List<String> globalCountries = new ArrayList<String>();
    List<String> europeanCountries = Arrays.asList("Germany", "Panama", "Australia");

    @Test
    public void whenIteratingUsingForLoop_thenReturnThreeAsSizeOfList() {
        for (int i = 0; i < europeanCountries.size(); i++) {
            globalCountries.add(europeanCountries.get(i));
        }
        assertEquals(globalCountries.size(), 3);
        globalCountries.clear();
    }

    @Test
    public void whenIteratingUsingEnhancedForLoop_thenReturnThreeAsSizeOfList() {
        for (String country : europeanCountries) {
            globalCountries.add(country);
        }
        assertEquals(globalCountries.size(), 3);
        globalCountries.clear();
    }

    @Test
    public void whenIteratingUsingIterator_thenReturnThreeAsSizeOfList() {
        Iterator<String> countriesIterator = europeanCountries.iterator();
        while (countriesIterator.hasNext()) {
            globalCountries.add(countriesIterator.next());
        }

        assertEquals(globalCountries.size(), 3);
        globalCountries.clear();
    }

    @Test
    public void whenIteratingUsingListIterator_thenReturnThreeAsSizeOfList() {
        ListIterator<String> countriesIterator = europeanCountries.listIterator();
        while (countriesIterator.hasNext()) {
            globalCountries.add(countriesIterator.next());
        }

        assertEquals(globalCountries.size(), 3);
        globalCountries.clear();
    }

    @Test
    public void whenIteratingUsingForEach_thenReturnThreeAsSizeOfList() {
        europeanCountries.forEach(country -> globalCountries.add(country));
        assertEquals(globalCountries.size(), 3);
        globalCountries.clear();
    }

    @Test
    public void whenIteratingUsingStreamForEach_thenReturnThreeAsSizeOfList() {
        europeanCountries.stream().forEach((country) -> globalCountries.add(country));
        assertEquals(globalCountries.size(), 3);
        globalCountries.clear();
    }
}
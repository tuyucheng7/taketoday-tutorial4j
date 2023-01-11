package cn.tuyucheng.taketoday.boot.count.service;

import cn.tuyucheng.taketoday.boot.count.SpringBootCountApplication;
import cn.tuyucheng.taketoday.boot.count.data.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringBootCountApplication.class)
@DirtiesContext
@ExtendWith(SpringExtension.class)
@TestPropertySource("/embedded.properties")
class CountCarServiceIntegrationTest {
    @Autowired
    private CountCarService service;

    Car car1 = new Car("B-A");

    @BeforeEach
    void init() {
        service.insertCar(car1);
        service.insertCar(new Car("B-B"));
        service.insertCar(new Car("B-C"));
    }

    @Test
    void givenAllDocs_whenQueryAnnotationCount_thenCountEqualsSize() {
        List<Car> all = service.findCars();

        long count = service.getCountWithQueryAnnotation();

        assertEquals(count, all.size());
    }

    @Test
    void givenAllDocs_whenCrudRepositoryCount_thenCountEqualsSize() {
        List<Car> all = service.findCars();

        long count = service.getCountWithCrudRepository();

        assertEquals(count, all.size());
    }

    @Test
    void givenFilteredDocs_whenCriteriaCountByBrand_thenCountEqualsSize() {
        String filter = "B-A";
        long all = service.findCars()
                .stream()
                .filter(car -> car.getBrand().equals(filter))
                .count();

        long count = service.getCountBrandWithCriteria(filter);

        assertEquals(count, all);
    }

    @Test
    void givenQueryAnnotation_whenCountingByBrand_thenCountEqualsSize() {
        String filter = "B-A";
        long all = service.findCars()
                .stream()
                .filter(car -> car.getBrand().equals(filter))
                .count();

        long count = service.getCountBrandWithQueryAnnotation(filter);

        assertEquals(count, all);
    }

    @Test
    void givenFilteredDocs_whenQueryMethodCountByBrand_thenCountEqualsSize() {
        String filter = "B-A";
        long all = service.findCars()
                .stream()
                .filter(car -> car.getBrand().equals(filter))
                .count();

        long count = service.getCountBrandWithQueryMethod(filter);

        assertEquals(count, all);
    }

    @Test
    void givenFilteredDocs_whenExampleCount_thenCountEqualsSize() {
        long all = service.findCars()
                .stream()
                .filter(car -> car.getBrand().equals(car1.getBrand()))
                .count();

        long count = service.getCountWithExample(car1);

        assertEquals(count, all);
    }

    @Test
    void givenFilteredDocs_whenExampleCriteriaCount_thenCountEqualsSize() {
        long all = service.findCars()
                .stream()
                .filter(car -> car.getBrand().equals(car1.getBrand()))
                .count();

        long count = service.getCountWithExampleCriteria(car1);

        assertEquals(count, all);
    }
}
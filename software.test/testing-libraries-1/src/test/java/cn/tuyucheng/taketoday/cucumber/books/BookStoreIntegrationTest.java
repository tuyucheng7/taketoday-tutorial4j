package cn.tuyucheng.taketoday.cucumber.books;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/book-store.feature")
public class BookStoreIntegrationTest {

}


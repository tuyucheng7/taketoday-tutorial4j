package cn.tuyucheng.taketoday.cucumber.e2e.config;

import cn.tuyucheng.taketoday.cucumber.e2e.managers.FileReaderManager;
import cn.tuyucheng.taketoday.cucumber.e2e.model.Customer;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class JsonDataReader {

   private final String customerFilePath = FileReaderManager.getInstance().getConfigFileReader().getTestDataResourcePath() + "customer.json";

   private List<Customer> customerList;

   public JsonDataReader() {
      customerList = getCustomerData();
   }

   private List<Customer> getCustomerData() {
      Gson gson = new Gson();
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(customerFilePath))) {
         Customer[] customers = gson.fromJson(bufferedReader, Customer[].class);
         return List.of(customers);
      } catch (IOException e) {
         throw new RuntimeException("Json file not found at path : " + customerFilePath);
      }
   }

   public final Customer getCustomerByName(String customerName) {
      return customerList.stream()
            .filter(x -> x.getFirstName().equalsIgnoreCase(customerName))
            .findAny()
            .get();
   }
}
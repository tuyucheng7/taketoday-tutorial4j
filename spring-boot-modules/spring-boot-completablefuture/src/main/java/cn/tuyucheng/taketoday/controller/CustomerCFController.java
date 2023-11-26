package cn.tuyucheng.taketoday.controller;

import cn.tuyucheng.taketoday.dto.CustomerResponse;
import cn.tuyucheng.taketoday.dto.UpdateCustomerRequest;
import cn.tuyucheng.taketoday.service.CustomerCompletableFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/customers/completable-feature")
public class CustomerCFController {

   private final CustomerCompletableFeatureService customerCompletableFeatureService;

   @PutMapping("/{customerId}")
   @ResponseStatus(HttpStatus.ACCEPTED)
   public CompletableFuture<Void> replaceCustomerUsingCompletableFeature(@PathVariable Integer customerId, @RequestBody UpdateCustomerRequest request) {
      return customerCompletableFeatureService.replaceCustomer(customerId, request);
   }

   @PatchMapping("/{customerId}")
   @ResponseStatus(HttpStatus.ACCEPTED)
   public CompletableFuture<Void> updateCustomerUsingCompletableFeature(@PathVariable Integer customerId, @RequestBody UpdateCustomerRequest request) {
      return customerCompletableFeatureService.updateCustomer(customerId, request);
   }

   @GetMapping("/{customerId}")
   public CompletableFuture<CustomerResponse> getCustomerByIdUsingCompletableFeature(@PathVariable Integer customerId) {
      return customerCompletableFeatureService.getCustomerById(customerId);
   }

   @GetMapping("/all-of/{customerId}")
   public CompletableFuture<CustomerResponse> getCustomerByIdUsingCompletableFeatureUsingAllOf(@PathVariable Integer customerId) {
      return customerCompletableFeatureService.getCustomerByIdUsingAllOf(customerId);
   }
}
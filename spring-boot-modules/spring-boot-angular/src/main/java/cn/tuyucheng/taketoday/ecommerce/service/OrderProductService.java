package cn.tuyucheng.taketoday.ecommerce.service;

import cn.tuyucheng.taketoday.ecommerce.model.OrderProduct;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface OrderProductService {

   OrderProduct create(@NotNull(message = "The products for order cannot be null.") @Valid OrderProduct orderProduct);
}
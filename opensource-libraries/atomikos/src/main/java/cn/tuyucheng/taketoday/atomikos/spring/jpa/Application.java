package cn.tuyucheng.taketoday.atomikos.spring.jpa;

import cn.tuyucheng.taketoday.atomikos.spring.jpa.inventory.Inventory;
import cn.tuyucheng.taketoday.atomikos.spring.jpa.inventory.InventoryRepository;
import cn.tuyucheng.taketoday.atomikos.spring.jpa.order.Order;
import cn.tuyucheng.taketoday.atomikos.spring.jpa.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;

public class Application {

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Transactional(rollbackFor = Exception.class)
	public void placeOrder(String productId, int amount) throws Exception {

		String orderId = UUID.randomUUID()
			.toString();
		Inventory inventory = inventoryRepository.findOne(productId);
		inventory.setBalance(inventory.getBalance() - amount);
		inventoryRepository.save(inventory);
		Order order = new Order();
		order.setOrderId(orderId);
		order.setProductId(productId);
		order.setAmount(new Long(amount));
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Order>> violations = validator.validate(order);
		if (violations.size() > 0)
			throw new Exception("Invalid instance of an order.");
		orderRepository.save(order);

	}

}

package cn.tuyucheng.taketoday.ddd.order.doubledispatch;

import cn.tuyucheng.taketoday.ddd.order.OrderLine;
import cn.tuyucheng.taketoday.ddd.order.doubledispatch.visitor.OrderVisitor;
import cn.tuyucheng.taketoday.ddd.order.doubledispatch.visitor.Visitable;
import org.joda.money.Money;

import java.math.RoundingMode;
import java.util.List;

public class Order extends cn.tuyucheng.taketoday.ddd.order.Order implements Visitable<OrderVisitor> {
	public Order(List<OrderLine> orderLines) {
		super(orderLines);
	}

	public Money totalCost(SpecialDiscountPolicy discountPolicy) {
		return totalCost().multipliedBy(1 - applyDiscountPolicy(discountPolicy), RoundingMode.HALF_UP);
	}

	protected double applyDiscountPolicy(SpecialDiscountPolicy discountPolicy) {
		return discountPolicy.discount(this);
	}

	@Override
	public void accept(OrderVisitor visitor) {
		visitor.visit(this);
	}
}

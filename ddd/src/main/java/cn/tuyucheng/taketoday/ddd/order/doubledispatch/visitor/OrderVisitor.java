package cn.tuyucheng.taketoday.ddd.order.doubledispatch.visitor;

import cn.tuyucheng.taketoday.ddd.order.doubledispatch.Order;
import cn.tuyucheng.taketoday.ddd.order.doubledispatch.SpecialOrder;

public interface OrderVisitor {
	void visit(Order order);

	void visit(SpecialOrder order);
}

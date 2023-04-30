package cn.tuyucheng.taketoday.javaxval.enums;

import cn.tuyucheng.taketoday.javaxval.enums.constraints.CustomerTypeSubset;
import cn.tuyucheng.taketoday.javaxval.enums.demo.CustomerType;

public class InheritedCustomerTypeSubSetValidator extends EnumSubSetValidator<CustomerTypeSubset, CustomerType> {
	@Override
	public void initialize(CustomerTypeSubset constraint) {
		super.initialize(constraint.anyOf());
	}
}

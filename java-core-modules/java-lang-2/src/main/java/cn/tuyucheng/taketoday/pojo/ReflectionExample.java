package cn.tuyucheng.taketoday.pojo;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;


public class ReflectionExample {

	public static void main(String[] args) {

		System.out.println("Fields for EmployeePojo are:");
		for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(EmployeePojo.class)) {
			System.out.println(pd.getDisplayName());
		}

		System.out.println("Fields for EmployeeBean are:");
		for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(EmployeeBean.class)) {
			System.out.println(pd.getDisplayName());
		}

	}

}

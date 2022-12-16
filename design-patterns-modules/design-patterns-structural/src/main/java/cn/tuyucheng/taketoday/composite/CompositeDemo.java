package cn.tuyucheng.taketoday.composite;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Created by Tuyucheng
 */
@ExcludeFromJacocoGeneratedReport
public class CompositeDemo {

	public static void main(String[] args) {
		Department salesDepartment = new SalesDepartment(1, "Sales department");
		Department financialDepartment = new FinancialDepartment(2, "Financial department");

		HeadDepartment headDepartment = new HeadDepartment(3, "Head department");

		headDepartment.addDepartMent(salesDepartment);
		headDepartment.addDepartMent(financialDepartment);

		headDepartment.printDepartmentName();
	}
}
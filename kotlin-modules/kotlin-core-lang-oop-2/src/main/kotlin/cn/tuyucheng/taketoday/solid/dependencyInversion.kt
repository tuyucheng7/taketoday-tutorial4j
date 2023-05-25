package cn.tuyucheng.taketoday.solid

import java.util.*

class VeryComplexService(
      private val properties: Properties = Properties(),
      private val employeeRepository: EmployeeRepository = EmployeeRepository(createDataSource()),
      private val transformer: G = G { C() }
)
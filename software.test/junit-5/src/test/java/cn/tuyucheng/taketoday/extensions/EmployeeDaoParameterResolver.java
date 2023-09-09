package cn.tuyucheng.taketoday.extensions;

import cn.tuyucheng.taketoday.helpers.EmployeeJdbcDao;
import cn.tuyucheng.taketoday.helpers.JdbcConnectionUtil;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class EmployeeDaoParameterResolver implements ParameterResolver {

   @Override
   public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      return parameterContext.getParameter().getType().equals(EmployeeJdbcDao.class);
   }

   @Override
   public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      return new EmployeeJdbcDao(JdbcConnectionUtil.getConnection());
   }
}
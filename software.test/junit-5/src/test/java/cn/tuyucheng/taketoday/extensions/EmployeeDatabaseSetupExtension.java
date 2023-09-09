package cn.tuyucheng.taketoday.extensions;

import cn.tuyucheng.taketoday.helpers.EmployeeJdbcDao;
import cn.tuyucheng.taketoday.helpers.JdbcConnectionUtil;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class EmployeeDatabaseSetupExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
   private final Connection con;
   private final EmployeeJdbcDao employeeDao;
   private Savepoint savepoint;

   public EmployeeDatabaseSetupExtension() {
      con = JdbcConnectionUtil.getConnection();
      employeeDao = new EmployeeJdbcDao(con);
   }

   public EmployeeDatabaseSetupExtension(String jdbcUrl, String driver, String username, String password) {
      con = JdbcConnectionUtil.getConnection(jdbcUrl, driver, username, password);
      employeeDao = new EmployeeJdbcDao(con);
   }

   @Override
   public void afterAll(ExtensionContext context) throws SQLException {
      if (con != null)
         con.close();
   }

   @Override
   public void beforeAll(ExtensionContext context) throws SQLException {
      employeeDao.createTable();
   }

   @Override
   public void afterEach(ExtensionContext context) throws SQLException {
      con.rollback(savepoint);
   }

   @Override
   public void beforeEach(ExtensionContext context) throws SQLException {
      con.setAutoCommit(false);
      savepoint = con.setSavepoint("before");
   }
}
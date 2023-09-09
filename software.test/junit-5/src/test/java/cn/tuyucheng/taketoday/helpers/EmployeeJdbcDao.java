package cn.tuyucheng.taketoday.helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeJdbcDao {
   private final Connection con;

   public EmployeeJdbcDao(Connection con) {
      this.con = con;
   }

   public void createTable() throws SQLException {
      String createQuery = "CREATE TABLE IF NOT EXISTS employees(id long primary key, firstName varchar(50))";
      PreparedStatement stmt = con.prepareStatement(createQuery);

      stmt.execute();
   }

   public void add(Employee emp) throws SQLException {
      String insertQuery = "INSERT INTO employees(id, firstName) VALUES(?,?)";
      PreparedStatement stmt = con.prepareStatement(insertQuery);
      stmt.setLong(1, emp.getId());
      stmt.setString(2, emp.getFirstName());

      stmt.executeUpdate();
   }

   public List<Employee> findAll() throws SQLException {
      List<Employee> employees = new ArrayList<>();
      String query = "SELECT * FROM employees";
      PreparedStatement stmt = con.prepareStatement(query);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
         Employee emp = new Employee(rs.getLong("id"), rs.getString("firstName"));
         employees.add(emp);
      }

      return employees;
   }
}
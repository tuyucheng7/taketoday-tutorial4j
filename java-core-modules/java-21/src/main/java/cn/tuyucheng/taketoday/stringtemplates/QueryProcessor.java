package cn.tuyucheng.taketoday.stringtemplates;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public record QueryProcessor(Connection conn) implements StringTemplate.Processor<ResultSet, SQLException> {

   public ResultSet process(StringTemplate st) throws SQLException {
      String query = String.join("?", st.fragments());

      var ps = conn.prepareStatement(query);

      int index = 1;
      for (var value : st.values()) {
         switch (value) {
            case Integer i -> ps.setInt(index++, i);
            case Float f -> ps.setFloat(index++, f);
            case Double d -> ps.setDouble(index++, d);
            case Boolean b -> ps.setBoolean(index++, b);
            default -> ps.setString(index++, String.valueOf(value));
         }
      }

      return ps.executeQuery();
   }
}
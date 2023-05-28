package cn.tuyucheng.taketoday.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UserServlet", urlPatterns = "/user")
public class UserServlet extends HttpServlet {

   private static final long serialVersionUID = 2923732283720972121L;

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String firstName = request.getParameter("firstName");
      String lastName = request.getParameter("lastName");

      response.getWriter()
            .append("Full Name: " + firstName + " " + lastName);
   }

}

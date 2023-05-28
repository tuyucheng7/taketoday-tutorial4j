package cn.tuyucheng.taketoday.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UploadWelcomeServlet", urlPatterns = "/uploadwelcome")
public class UploadWelcomeServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      request.getRequestDispatcher("/upload.jsp").forward(request, response);
   }
}

package test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletTest_2_6 extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String pass = request.getParameter("password");
		System.out.println(name);

		PrintWriter pw = response.getWriter();

		if (name.trim().equals("ÌÀÄ·") && pass.trim().equals("admin")) {

			response.setHeader("refresh", "1;URL=index.html");
			response.setContentType("text/html; charset=utf-8");
			pw.println("<h6 align=\"center\"><font color=\"#FF1515\">µÇÂ½³É¹¦!</font></h6>");
			pw.close();

		} else {

			response.setHeader("refresh", "1;URL=login.html");
			response.setContentType("text/html; charset=utf-8");
			pw.println("<h6 align=\"center\"><font color=\"#FF1515\">µÇÂ½Ê§°Ü!</font></h6>");
			pw.flush();
			pw.close();

		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

}

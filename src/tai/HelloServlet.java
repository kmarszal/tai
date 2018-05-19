package tai;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private UserManager userManager;
	
	public HelloServlet() {
		try {
			userManager = new UserManager();
		} catch(ConnectionException e) {
			e.printStackTrace();
		}
	}
	
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("message", "The Message");
        userManager.printHello();
        getServletContext().getRequestDispatcher("/hello.jsp").forward(request, response);
    }
}

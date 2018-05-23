package tai;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private UserDAO userDAO;
	
	public HelloServlet() {
		try {
			userDAO = new UserDAO();
		} catch(ConnectionException e) {
			e.printStackTrace();
		}
	}
	
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User test = new User("a","b","c");
        userDAO.insertUser(test);
        boolean deleted = userDAO.deleteUser(test);
        if(deleted) {
        	request.setAttribute("message", "Flawless victory");
        } else {
        	request.setAttribute("message", "Something went wrong :'(");
        }
        getServletContext().getRequestDispatcher("/hello.jsp").forward(request, response);
    }
}

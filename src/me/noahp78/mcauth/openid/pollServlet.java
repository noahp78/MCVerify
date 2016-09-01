package me.noahp78.mcauth.openid;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.noahp78.mcauth.mc.McServerFacade;

/**
 * Servlet implementation class pollServlet
 */
@WebServlet("/poll")
public class pollServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public pollServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		if(request.getParameterMap().containsKey("token")){
			String token = request.getParameter("token");
			if(McServerFacade.authenticated_users.containsKey(token)){
				response.getWriter().append(McServerFacade.authenticated_users.get(token).username);
			}else{
				response.sendError(403);
			}
			
		}else{
			response.getWriter().append("INVALID REQUEST");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

package me.noahp78.mcauth.openid;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import me.noahp78.mcauth.PersistantData;
import me.noahp78.mcauth.mc.MCUserData;
import me.noahp78.mcauth.mc.McServerFacade;

/**
 * Servlet implementation class pollServlet
 */
@WebServlet("/poll")
public class pollServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public static final boolean DEBUG_MODE = false;
	
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
				//FAST LOGIN NEXT TIME
				if(OpenidLogin.getProfileTicket(request)!=null){
					PersistantData.get().data_keys.put(OpenidLogin.getProfileTicket(request), McServerFacade.authenticated_users.get(token));
					System.out.println("Upgraded " + OpenidLogin.getProfileTicket(request) + " with MCUserData");
					if(request.getParameterMap().containsKey("json")){
						response.getWriter().append(new Gson().toJson(McServerFacade.authenticated_users.get(token).username));
					}else{
						response.getWriter().append(McServerFacade.authenticated_users.get(token).username);
					}
				}
					
			}else if(DEBUG_MODE){
				MCUserData d = new MCUserData();
				d.username="test";
				d.uuid="test";
				McServerFacade.authenticated_users.put(token, d);
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

package me.noahp78.mcauth.openid;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.noahp78.mcauth.helper.ContextListener;
import me.noahp78.mcauth.mc.McServerFacade;

/**
 * Servlet implementation class OpenidLogin
 */
@WebServlet("/auth")
public class OpenidLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OpenidLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		if(request.getParameterMap().containsKey("redirect_uri") && request.getParameterMap().containsKey("client_id")
			&& request.getParameterMap().containsKey("scope")){
			String return_url = request.getParameter("redirect_uri");
			//Check if url is really a URL
			try {
				URL u = new URL(return_url); // this would check for the protocol
			
				URI uri = u.toURI();
			} catch (Exception e) {
				System.out.println("Got invalid request!");
				request.setAttribute("error", "Invalid Return Url, Please contact the website administrator that linked you here.");
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				request.getRequestDispatcher("/error.jsp").forward(request, response);
				//TODO a nice error page?
				return;
			} // does the extra checking required for validation of URI 
			
			
			
			
			//String token = request.getParameter("client_id");
			//String scope = request.getParameter("scope");
			
			int index = new Random().nextInt(ContextListener.words.size());
	        String prefix = ContextListener.words.get(index);
	        String hostname = "";
	        if(McServerFacade.hostname_token.containsKey((prefix+".verify.mcverify.ga"))){
	        	hostname = randomString(6) + ".verify.mcverify.ga";
	        }else{
	        	hostname =prefix+".verify.mcverify.ga";
				
	        }
			//String prefix = ContextListener.words.get((int) (Math.random() * ContextListener.words.size()));
			//prefix = new BigInteger(130, new SecureRandom()).toString(4);
			request.setAttribute("verify_ip", hostname);
			String authtoken = UUID.randomUUID().toString();
			McServerFacade.token_url_connection.put(authtoken, hostname);
			McServerFacade.hostname_token.put(hostname,authtoken);
			
			
			request.setAttribute("return_url", return_url+"?code=" + authtoken);
			request.setAttribute("poll_token", authtoken);
			request.getRequestDispatcher("/auth_step0.jsp").forward(request, response);
			
			
			
			
		}else{
			System.out.println("ERROR STARTING AUTH_FLOW. NOT ALL PARAMETERS PRESENT");
			request.setAttribute("error", "Not all parameters are present, Please contact the website administrator that linked you here.");
			
			// TODO Auto-generated catch block
			request.getRequestDispatcher("/error.jsp").forward(request, response);
			//TODO a nice error page?
			return;
		}
	}
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();

	String randomString( int len ){
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

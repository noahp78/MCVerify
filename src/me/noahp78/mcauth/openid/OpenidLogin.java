package me.noahp78.mcauth.openid;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.noahp78.mcauth.DonatorCustomization;
import me.noahp78.mcauth.PersistantData;
import me.noahp78.mcauth.helper.ContextListener;
import me.noahp78.mcauth.mc.MCUserData;
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

    public static String getProfileTicket(HttpServletRequest request){
    	if(request.getCookies()!=null){
		for(Cookie c : request.getCookies()){
			if(c.getName().equalsIgnoreCase("request_key")){
				return c.getValue();
			}
		}
    	}
    	return null;
    	
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//We require redirect_uri, client_id and scope to be prepared for a possible Full OpenID implementation
		if(request.getParameterMap().containsKey("redirect_uri") && request.getParameterMap().containsKey("client_id")
			&& request.getParameterMap().containsKey("scope")){
			String return_url = request.getParameter("redirect_uri");
			//Check if url is really a URL
			if(getProfileTicket(request)!=null){
				String ticket = getProfileTicket(request);
				//Check if we have a ticket that has Authentication Information and service doesn't force user auth.
				if(PersistantData.get().data_keys.get(ticket)!=null && !request.getParameterMap().containsKey("nofastlogin")){
					String hostname = PersistantData.get().data_keys.get(ticket);
					if(McServerFacade.authenticated_users.get(McServerFacade.hostname_token.get(hostname))!=null){
						MCUserData data = McServerFacade.authenticated_users.get(McServerFacade.hostname_token.get(hostname));
						if(data!=null){
							UUID u = UUID.randomUUID();
							McServerFacade.authenticated_users.put(u.toString(), data);
							response.sendRedirect(response.encodeRedirectUrl(return_url + "?code=" + u.toString() + "&FastLogin"));
							return;
						}
					}
				}	
			}
				String request_key = UUID.randomUUID().toString();
				Cookie c =new Cookie("request_key", request_key);
				if(pollServlet.DEBUG_MODE){
					c.setDomain("localhost");
				}else{
					c.setDomain(".mcverify.ga");
				}
				c.setPath("/");
				c.setVersion(1);
				c.setMaxAge(3600*12);
				response.addCookie(c);
			
			
			
			try {
				URL u = new URL(return_url); // this would check for the protocol
			
				URI uri = u.toURI();
			} catch (Exception e) {
				System.out.println("Got invalid request!");
				String error_msg =e.getMessage();
				System.out.println(error_msg);
				String error_name = e.getMessage().split(":")[0];
				request.setAttribute("error", "Invalid Return Url, Please contact the website administrator that linked you here.");
				request.setAttribute("error_detail", error_name);
				
				/* There is no reason to report this error since we handled it with the customer
				 * e.printStackTrace();
				 */
				request.getRequestDispatcher("/error.jsp").forward(request, response);
				
				return;
			} 
			String hostname_suffix = ".verify.mcverify.ga";
			if(request.getParameterMap().containsKey("donator")){
				String donator_code = request.getParameter("donator");
				if(DonatorCustomization.h.containsKey(donator_code)){
					DonatorCustomization.Donator d = DonatorCustomization.h.get(donator_code);
					if(!d.isUrlAllowed(return_url)){
						request.setAttribute("error", "Return path doesn't match Donator Specification");
						request.setAttribute("error_detail", "Return path: " + return_url.replaceAll("\\<.*?>","") + "Doesn't match any donator specification");
						request.getRequestDispatcher("/error.jsp").forward(request, response);
						return;
					}
					if(d.image!=null){
						request.setAttribute("image", d.image);
					}
					hostname_suffix = d.custom_suffix;
				}else{
					request.setAttribute("error", "Invalid Donator Key, Please contact the website administrator that linked you here.");
					request.setAttribute("error_detail", "Donator key was invalid. Or not yet deployed");
					
					/* There is no reason to report this error since we handled it with the customer
					 * e.printStackTrace();
					 */
					request.getRequestDispatcher("/error.jsp").forward(request, response);
					return;
				}
				
				
				
				
				
			}
			
			
			int index = new Random().nextInt(ContextListener.words.size());
	        String prefix = ContextListener.words.get(index);
	        String hostname = "";
	        if(McServerFacade.hostname_token.containsKey((prefix+hostname_suffix))){
	        	hostname = randomString(6) + hostname_suffix;
	        }else{
	        	hostname =prefix+hostname_suffix;
				
	        }
	        PersistantData.get().data_keys.put(request_key, hostname);
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

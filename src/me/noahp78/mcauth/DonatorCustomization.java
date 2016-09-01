package me.noahp78.mcauth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.noahp78.mcauth.helper.ContextListener;

public class DonatorCustomization {

	public static void Load(){
		 try{
			 Gson g =new Gson();
		    	InputStream stream = (DonatorCustomization.class.getResourceAsStream("donators.json"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String fulltext = "";
			String line;
			while ((line = reader.readLine()) != null) {
				fulltext = fulltext+line;
			}
			Type type = new TypeToken<HashMap<String, Donator>>(){}.getType();
			h = g.fromJson(fulltext,type);
			System.out.println(g.toJson(h));
		    	
		 }catch(Exception e){
				e.printStackTrace();
			}
	}
	public static HashMap<String, Donator> h = new HashMap<String, Donator>();
	
	
	public static class Donator{
		
		public Donator(String custom_suffix, String allowed_return_urls, String image){
			this.custom_suffix=custom_suffix;
			this.allowed_return_urls=allowed_return_urls;
			this.image =image;
		}
		public String image;
		public boolean isUrlAllowed(String url){
			String[] allowed_urls= this.allowed_return_urls.split(",");
			for(String s : allowed_urls){
				if(url.equalsIgnoreCase(s)){
					return true;
				}
			}
			return false;
		}
		public String allowed_return_urls;
		public String custom_suffix;
	}
}

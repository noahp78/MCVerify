package me.noahp78.mcauth;

import java.util.HashMap;

import me.noahp78.mcauth.mc.MCUserData;

public class PersistantData {

	private static PersistantData i;
	public static PersistantData get(){
		if(i!=null){
			return i;
		}else{
			i = new PersistantData();
			i.data_keys = new HashMap<>();
			return i;
		}
	}
	/**
	 * Return the hostname used to auth
	 */
	public HashMap<String, String> data_keys = new HashMap<String,String>();
	
	
}

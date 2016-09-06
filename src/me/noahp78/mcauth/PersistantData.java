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
	public HashMap<String, MCUserData> data_keys = new HashMap<String,MCUserData>();
	
	
}

package me.noahp78.mcauth.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import me.noahp78.mcauth.DonatorCustomization;
import me.noahp78.mcauth.mc.McServerFacade;


@WebListener
public class ContextListener implements javax.servlet.ServletContextListener {
	public static ArrayList<String> words= new ArrayList<>();
	
	public void contextInitialized(ServletContextEvent sce) {
		Scanner scanner;
		try {
			InputStream stream = (ContextListener.class.getResourceAsStream("words.txt"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				String word = line;
				words.add(word);
			}
			System.out.println("Read " + words.size() + " words");
			DonatorCustomization.Load();
		}catch(Exception e){
			e.printStackTrace();
		}
		McServerFacade.startServer();
		
		
		
	
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

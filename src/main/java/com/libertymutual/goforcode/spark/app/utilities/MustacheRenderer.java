package com.libertymutual.goforcode.spark.app.utilities;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class MustacheRenderer {

	private static final MustacheRenderer instance = new MustacheRenderer("templates");
	private DefaultMustacheFactory factory;
	
	//private contructor - only the class itself can call the constructor
	private MustacheRenderer(String folderName) {
		factory = new DefaultMustacheFactory(folderName);
		
	}
	
	//singleton pattern
	public static MustacheRenderer getInstance() {
		return instance; 
	}
	
	public String render(String templatePath, Map<String, Object> model) {
		Mustache mustache = factory.compile(templatePath);  
    	StringWriter writer = new StringWriter();
    	mustache.execute(writer, model); //takes anything in "home/default file", executed it and wrote it using writer
    	return writer.toString();
	}
	
}

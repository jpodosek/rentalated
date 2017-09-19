package com.libertymutual.goforcode.spark.app.utilities;

import java.io.Closeable;

import org.javalite.activejdbc.Base;

public class AutoClosableDb implements Closeable, AutoCloseable {

	public AutoClosableDb() {
			Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/rental", "rental", "rental"); //get default connection to database
	}
	@Override
	public void close(){
		Base.close();
		
	}

}

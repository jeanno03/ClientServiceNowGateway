package mysingleton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import myconstants.MyConstant;

public enum LazySingleton {
	
	INSTANCE;
	
	private LazySingleton() 
	{
		getFileHandler(MyConstant.LOGGER);
		loadPropertiesFile();
	}
	
	public static LazySingleton getInstance() {
		return INSTANCE;
	}
	
	//Démarrage des logs
	private void getFileHandler(Logger logger) {

		boolean append = true;
		Date day = new Date();
		SimpleDateFormat formater = null;
		formater = new SimpleDateFormat("ddMMyy");

		try {  

			// This block configure the logger with handler and formatter  
			FileHandler fh = new FileHandler(MyConstant.PATH_LOG+""+formater.format(day)+"-gateway-api.log", append);  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

		} catch (SecurityException ex) {  
			ex.printStackTrace();  
		} catch (IOException ex) {  
			ex.printStackTrace();  
		}  

	}
	
	private void loadPropertiesFile() {

		FileInputStream propFile = null;
		
		try {
			
			propFile = new FileInputStream(MyConstant.PATH_FILE_INPUT_STREAM);
			MyConstant.PROP.load(propFile);

		} catch (FileNotFoundException ex) {

			ex.printStackTrace();
			
		} catch (IOException ex) {
			
			MyConstant.LOGGER.info(ex.getMessage());	
			
		}finally {
			
			if(null!=propFile) {
				try {
					propFile.close();
				}catch(Exception ex) {
					
					MyConstant.LOGGER.info(ex.getMessage());
					
				}
			}
		}


	}
	

}

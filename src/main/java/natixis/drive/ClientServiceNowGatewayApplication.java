package natixis.drive;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import drive.natixis.entities.DataParent;
import natixis.drive.services.MyService;
import natixis.drive.services.MyServiceInterface;

@Configuration
@ComponentScan(value = "natixis.drive.services")
@SpringBootApplication
public class ClientServiceNowGatewayApplication {

	//normalement avec Spring pas besoin d'instancier cet objet
	//A voir plus tard
	@Autowired
	private static MyServiceInterface myServiceInterface = new MyService();
	
	private static final Logger logger = Logger.getLogger("application log");

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ClientServiceNowGatewayApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
		app.run(args);

//		Logger logger = Logger.getLogger("application log");  
		
		myServiceInterface.getFileHandler(logger);  

		logger.info("**********Application start ***********"); 
		logger.info("**********Method : getDataParentIca() ***********"); 
		DataParent dataParent = myServiceInterface.getDataParentIca();
		logger.info("**********Method : writeCSVFile(dataParent) ***********"); 	

		myServiceInterface.writeCSVFile(dataParent);
		
		logger.info("**********Application stop ***********"); 
	}

}


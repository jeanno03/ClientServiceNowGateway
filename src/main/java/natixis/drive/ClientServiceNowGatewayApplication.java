package natixis.drive;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.http.HttpHost;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import natixis.drive.services.MyService;
import natixis.drive.services.MyServiceInterface;
import natixis.drive.entities.DataParent;
import natixis.drive.services.HttpClientService;
import natixis.drive.services.HttpClientServiceInterface;
import natixis.drive.services.MyConstante;

@Configuration
@ComponentScan(value = "natixis.drive.services")
@SpringBootApplication
public class ClientServiceNowGatewayApplication {

	
	
	//normalement avec Spring pas besoin d'instancier cet objet
	//A voir plus tard
	@Autowired
	private static MyServiceInterface myServiceInterface = new MyService();
	
	@Autowired
	private static HttpClientServiceInterface httpClientServiceInterface = new HttpClientService();

	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(ClientServiceNowGatewayApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
		ApplicationContext context = app.run(args);
		
		//charge le fichier de paramétrage
		myServiceInterface.loadPropertiesFile();
		//démarrer les logs
		myServiceInterface.getFileHandler(MyConstante.LOGGER);  
		
		MyConstante.LOGGER.info("**********Application start ***********"); 
		
		
		//ByPass le certificat
		httpClientServiceInterface.passByCertificat();

		
//		not working
		//new test
		Properties props = System.getProperties();
		props.put("https.proxyHost", "proxybusiness.intranet");
		props.put("https.proxyPort", 3125);
		

		



		MyConstante.LOGGER.info("**********Method : getDataParentIca() ***********"); 
		DataParent dataParent = myServiceInterface.getDataParentIca();
		MyConstante.LOGGER.info("**********Method : writeCSVFile(dataParent) ***********"); 	

		myServiceInterface.writeCSVFile(dataParent);
		
		MyConstante.LOGGER.info("**********Application stop ***********"); 
		
		((ConfigurableApplicationContext)context).close();
	}

}


package natixis.drive;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import myconstants.MyConstant;
import mysingleton.LazySingleton;
import natixis.drive.services.MyService;
import natixis.drive.services.MyServiceInterface;
import natixis.drive.entities.Result;
import natixis.drive.services.HttpClientService;
import natixis.drive.services.HttpClientServiceInterface;

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

	public static void main(String[] args) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
		
		SpringApplication app = new SpringApplication(ClientServiceNowGatewayApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
		ApplicationContext context = app.run(args);
		
		LazySingleton singleton = LazySingleton.getInstance();
		
		MyConstant.LOGGER.info("**********Application start ***********"); 
		
		String urlIca = MyConstant.getPropertyParameter("urlIca");
		
		myServiceInterface.getProxyConfiguration();

		MyConstant.LOGGER.info("**********Method : getSnowToObject() ***********"); 
		Object objectIca = myServiceInterface.getSnowToObject(urlIca);
		
		MyConstant.LOGGER.info("**********Method : getObjectToIcaResultsTab() ***********"); 	
		Result [] icaResults = myServiceInterface.getObjectToIcaResultsTab(objectIca);
		
		MyConstant.LOGGER.info("**********Method : writeIcaCSVFile() ***********"); 
		myServiceInterface.writeIcaCSVFile(icaResults);

		MyConstant.LOGGER.info("**********Application stop ***********"); 
		
		((ConfigurableApplicationContext)context).close();
	}

}


package natixis.drive;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.http.HttpHost;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import myconstants.MyConstant;
import mysingleton.LazySingleton;
import natixis.drive.services.MyService;
import natixis.drive.services.MyServiceInterface;
import natixis.drive.entities.Data;
import natixis.drive.entities.DataParent;
import natixis.drive.entities.Result;
import natixis.drive.entities.ResultList;
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

		myServiceInterface.getProxyConfiguration();
		
		MyConstant.LOGGER.info("**********Method : getSnowToObject() ***********"); 

		String urlIca = MyConstant.getPropertyParameter("urlIca");
		
		//it works
		Object objectIca = myServiceInterface.getSnowToObject(urlIca);
		
		Result [] icaResults = myServiceInterface.getObjectToIcaResultsTab(objectIca);

		myServiceInterface.writeIcaCSVFile(icaResults);
		
		
		//it works
//		MyConstant.LOGGER.info("objectIca : " + objectIca); 
		
//		MyConstant.LOGGER.info("**********Method : getStringFromSnow() ***********"); 

		//it works
//		String strIca = httpClientServiceInterface.getStringFromSnow(urlIca);

		
		
		//it works
//		MyConstant.LOGGER.info("strIca : " + strIca); 	
		
		//not working
//		myServiceInterface.getSnowWithJackson(strIca);
		
		//not working
//		JsonNode root = myServiceInterface.getSnowToJsonNode(urlIca);

		MyConstant.LOGGER.info("*********************"); 	
		

		

		
		MyConstant.LOGGER.info("**********Application stop ***********"); 
		
		((ConfigurableApplicationContext)context).close();
	}

}


package natixis.drive.services;

import java.util.Properties;
import java.util.logging.Logger;

import natixis.drive.ClientServiceNowGatewayApplication;

public interface MyConstante {
	
	public static final Logger LOGGER = Logger.getLogger( ClientServiceNowGatewayApplication.class.getName() );
	public static final Properties PROP = new Properties();
	public  static String URL = "https://blabla";
		

	public static String getPropertyParameter(String para) {
		String str = MyConstante.PROP.getProperty(para);
		return str;
	}
	
}

package myconstants;

import java.util.Properties;
import java.util.logging.Logger;

import natixis.drive.ClientServiceNowGatewayApplication;

public interface MyConstant {
	
	public static final Logger LOGGER = Logger.getLogger( ClientServiceNowGatewayApplication.class.getName() );
	public static final String PATH_LOG = "C:/Files/";
	public static final String PATH_FILE_INPUT_STREAM = "C:/Files/FileInputStream.properties";
	public static final Properties PROP = new Properties();

	public static String getPropertyParameter(String para) {
		String str = MyConstant.PROP.getProperty(para);
		return str;
	}
	
}

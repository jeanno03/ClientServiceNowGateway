package natixis.drive.services;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import natixis.drive.entities.Data;
import natixis.drive.entities.DataParent;
import natixis.drive.entities.Result;
import natixis.drive.entities.ResultList;

public interface MyServiceInterface {
	
//	public void loadPropertiesFile();
//	public void getFileHandler(Logger logger) ;
	public void getProxyConfiguration() ;
//	public DataParent getDataParentIca();
	public Object getSnowToObject(String url);
	public void getSnowWithJackson(String url);
	public JsonNode getSnowToJsonNode(String url) ;
	public Result[] getObjectToIcaResultsTab (Object objectIca) ;
//	public Result[] getDataParentIca3();
//	public Data getDataParentIca4() ;
//	public DataParent getDataParentIca5();
//	public ResultList getDataParentIca7();
//	public JSONObject getDataParentIca8();
//	public Object[] getDataParentIca9();
	public void writeIcaCSVFile(Result[] icaResults);

}

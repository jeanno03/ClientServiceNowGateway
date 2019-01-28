package natixis.drive.services;

import natixis.drive.entities.Result;

public interface MyServiceInterface {
	
	public void getProxyConfiguration() ;
	public Object getSnowToObject(String url);
	public Result[] getObjectToIcaResultsTab (Object objectIca) ;
	public void writeIcaCSVFile(Result[] icaResults);

}

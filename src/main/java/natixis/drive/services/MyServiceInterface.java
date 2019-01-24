package natixis.drive.services;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

import natixis.drive.entities.DataParent;

public interface MyServiceInterface {

	public void getFileHandler(Logger logger) ;
	public DataParent getDataParentIca();
	public void writeCSVFile(DataParent dataParent);

}

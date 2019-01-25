package natixis.drive.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import natixis.drive.entities.DataParent;
import natixis.drive.entities.Result;
import natixis.drive.services.MyConstante;

@Service
public class MyService implements MyServiceInterface{

	private static FileInputStream propFile;

	public void loadPropertiesFile() {

		//		prop = new Properties();
		try {
			propFile = new FileInputStream("C:/Files/FileInputStream.properties");
			MyConstante.PROP.load(propFile);

		} catch (FileNotFoundException ex) {

			ex.printStackTrace();
		} catch (IOException ex) {

			ex.printStackTrace();
			
		}finally {
			
			if(null!=propFile) {
				try {
					propFile.close();
				}catch(Exception ex) {
					ex.printStackTrace();	
				}
			}
		}


	}

	@Override
	public void getFileHandler(Logger logger) {

		String logs = MyConstante.getPropertyParameter("logs");
		boolean append = true;
		Date day = new Date();
		SimpleDateFormat formater = null;
		formater = new SimpleDateFormat("ddMMyy");

		try {  

			// This block configure the logger with handler and formatter  
			FileHandler fh = new FileHandler(logs+""+formater.format(day)+"-gateway-api.log", append);  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

		} catch (SecurityException ex) {  
			ex.printStackTrace();  
		} catch (IOException ex) {  
			ex.printStackTrace();  
		}  

	}

	@Override
	public DataParent getDataParentIca() {
		//test
		//		HttpHost host = new HttpHost("proxybusiness.intranet", 3125, "https");

		RestTemplate restTemplate = new RestTemplate();
		String url = MyConstante.getPropertyParameter("url");
		String username = MyConstante.getPropertyParameter("username");
		String password = MyConstante.getPropertyParameter("password");

		MyConstante.LOGGER.info("url : " + url);
		
		//test commenté
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.setBasicAuth(username, password);		
//		HttpEntity<String> entity = new HttpEntity<String> ("parameter", headers);
		
		//test non commenté
		String plainCreds = username+":"+password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		MyConstante.LOGGER.info("headers value : " + "Basic " + base64Creds);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		try {
			
			//test commenté
//			ResponseEntity<DataParent>respEntity = restTemplate.exchange(MyConstante.URL, HttpMethod.GET, entity, DataParent.class);
//			DataParent result = respEntity.getBody();
			
			//test non commenté
		ResponseEntity<DataParent> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, DataParent.class);
		DataParent result = respEntity.getBody();



			return result;
		}catch(Exception ex) {
			MyConstante.LOGGER.info(ex.getMessage());
		}
		return null;
	}


	@Override
	public DataParent getDataParentIca2() {
		//test
		//		HttpHost host = new HttpHost("proxybusiness.intranet", 3125, "https");

		RestTemplate restTemplate = new RestTemplate();
		String url = MyConstante.getPropertyParameter("url");
		String username = MyConstante.getPropertyParameter("username");
		String password = MyConstante.getPropertyParameter("password");

		MyConstante.LOGGER.info("url : " + url);
		
		//test commenté
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.setBasicAuth(username, password);		
//		HttpEntity<String> entity = new HttpEntity<String> ("parameter", headers);
		
		//test non commenté
		String plainCreds = username+":"+password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		MyConstante.LOGGER.info("headers value : " + "Basic " + base64Creds);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		try {
			
			//test commenté
//			ResponseEntity<DataParent>respEntity = restTemplate.exchange(MyConstante.URL, HttpMethod.GET, entity, DataParent.class);
//			DataParent result = respEntity.getBody();
			
			//test non commenté
		ResponseEntity<DataParent> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, DataParent.class);
		DataParent result = respEntity.getBody();



			return result;
		}catch(Exception ex) {
			MyConstante.LOGGER.info(ex.getMessage());
		}
		return null;
	}


	
	@Override
	public void writeCSVFile(DataParent dataParent) {

		String destination = MyConstante.getPropertyParameter("destination");

		File outputFile = new File(destination+"ica.csv");
		try {

			natixis.drive.entities.Data data = dataParent.getData();
			Result[] results = data.getResult();

			ICsvBeanWriter beanWriter = null;
			CellProcessor[] processors = new CellProcessor[] {
					new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),
					new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),
					new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),
					new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),
					new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),
					new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),new NotNull(),
			};

			try {
				beanWriter = new CsvBeanWriter(new FileWriter(outputFile), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
				String[] header = {
						"parent", "u_internal_group_watch_list", "u_mtx_financial_impact","u_internal_watch_list","sys_updated_on","u_origin",
						"number", "u_outage", "u_business_service","u_security_incident","sys_updated_by","opened_by",
						"sys_created_on", "u_steering_commitee_count", "u_mtx_user_impacted_perimeter","state","u_operational_risk_reference","sys_created_by",
						"u_root_cause_description", "u_completed_at", "closed_at","cmdb_ci","impact","active",
						"u_impacted_customers", "u_mtx_outage_downtime", "u_cancel_reason","u_comm_linked_count","u_resolution_action_summary","opened_at",
						"u_problem", "u_start_date", "u_outage_type","u_mtx_brand_image_impact","work_notes","u_action_plan_summary",
						"short_description", "assignment_group", "u_completed_by","description","u_calculated_impact","u_incident_avere_child_count",
						"u_source", "closed_by", "sys_id","u_impacted_business_ia","u_end_date","u_opening_group",
						"assigned_to", "u_external_reference", "u_quality_engineers_rpt","u_mtx_service_label","u_problem_linked_count","u_original_change",
						"u_environment", "u_organisational_units_rpt", "u_origin_detailled","u_service_linked_count","u_mtx_service_pca","u_impacted_business_ia_rpt"
				};
				beanWriter.writeHeader(header);

				for (Result r : results) {
					beanWriter.write(r, header, processors);
				}

			} catch (IOException ex) {
				MyConstante.LOGGER.info(ex.getMessage());
				ex.printStackTrace();
			} finally {
				if (beanWriter != null) {
					try {
						beanWriter.close();
					} catch (IOException ex) {
						MyConstante.LOGGER.info(ex.getMessage());
					}
				}
			}

		}catch(Exception ex) {
			MyConstante.LOGGER.info("writeCSVFile para : " + ex.getMessage());
		}
	}




}

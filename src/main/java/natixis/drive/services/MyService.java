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

import drive.natixis.entities.DataParent;
import drive.natixis.entities.Result;
import drive.natixis.entities.Data;

@Service
public class MyService implements MyServiceInterface{

	private static Properties prop;
	private static FileInputStream propFile;




	private static final void loadPropertiesFile() {
		prop = new Properties();
		try {
			propFile = new FileInputStream("C:/Files/FileInputStream.properties");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} 
		try {
			prop.load(propFile);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private String getPropertyParameter(String para) {
		loadPropertiesFile();
		String str = prop.getProperty(para);
		return str;
	}


	@Override
	public void getFileHandler(Logger logger) {

		String logs = getPropertyParameter("logs");
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

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  

	}

	@Override
	public DataParent getDataParentIca() {
		RestTemplate restTemplate = new RestTemplate();
		String url = getPropertyParameter("url");
		String username = getPropertyParameter("username");
		String password = getPropertyParameter("password");

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(username, password);		
		HttpEntity<String> entity = new HttpEntity<String> ("parameter", headers);
		try {
			ResponseEntity<DataParent>respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, DataParent.class);
			DataParent result = respEntity.getBody();

			return result;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public void writeCSVFile(DataParent dataParent) {
		String destination = getPropertyParameter("destination");

		File outputFile = new File(destination+"ica.csv");
		try {

			drive.natixis.entities.Data data = dataParent.getData();
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
				ex.printStackTrace();
			} finally {
				if (beanWriter != null) {
					try {
						beanWriter.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}

		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}

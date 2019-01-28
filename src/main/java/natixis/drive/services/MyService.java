package natixis.drive.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import myconstants.MyConstant;
import natixis.drive.entities.Data;
import natixis.drive.entities.DataParent;
import natixis.drive.entities.Result;
import natixis.drive.entities.ResultList;

@Service
public class MyService implements MyServiceInterface{

	private static FileInputStream propFile;

	@Override
	public void getProxyConfiguration() {

		Properties props = System.getProperties();
		props.put("https.proxyHost", "proxybusiness.intranet");
		props.put("https.proxyPort", 3125);

	}

	//	@Override
	//	public DataParent getDataParentIca() {
	//
	//		RestTemplate restTemplate = new RestTemplate();
	//		String url = MyConstant.getPropertyParameter("url");
	//		String username = MyConstant.getPropertyParameter("username");
	//		String password = MyConstant.getPropertyParameter("password");
	//
	//		MyConstant.LOGGER.info("url : " + url);
	//
	//		String plainCreds = username+":"+password;
	//		byte[] plainCredsBytes = plainCreds.getBytes();
	//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	//		String base64Creds = new String(base64CredsBytes);
	//
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.add("Authorization", "Basic " + base64Creds);
	//		MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
	//		HttpEntity<String> entity = new HttpEntity<String>(headers);
	//		
	//		try {
	//
	//		ResponseEntity<DataParent> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, DataParent.class);
	//		DataParent result = respEntity.getBody();
	//
	//			return result;
	//		}catch(Exception ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}
	//		return null;
	//	}

	@Override
	public Object getSnowToObject(String url) {

		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		try {
			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
					.loadTrustMaterial(null, acceptingTrustStrategy)
					.build();

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLSocketFactory(csf)
					.build();

			HttpComponentsClientHttpRequestFactory requestFactory =
					new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);
			RestTemplate restTemplate = new RestTemplate(requestFactory);

			String username = MyConstant.getPropertyParameter("username");
			String password = MyConstant.getPropertyParameter("password");

			MyConstant.LOGGER.info("url : " + url);

			String plainCreds = username+":"+password;
			byte[] plainCredsBytes = plainCreds.getBytes();
			byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
			String base64Creds = new String(base64CredsBytes);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Basic " + base64Creds);
			MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
			HttpEntity<String> entity = new HttpEntity<String>(headers);

			ResponseEntity<Object> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
			Object result = respEntity.getBody();

			return result;
		}catch(KeyManagementException ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}catch(NoSuchAlgorithmException ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}catch(KeyStoreException ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}catch(Exception ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}

		return null;
	}

	@Override
	public Result[] getObjectToIcaResultsTab (Object objectIca) {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.convertValue(objectIca, JsonNode.class);

		//un noeud
		JsonNode dataNode = root.path("Data");


		// un autre noeud
		JsonNode resultNode = dataNode.path("result");
		if (resultNode.isArray()) {
			//			it works
			//			MyConstant.LOGGER.info("resultNode.isArray() is true ");
			//			MyConstant.LOGGER.info("resultNode.size() : " + resultNode.size());
		}
		else {
			MyConstant.LOGGER.info("resultNode.isArray() is false ");
		}

		Result[] results = new Result[resultNode.size()];

		int i=0;

		for(JsonNode node : resultNode) {

			String parent = node.path("parent").asText();
			String u_internal_group_watch_list = node.path("u_internal_group_watch_list").asText();
			String u_mtx_financial_impact = node.path("u_mtx_financial_impact").asText();
			String u_internal_watch_list = node.path("u_internal_watch_list").asText();
			String sys_updated_on = node.path("sys_updated_on").asText();
			String u_origin = node.path("u_origin").asText();
			String number = node.path("number").asText();
			String u_outage = node.path("u_outage").asText();
			String u_business_service = node.path("u_business_service").asText();
			String u_security_incident = node.path("u_security_incident").asText();
			String sys_updated_by = node.path("sys_updated_by").asText();
			String opened_by = node.path("opened_by").asText();
			String sys_created_on = node.path("sys_created_on").asText();
			String u_steering_commitee_count = node.path("u_steering_commitee_count").asText();
			String u_mtx_user_impacted_perimeter = node.path("u_mtx_user_impacted_perimeter").asText();
			String state = node.path("state").asText();
			String u_operational_risk_reference = node.path("u_operational_risk_reference").asText();
			String sys_created_by = node.path("sys_created_by").asText();
			String u_root_cause_description = node.path("u_root_cause_description").asText();
			String u_completed_at = node.path("u_completed_at").asText();
			String closed_at = node.path("closed_at").asText();
			String cmdb_ci = node.path("cmdb_ci").asText();
			String impact = node.path("impact").asText();
			String active = node.path("active").asText();
			String u_impacted_customers = node.path("u_impacted_customers").asText();
			String u_mtx_outage_downtime = node.path("u_mtx_outage_downtime").asText();
			String u_cancel_reason = node.path("u_cancel_reason").asText();
			String u_comm_linked_count = node.path("u_comm_linked_count").asText();
			String u_resolution_action_summary = node.path("u_resolution_action_summary").asText();
			String opened_at = node.path("opened_at").asText();
			String u_problem = node.path("u_problem").asText();
			String u_start_date = node.path("u_start_date").asText();
			String u_outage_type = node.path("u_outage_type").asText();
			String u_mtx_brand_image_impact = node.path("u_mtx_brand_image_impact").asText();
			String work_notes = node.path("work_notes").asText();
			String u_action_plan_summary = node.path("u_action_plan_summary").asText();
			String short_description = node.path("short_description").asText();
			String assignment_group = node.path("assignment_group").asText();
			String u_completed_by = node.path("u_completed_by").asText();
			String description = node.path("description").asText();
			String u_calculated_impact = node.path("u_calculated_impact").asText();
			String u_incident_avere_child_count = node.path("u_incident_avere_child_count").asText();
			String u_source = node.path("u_source").asText();
			String closed_by = node.path("closed_by").asText();
			String sys_id = node.path("sys_id").asText();
			String u_impacted_business_ia = node.path("u_impacted_business_ia").asText();
			String u_end_date = node.path("u_end_date").asText();
			String u_opening_group = node.path("u_opening_group").asText();
			String assigned_to = node.path("assigned_to").asText();
			String u_external_reference = node.path("u_external_reference").asText();
			String u_quality_engineers_rpt = node.path("u_quality_engineers_rpt").asText();
			String u_mtx_service_label = node.path("u_mtx_service_label").asText();
			String u_problem_linked_count = node.path("u_problem_linked_count").asText();
			String u_original_change = node.path("u_original_change").asText();
			String u_environment = node.path("u_environment").asText();
			String u_organisational_units_rpt = node.path("u_organisational_units_rpt").asText();
			String u_origin_detailled = node.path("u_origin_detailled").asText();
			String u_service_linked_count = node.path("u_service_linked_count").asText();
			String u_mtx_service_pca = node.path("u_mtx_service_pca").asText();
			String u_impacted_business_ia_rpt = node.path("u_impacted_business_ia_rpt").asText();

			Result result = new Result();
			result.setParent(parent);
			result.setU_internal_group_watch_list(u_internal_group_watch_list);
			result.setU_mtx_financial_impact(u_mtx_financial_impact);
			result.setU_internal_watch_list(u_internal_watch_list);
			result.setSys_updated_on(sys_updated_on);
			result.setU_origin(u_origin);
			result.setNumber(number);
			result.setU_outage(u_outage);
			result.setU_business_service(u_business_service);
			result.setU_security_incident(u_security_incident);
			result.setSys_updated_by(sys_updated_by);
			result.setOpened_by(opened_by);
			result.setSys_created_on(sys_created_on);
			result.setU_steering_commitee_count(u_steering_commitee_count);
			result.setU_mtx_user_impacted_perimeter(u_mtx_user_impacted_perimeter);
			result.setState(state);
			result.setU_operational_risk_reference(u_operational_risk_reference);
			result.setSys_created_by(sys_created_by);
			result.setU_root_cause_description(u_root_cause_description);
			result.setU_completed_at(u_completed_at);
			result.setClosed_at(closed_at);
			result.setCmdb_ci(cmdb_ci);
			result.setImpact(impact);
			result.setActive(active);
			result.setU_impacted_customers(u_impacted_customers);
			result.setU_mtx_outage_downtime(u_mtx_outage_downtime);
			result.setU_cancel_reason(u_cancel_reason);
			result.setU_comm_linked_count(u_comm_linked_count);
			result.setU_resolution_action_summary(u_resolution_action_summary);
			result.setOpened_at(opened_at);
			result.setU_problem(u_problem);
			result.setU_start_date(u_start_date);
			result.setU_outage_type(u_outage_type);
			result.setU_mtx_brand_image_impact(u_mtx_brand_image_impact);
			result.setWork_notes(work_notes);
			result.setU_action_plan_summary(u_action_plan_summary);
			result.setShort_description(short_description);
			result.setAssignment_group(assignment_group);
			result.setU_completed_by(u_completed_by);
			result.setDescription(description);
			result.setU_calculated_impact(u_calculated_impact);
			result.setU_incident_avere_child_count(u_incident_avere_child_count);
			result.setU_source(u_source);
			result.setClosed_by(closed_by);
			result.setSys_id(sys_id);
			result.setU_impacted_business_ia(u_impacted_business_ia);
			result.setU_end_date(u_end_date);
			result.setU_opening_group(u_opening_group);
			result.setAssigned_to(assigned_to);
			result.setU_external_reference(u_external_reference);
			result.setU_quality_engineers_rpt(u_quality_engineers_rpt);
			result.setU_mtx_service_label(u_mtx_service_label);
			result.setU_problem_linked_count(u_problem_linked_count);
			result.setU_original_change(u_original_change);
			result.setU_environment(u_environment);
			result.setU_organisational_units_rpt(u_organisational_units_rpt);
			result.setU_origin_detailled(u_origin_detailled);
			result.setU_service_linked_count(u_service_linked_count);
			result.setU_mtx_service_pca(u_mtx_service_pca);
			result.setU_impacted_business_ia_rpt(u_impacted_business_ia_rpt);

			results[i]=result;
			i=i+1;	

		}

		return results;

	}

	//not working
	@Override
	public JsonNode getSnowToJsonNode(String url) {

		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		try {
			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
					.loadTrustMaterial(null, acceptingTrustStrategy)
					.build();

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLSocketFactory(csf)
					.build();

			HttpComponentsClientHttpRequestFactory requestFactory =
					new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);
			RestTemplate restTemplate = new RestTemplate(requestFactory);

			String username = MyConstant.getPropertyParameter("username");
			String password = MyConstant.getPropertyParameter("password");

			MyConstant.LOGGER.info("url : " + url);

			String plainCreds = username+":"+password;
			byte[] plainCredsBytes = plainCreds.getBytes();
			byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
			String base64Creds = new String(base64CredsBytes);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Basic " + base64Creds);
			MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
			HttpEntity<String> entity = new HttpEntity<String>(headers);

			ResponseEntity<Object> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

			ObjectMapper mapper = new ObjectMapper();
			Object obj = respEntity.getBody();
			JsonNode root = mapper.readTree(new File(obj.toString()));

			//un noeud
			JsonNode dataNode = root.path("Data");


			// un autre noeud
			JsonNode resultNode = dataNode.path("result");
			if (resultNode.isArray()) {
				MyConstant.LOGGER.info("resultNode.isArray() is true ");
				MyConstant.LOGGER.info("resultNode.size() : " + resultNode.size());
			}
			else {
				MyConstant.LOGGER.info("resultNode.isArray() is false ");
			}
			for(JsonNode node : resultNode) {
				String u_internal_group_watch_list = node.path("u_internal_group_watch_list").asText();
				MyConstant.LOGGER.info("u_internal_group_watch_list : " + u_internal_group_watch_list);
			}


			return root;

		}catch(KeyManagementException ex) {
			MyConstant.LOGGER.info("KeyManagementException : "+ ex.getMessage());
		}catch(NoSuchAlgorithmException ex) {
			MyConstant.LOGGER.info("NoSuchAlgorithmException : "+ ex.getMessage());
		}catch(KeyStoreException ex) {
			MyConstant.LOGGER.info("KeyStoreException : "+ ex.getMessage());
		}catch(Exception ex) {
			MyConstant.LOGGER.info("Exception : "+ ex.getMessage());
		}

		return null;
	}


	//	not working
	@Override
	public void getSnowWithJackson(String url) {

		try {

			ObjectMapper objectMapper = new ObjectMapper();


			//converting json to Map
			//		byte[] mapData = url.getBytes();

			Map<String,String> myMap = new HashMap<String, String>();

			myMap = objectMapper.readValue(url, HashMap.class);

			MyConstant.LOGGER.info("Map is: "+myMap); 


			String json = url;
			Map<String, Object> map 
			= objectMapper.readValue(json, new TypeReference<Map<String,Object>>(){});	

			//		System.out.println("Map using TypeReference: "+map);
			MyConstant.LOGGER.info("Map using TypeReference: "+map); 

		} catch (JsonProcessingException ex) {
			// TODO Auto-generated catch block
			MyConstant.LOGGER.info(ex.getMessage());
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			MyConstant.LOGGER.info(ex.getMessage());
		}catch (Exception ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}




	}

	//	@Override
	//	public Result[] getDataParentIca3() {
	//
	//		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
	//try {
	//		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	//		                    .loadTrustMaterial(null, acceptingTrustStrategy)
	//		                    .build();
	//
	//		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	//
	//		    CloseableHttpClient httpClient = HttpClients.custom()
	//		                    .setSSLSocketFactory(csf)
	//		                    .build();
	//
	//		    HttpComponentsClientHttpRequestFactory requestFactory =
	//		                    new HttpComponentsClientHttpRequestFactory();
	//
	//		    requestFactory.setHttpClient(httpClient);
	//		    RestTemplate restTemplate = new RestTemplate(requestFactory);
	//
	//		String url = MyConstant.getPropertyParameter("url");
	//		String username = MyConstant.getPropertyParameter("username");
	//		String password = MyConstant.getPropertyParameter("password");
	//
	//		MyConstant.LOGGER.info("url : " + url);
	//
	//		String plainCreds = username+":"+password;
	//		byte[] plainCredsBytes = plainCreds.getBytes();
	//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	//		String base64Creds = new String(base64CredsBytes);
	//
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.add("Authorization", "Basic " + base64Creds);
	//		MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
	//		HttpEntity<String> entity = new HttpEntity<String>(headers);
	//
	//		ResponseEntity<Result[]> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Result[].class);
	//		Result[] result = respEntity.getBody();
	//
	//			return result;
	//		}catch(KeyManagementException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(NoSuchAlgorithmException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(KeyStoreException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(Exception ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}
	//
	//		return null;
	//	}

	//	@Override
	//	public Data getDataParentIca4() {
	//
	//		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
	//try {
	//		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	//		                    .loadTrustMaterial(null, acceptingTrustStrategy)
	//		                    .build();
	//
	//		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	//
	//		    CloseableHttpClient httpClient = HttpClients.custom()
	//		                    .setSSLSocketFactory(csf)
	//		                    .build();
	//
	//		    HttpComponentsClientHttpRequestFactory requestFactory =
	//		                    new HttpComponentsClientHttpRequestFactory();
	//
	//		    requestFactory.setHttpClient(httpClient);
	//		    RestTemplate restTemplate = new RestTemplate(requestFactory);
	//
	//		String url = MyConstant.getPropertyParameter("url");
	//		String username = MyConstant.getPropertyParameter("username");
	//		String password = MyConstant.getPropertyParameter("password");
	//
	//		MyConstant.LOGGER.info("url : " + url);
	//
	//		String plainCreds = username+":"+password;
	//		byte[] plainCredsBytes = plainCreds.getBytes();
	//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	//		String base64Creds = new String(base64CredsBytes);
	//
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.add("Authorization", "Basic " + base64Creds);
	//		MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
	//		HttpEntity<String> entity = new HttpEntity<String>(headers);
	//
	//		ResponseEntity<Data> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Data.class);
	//		Data result = respEntity.getBody();
	//
	//			return result;
	//		}catch(KeyManagementException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(NoSuchAlgorithmException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(KeyStoreException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(Exception ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}
	//
	//		return null;
	//	}

	//	@Override
	//	public DataParent getDataParentIca5() {
	//
	//		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
	//try {
	//		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	//		                    .loadTrustMaterial(null, acceptingTrustStrategy)
	//		                    .build();
	//
	//		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	//
	//		    CloseableHttpClient httpClient = HttpClients.custom()
	//		                    .setSSLSocketFactory(csf)
	//		                    .build();
	//
	//		    HttpComponentsClientHttpRequestFactory requestFactory =
	//		                    new HttpComponentsClientHttpRequestFactory();
	//
	//		    requestFactory.setHttpClient(httpClient);
	//		    RestTemplate restTemplate = new RestTemplate(requestFactory);
	//
	//		String url = MyConstant.getPropertyParameter("url");
	//		String username = MyConstant.getPropertyParameter("username");
	//		String password = MyConstant.getPropertyParameter("password");
	//
	//		MyConstant.LOGGER.info("url : " + url);
	//
	//		String plainCreds = username+":"+password;
	//		byte[] plainCredsBytes = plainCreds.getBytes();
	//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	//		String base64Creds = new String(base64CredsBytes);
	//
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.add("Authorization", "Basic " + base64Creds);
	//		MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
	//		HttpEntity<String> entity = new HttpEntity<String>(headers);
	//
	//		ResponseEntity<DataParent> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, DataParent.class);
	//		DataParent dataParent = respEntity.getBody();
	//
	//			return dataParent;
	//		}catch(KeyManagementException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(NoSuchAlgorithmException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(KeyStoreException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(Exception ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}
	//
	//		return null;
	//	}

	//	@Override
	//	public ResultList getDataParentIca7() {
	//
	//		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
	//try {
	//		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	//		                    .loadTrustMaterial(null, acceptingTrustStrategy)
	//		                    .build();
	//
	//		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	//
	//		    CloseableHttpClient httpClient = HttpClients.custom()
	//		                    .setSSLSocketFactory(csf)
	//		                    .build();
	//
	//		    HttpComponentsClientHttpRequestFactory requestFactory =
	//		                    new HttpComponentsClientHttpRequestFactory();
	//
	//		    requestFactory.setHttpClient(httpClient);
	//		    RestTemplate restTemplate = new RestTemplate(requestFactory);
	//
	//		String url = MyConstant.getPropertyParameter("url");
	//		String username = MyConstant.getPropertyParameter("username");
	//		String password = MyConstant.getPropertyParameter("password");
	//
	//		MyConstant.LOGGER.info("url : " + url);
	//
	//		String plainCreds = username+":"+password;
	//		byte[] plainCredsBytes = plainCreds.getBytes();
	//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	//		String base64Creds = new String(base64CredsBytes);
	//
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.add("Authorization", "Basic " + base64Creds);
	//		MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
	//		HttpEntity<String> entity = new HttpEntity<String>(headers);
	//
	//		ResponseEntity<ResultList> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, ResultList.class);
	//		ResultList resultList = respEntity.getBody();
	//
	//			return resultList;
	//		}catch(KeyManagementException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(NoSuchAlgorithmException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(KeyStoreException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(Exception ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}
	//
	//		return null;
	//	}

	//	@Override
	//	public JSONObject getDataParentIca8() {
	//
	//		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
	//try {
	//		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	//		                    .loadTrustMaterial(null, acceptingTrustStrategy)
	//		                    .build();
	//
	//		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	//
	//		    CloseableHttpClient httpClient = HttpClients.custom()
	//		                    .setSSLSocketFactory(csf)
	//		                    .build();
	//
	//		    HttpComponentsClientHttpRequestFactory requestFactory =
	//		                    new HttpComponentsClientHttpRequestFactory();
	//
	//		    requestFactory.setHttpClient(httpClient);
	//		    RestTemplate restTemplate = new RestTemplate(requestFactory);
	//
	//		String url = MyConstant.getPropertyParameter("url");
	//		String username = MyConstant.getPropertyParameter("username");
	//		String password = MyConstant.getPropertyParameter("password");
	//
	//		MyConstant.LOGGER.info("url : " + url);
	//
	//		String plainCreds = username+":"+password;
	//		byte[] plainCredsBytes = plainCreds.getBytes();
	//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	//		String base64Creds = new String(base64CredsBytes);
	//
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.add("Authorization", "Basic " + base64Creds);
	//		MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
	//		HttpEntity<String> entity = new HttpEntity<String>(headers);
	//
	//		ResponseEntity<JSONObject> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, JSONObject.class);
	//		JSONObject jSONObject = respEntity.getBody();
	//
	//			return jSONObject;
	//		}catch(KeyManagementException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(NoSuchAlgorithmException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(KeyStoreException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(Exception ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}
	//
	//		return null;
	//	}

	//	@Override
	//	public Object[] getDataParentIca9() {
	//
	//		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
	//try {
	//		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	//		                    .loadTrustMaterial(null, acceptingTrustStrategy)
	//		                    .build();
	//
	//		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	//
	//		    CloseableHttpClient httpClient = HttpClients.custom()
	//		                    .setSSLSocketFactory(csf)
	//		                    .build();
	//
	//		    HttpComponentsClientHttpRequestFactory requestFactory =
	//		                    new HttpComponentsClientHttpRequestFactory();
	//
	//		    requestFactory.setHttpClient(httpClient);
	//		    RestTemplate restTemplate = new RestTemplate(requestFactory);
	//
	//		String url = MyConstant.getPropertyParameter("url");
	//		String username = MyConstant.getPropertyParameter("username");
	//		String password = MyConstant.getPropertyParameter("password");
	//
	//		MyConstant.LOGGER.info("url : " + url);
	//
	//		String plainCreds = username+":"+password;
	//		byte[] plainCredsBytes = plainCreds.getBytes();
	//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	//		String base64Creds = new String(base64CredsBytes);
	//
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.add("Authorization", "Basic " + base64Creds);
	//		MyConstant.LOGGER.info("headers value : " + "Basic " + base64Creds);
	//		HttpEntity<String> entity = new HttpEntity<String>(headers);
	//
	//		ResponseEntity<Object[]> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Object[].class);
	//		Object[] objectTab = respEntity.getBody();
	//
	//			return objectTab;
	//		}catch(KeyManagementException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(NoSuchAlgorithmException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(KeyStoreException ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}catch(Exception ex) {
	//			MyConstant.LOGGER.info(ex.getMessage());
	//		}
	//
	//		return null;
	//	}



	@Override
	public void writeIcaCSVFile(Result[] icaResults) {

		String destination = MyConstant.getPropertyParameter("destination");

		File outputFile = new File(destination+"ica.csv");
		try {

//			natixis.drive.entities.Data data = dataParent.getData();
//			Result[] results = data.getResult();

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

				for (Result r : icaResults) {
					beanWriter.write(r, header, processors);
				}

			} catch (IOException ex) {
				MyConstant.LOGGER.info(ex.getMessage());
				ex.printStackTrace();
			} finally {
				if (beanWriter != null) {
					try {
						beanWriter.close();
					} catch (IOException ex) {
						MyConstant.LOGGER.info(ex.getMessage());
					}
				}
			}

		}catch(Exception ex) {
			MyConstant.LOGGER.info("writeCSVFile para : " + ex.getMessage());
		}
	}




}

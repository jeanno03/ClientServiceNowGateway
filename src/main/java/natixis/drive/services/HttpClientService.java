package natixis.drive.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.io.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import myconstants.MyConstant;

@Service
public class HttpClientService implements HttpClientServiceInterface{
	

	private TrustManager[ ] get_trust_mgr() {
		TrustManager[ ] certs = new TrustManager[ ] {
				new X509TrustManager() {
					public X509Certificate[ ] getAcceptedIssuers() { return null; }
					public void checkClientTrusted(X509Certificate[ ] certs, String t) { }
					public void checkServerTrusted(X509Certificate[ ] certs, String t) { }
				}
		};
		return certs;
	}

	
	public String getStringFromSnow(String url){

		MyConstant.LOGGER.info("url : " + url);
		String namehost = MyConstant.getPropertyParameter("namehost");
		String https_url = url;
		URL uri;
		try {

			// Create a context that doesn't check certificates.
			SSLContext ssl_ctx = SSLContext.getInstance("TLS");
			TrustManager[ ] trust_mgr = get_trust_mgr();
			ssl_ctx.init(null,                // key manager
					trust_mgr,           // trust manager
					new SecureRandom()); // random number generator
			HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());

			uri = new URL(https_url);

			HttpsURLConnection con = (HttpsURLConnection)uri.openConnection();

			String username = MyConstant.getPropertyParameter("username");
			String password = MyConstant.getPropertyParameter("password");

			String userCredentials = username + ":" + password;
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

			con.setRequestProperty ("Authorization", basicAuth);

			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);

			// Guard against "bad hostname" errors during handshake.
			con.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String host, SSLSession sess) {
					if (host.equals(namehost)) {
						MyConstant.LOGGER.info("namehost : " + namehost);
						return true;
					}

					else return false;
				}
			});

			//facultatif début*********************************

			if(con!=null){

				try {
					MyConstant.LOGGER.info("Response Code : " + con.getResponseCode());
					MyConstant.LOGGER.info("Cipher Suite : " + con.getCipherSuite());	

					Certificate[] certs = con.getServerCertificates();
					for(Certificate cert : certs){
						MyConstant.LOGGER.info("Cert Type : " + cert.getType());
						MyConstant.LOGGER.info("Cert Hash Code : " + cert.hashCode());
						MyConstant.LOGGER.info("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
						MyConstant.LOGGER.info("Cert Public Key Format : " + cert.getPublicKey().getFormat());


					}


				} catch (SSLPeerUnverifiedException ex) {
					MyConstant.LOGGER.info(ex.getMessage());
				} catch (IOException ex){
					MyConstant.LOGGER.info(ex.getMessage());
				}	   
			}
			
			//facultatif fin *********************************

			if(con!=null){

				try {

					BufferedReader br = 
							new BufferedReader(
									new InputStreamReader(con.getInputStream(), "UTF8"));

					String input;
					String result = null;

					while ((input = br.readLine()) != null){
				
						result = result + input;

					}
					br.close();

					
					return result;
					
				} catch (IOException ex) {
					MyConstant.LOGGER.info(ex.getMessage());
				}		
			}
			
			

		} catch (MalformedURLException ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		} catch (IOException ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}catch (NoSuchAlgorithmException ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}catch (KeyManagementException ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}catch	 (ResourceAccessException ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}catch(Exception ex) {
			MyConstant.LOGGER.info(ex.getMessage());
		}
		
		return null;
	}

	//facultatif
//	private void print_https_cert(HttpsURLConnection con){
//				
//		if(con!=null){
//
//			try {
//				MyConstant.LOGGER.info("Response Code : " + con.getResponseCode());
//				MyConstant.LOGGER.info("Cipher Suite : " + con.getCipherSuite());	
//
//				Certificate[] certs = con.getServerCertificates();
//				for(Certificate cert : certs){
//					MyConstant.LOGGER.info("Cert Type : " + cert.getType());
//					MyConstant.LOGGER.info("Cert Hash Code : " + cert.hashCode());
//					MyConstant.LOGGER.info("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
//					MyConstant.LOGGER.info("Cert Public Key Format : " + cert.getPublicKey().getFormat());
//
//
//				}
//
//
//			} catch (SSLPeerUnverifiedException ex) {
//				MyConstant.LOGGER.info(ex.getMessage());
//			} catch (IOException ex){
//				MyConstant.LOGGER.info(ex.getMessage());
//			}	   
//		}		
//	}

	
//	private void print_content(HttpsURLConnection con){
//		if(con!=null){
//
//			try {
//				MyConstant.LOGGER.info("****** Content of the URL ********");
//
//				BufferedReader br = 
//						new BufferedReader(
//								new InputStreamReader(con.getInputStream()));
//
//				String input;
//
//				while ((input = br.readLine()) != null){
//					MyConstant.LOGGER.info(input);
//
//				}
//				br.close();
//
//			} catch (IOException ex) {
//				MyConstant.LOGGER.info(ex.getMessage());
//			}		
//		}
//	}

}

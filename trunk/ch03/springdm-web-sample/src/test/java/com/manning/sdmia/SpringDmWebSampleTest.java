/**
 * 
 */
package com.manning.sdmia;

import java.net.HttpURLConnection;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

/**
 * @author acogoluegnes
 *
 */
public class SpringDmWebSampleTest extends AbstractConfigurableBundleCreatorTests {

	public void testIntegration() throws Exception {		
		boolean bundleIsHereAndStarted = false;
		for(Bundle currentBundle : bundleContext.getBundles()) {
			if("com.manning.sdmia.springdm-web-sample".equals(currentBundle.getSymbolicName()) 
					&& currentBundle.getState() == Bundle.ACTIVE) {
				bundleIsHereAndStarted = true;
				break;
			}			
		}
		assertTrue("springdm-web-sample is not installed nor activated!",bundleIsHereAndStarted);
		
		Thread.sleep(10 * 1000);
		
		testConnection("http://localhost:8080/springdm-web-sample/index.html");
		testConnection("http://localhost:8080/springdm-web-sample/index.jsp");
	}
	
	@Override
	protected String[] getTestBundlesNames() {
		return new String [] {
			"org.springframework.osgi, spring-osgi-web," + getSpringDMVersion(),
			"org.springframework.osgi, spring-osgi-web-extender," + getSpringDMVersion(),
			"org.springframework.osgi, servlet-api.osgi, 2.5-SNAPSHOT",
			"org.springframework.osgi, catalina.osgi, 5.5.23-SNAPSHOT",
			"org.springframework.osgi, catalina.start.osgi, 1.0.0",								
			"org.springframework.osgi, jsp-api.osgi, 2.0-SNAPSHOT",
			"org.springframework.osgi, jasper.osgi, 5.5.23-SNAPSHOT",
			"org.springframework.osgi, commons-el.osgi, 1.0-SNAPSHOT",
			"org.springframework.osgi, jstl.osgi, 1.1.2-SNAPSHOT",
			"com.manning.sdmia, springdm-web-sample, 1.0-SNAPSHOT"				
		};
	}
	
	private void testConnection(String address) throws Exception {
		URL url = new URL(address);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setUseCaches(false);
		try {
			con.connect();
			assertEquals(HttpURLConnection.HTTP_OK, con.getResponseCode());
		}
		finally {
			con.disconnect();
		}
	}

}
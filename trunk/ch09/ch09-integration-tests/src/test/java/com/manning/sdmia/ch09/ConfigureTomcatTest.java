/**
 * 
 */
package com.manning.sdmia.ch09;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

/**
 * @author acogoluegnes
 *
 */
public class ConfigureTomcatTest extends AbstractOsgiTest {

	public void testLaunchJettyWithConfigurationFragment() throws Exception {
		// waits a little for everything to be deployed correctly
		Thread.sleep(5 *1000);
		
		String pageContent = getTextResponse("http://localhost:8090/simplewebmvcapp/hello.htm");
		
		Assert.assertTrue(pageContent.contains("Hello from chapter 09! Today we are"));
	}
	
	@Override
	protected String[] getTestBundlesNames() {
		List<String> col = new ArrayList<String>();
		// Servlet/JSP artifacts
		col.addAll(getJavaEe4WebArtifacts());

		// JSP compiler
		col.add(SPRING_OSGI_GROUP + ", jasper.osgi, 5.5.23-SNAPSHOT");
		col.add(SPRING_OSGI_GROUP + ", commons-el.osgi, 1.0-SNAPSHOT");

		// standard tag library
		col.add(SPRING_OSGI_GROUP+", jstl.osgi, 1.1.2-SNAPSHOT");
		
		// web container
		col.addAll(getTomcat5Artifacts());		
		
		// web container configuration fragment
		col.add("com.manning.sdmia.ch09, tomcat-configuration-fragment, 1.0.0");

		// Spring DM web extender
		col.addAll(getSpringDmWebArtifacts());
		
		// Spring Web
		col.add("org.springframework, org.springframework.web, "+getSpringVersion());
		col.add("org.springframework, org.springframework.web.servlet, "+getSpringVersion());
		
		// simple web app
		col.add("com.manning.sdmia.ch09, simple-web-mvc-app, 1.0.0");
		
		return (String[]) col.toArray(new String[col.size()]);
	}
	
}
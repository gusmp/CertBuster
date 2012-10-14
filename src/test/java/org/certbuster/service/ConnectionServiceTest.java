package org.certbuster.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.certbuster.beans.CertificateInfoBean;
import org.certbuster.beans.CertificateInfoBean.RESULT_CODE;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


public class ConnectionServiceTest
{
	private ConfigService configService;
	private final String CONFIG_FILE = "src/test/resources/config.properties";
	private ConnectionService connectionService;
	
	private String  HOST_TEST = "www.google.es"; 
	private Integer PORT_TEST = 443;
	private String  ISSUER_TEST = "CN=Google Internet Authority, O=Google Inc, C=US";
	private String 	SUBJECT_TEST = "CN=*.google.es, O=Google Inc, L=Mountain View, ST=California, C=US";
	
	
	
	@Before
	public void init() throws IOException, ParserConfigurationException
	{
		configService = new ConfigService();
		configService.load(CONFIG_FILE);
		connectionService = new ConnectionService();
	}
    
	@Test
    public void connectionServerAuthTest()
    {
		CertificateInfoBean certInfoBean = connectionService.getCertificate(HOST_TEST, PORT_TEST);
		assertNotNull(certInfoBean);
		assertEquals(RESULT_CODE.OK, certInfoBean.getResult());
		assertEquals(ISSUER_TEST, certInfoBean.getIssuer());
		assertEquals(SUBJECT_TEST, certInfoBean.getSubject());
		assertNotNull(certInfoBean.getNotBefore());
		assertNotNull(certInfoBean.getNotAfter());
		assertEquals(HOST_TEST, certInfoBean.getHost());
		assertEquals(PORT_TEST, certInfoBean.getPort());
		
    }
   
}

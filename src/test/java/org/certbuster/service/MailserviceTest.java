package org.certbuster.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.certbuster.beans.CertificateInfoBean;
import org.certbuster.beans.CertificateInfoBean.RESULT_CODE;
import org.certbuster.beans.ConfigurationBean;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class MailserviceTest 
{
	private ConfigService configService;
	private final String CONFIG_FILE = "src/test/resources/config.properties";
	
	MailService mailService;
	private List<CertificateInfoBean> certificateInfoBeans;
	
	
	private void buildFixure()
	{
		certificateInfoBeans = new ArrayList<CertificateInfoBean>(ConfigurationBean.EXPIRE_WARN.length+1);
		
		CertificateInfoBean ciBean;
		
		for(Integer days: ConfigurationBean.EXPIRE_WARN)
		{
			ciBean = new CertificateInfoBean();
			ciBean.setResult(RESULT_CODE.OK);
			
			ciBean.setHost("Host " + days);
			ciBean.setIssuer("Issuer " + days);
			ciBean.setSubject("Subject " + days);
			ciBean.setPort(443);
			ciBean.setNotBefore(new Date());
			
			Calendar expire = GregorianCalendar.getInstance();
			expire.add(Calendar.DAY_OF_MONTH, days);
			
			ciBean.setNotAfter(expire.getTime());
			
			certificateInfoBeans.add(ciBean);
		}
		
		ciBean = new CertificateInfoBean();

		ciBean.setHost("Host Expired");
		ciBean.setIssuer("Issuer Expired");
		ciBean.setSubject("Subject Expired");
		ciBean.setPort(443);
		
		Calendar expire = GregorianCalendar.getInstance();
		expire.add(Calendar.DAY_OF_MONTH, -20);
		
		ciBean.setNotBefore(expire.getTime());
		ciBean.setNotAfter(expire.getTime());

		certificateInfoBeans.add(ciBean);
		
	}
	
	@Before
	public void init() throws IOException, ParserConfigurationException
	{
		mailService = new MailService();
		configService = new ConfigService();
		configService.load(CONFIG_FILE);
		buildFixure();
	}
	
	@Test
	public void sendMailTest()
	{
		try
		{
			mailService.sendMail(certificateInfoBeans);
			assertTrue(true);
		}
		catch(Exception exc)
		{
			System.out.println(exc.toString());
			assertTrue(false);
		}
	}
	
	@Test
	public void filterCertificatesTest()
	{
		try
		{
			Map<Integer, ArrayList<CertificateInfoBean>> certificateMap = mailService.filterCertificates(certificateInfoBeans);
			assertTrue(!certificateMap.isEmpty());
			for(Integer item : ConfigurationBean.EXPIRE_WARN)
			{
				assertEquals(1, certificateMap.get(item).size());
			}
			assertEquals(1, certificateMap.get(0).size());
			
			
		}
		catch(Exception exc)
		{
			System.out.println(exc.toString());
			assertTrue(false);
		}
	}

}

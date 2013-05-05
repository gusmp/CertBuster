package org.certbuster.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.certbuster.beans.CertificateInfoBean;
import org.certbuster.beans.CertificateInfoBean.RESULT_CODE;
import org.certbuster.service.CertificateService.Crl_Status;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;


public class ReportServiceTest
{
	private ConfigService configService;
	private final String CONFIG_FILE = "src/test/resources/config.properties";
	
	private ReportService reportService;
	private List<CertificateInfoBean> certificateInfoBeanList;
	
	@Before
	public void init() throws IOException, ParserConfigurationException
	{
		configService = new ConfigService();
		configService.load(CONFIG_FILE);
		
		reportService = new ReportService();
		certificateInfoBeanList = new ArrayList<CertificateInfoBean>();
		
		for(int i=0; i < 5; i++)
		{
			CertificateInfoBean certificateInfoBean = new CertificateInfoBean();
			certificateInfoBean.setResult(RESULT_CODE.OK);
			certificateInfoBean.setIssuer("Issuer " + i);
			certificateInfoBean.setSubject("Subject " + i);
			certificateInfoBean.setNotAfter(new Date());
			certificateInfoBean.setNotBefore(new Date());
			certificateInfoBean.setHost("Host " + i);
			certificateInfoBean.setPort(5000 + i);
			certificateInfoBean.setCrlStatus(Crl_Status.VALID);
			
			certificateInfoBeanList.add(certificateInfoBean);
		}
	}
    
	@Test
    public void reportTest()
    {
		try
		{
			reportService.generateReport(certificateInfoBeanList);
			assertTrue(true);
		}
		catch(Exception exc)
		{
			assertTrue(false);
		}
		
    }
    
}

package org.certbuster.service;

import org.certbuster.beans.ConfigurationBean;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class ConfigServiceTest
{
	private ConfigService configService;
	private final String CONFIG_FILE = "src/test/resources/config.properties";
	
	@Before
	public void init()
	{
		configService = new ConfigService();
	}
    
	@Test
    public void loadConfigurationTest()
    {
		String  HOST_FILE = "src/test/resources/hosts.csv";
		
		try
		{
			configService.load(CONFIG_FILE);
			assertEquals(ConfigurationBean.HOSTFILE, HOST_FILE);
			assertEquals(ConfigurationBean.HTTP_PROXY_PORT,"");
			assertEquals(ConfigurationBean.ENABLE_LOGIN,true);
			assertEquals(ConfigurationBean.MAIL_SMTP_HOST,"smtp.gmail.com");
			assertArrayEquals(ConfigurationBean.EXPIRE_WARN, new Integer[] {60,30,15});
		}
		catch(Exception exc)
		{
			assertTrue(false);
		}
    }
    
    
}

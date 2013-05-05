package org.certbuster.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.certbuster.beans.ConfigurationBean;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class HostFileServiceTest
{
	private ConfigService configService;
	private final String CONFIG_FILE = "src/test/resources/config.properties";
	private HostFileService hostFileService;
	
	@Before
	public void init() throws IOException, ParserConfigurationException
	{
		configService = new ConfigService();
		configService.load(CONFIG_FILE);
		hostFileService = new HostFileService();
	}
    
	@Test
    public void loadHostFileTest()
    {
		try
		{
			hostFileService.load(ConfigurationBean.HOSTFILE);
			assertNotNull(ConfigurationBean.HOSTLIST);
			assertEquals(5,ConfigurationBean.HOSTLIST.size());
		}
		catch(Exception exc)
		{
			assertTrue(false);
		}
    }
}

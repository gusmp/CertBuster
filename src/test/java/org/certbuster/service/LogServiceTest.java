package org.certbuster.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;


public class LogServiceTest
{
	private ConfigService configService;
	private final String CONFIG_FILE = "src/test/resources/config.properties";
	
	@Before
	public void init() throws IOException, ParserConfigurationException
	{
		configService = new ConfigService();
		configService.load(CONFIG_FILE);
	}
    
	@Test
    public void logTest()
    {
		LogService.writeLog("test");
    }
    
    
}

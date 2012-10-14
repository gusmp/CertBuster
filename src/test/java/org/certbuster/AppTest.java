package org.certbuster;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.certbuster.service.ConfigService;
import org.junit.Before;
import org.junit.Test;

public class AppTest
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
    public void appTest()
    {
		try
		{
			App app = new App(CONFIG_FILE);
			app.run();
			assertTrue(true);
		}
		catch(Exception exc)
		{
			System.out.println(exc.toString());
			assertTrue(false);
		}
    }
}

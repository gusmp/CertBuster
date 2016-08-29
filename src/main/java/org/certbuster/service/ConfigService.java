package org.certbuster.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.certbuster.beans.ConfigurationBean;

public class ConfigService
{
    // Properties
    private String HOST_FILE = "hostFile";

    private String EXPIRE_WARN = "expireWarn";
    private String ENABLE_SEND_WARN_MAIL = "sendWarnMail";
    private String WARN_MAIL_ADDRESS = "warnMailAddress";

    private String HTTP_PROXY_HOST = "http.proxyHost";
    private String HTTP_PROXY_PORT = "http.proxyPort";
    private String HTTP_PROXY_USER = "http.proxyUser";
    private String HTTP_PROXY_PASSWORD = "http.proxyPassword";

    private String LOG_DIR = "logDir";
    private String ENABLE_LOGIN = "enableLogin";

    private String REPORTING_DIR = "reportingDir";

    private String MAIL_SMTP_HOST = "mail.smtp.host";
    private String MAIL_SMTP_START_TLS_ENABLE = "mail.smtp.starttls.enable";
    private String MAIL_SMTP_PORT = "mail.smtp.port";
    private String MAIL_SMTP_USER = "mail.smtp.user";
    private String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private String MAIL_SMTP_PASSWORD = "mail.smtp.password";

    private String VALIDATION_CRL_STATUS = "validate.crlStatus";

    public void load(String configFilePath) throws FileNotFoundException, IOException, ParserConfigurationException
    {
		FileInputStream fin = new FileInputStream(configFilePath);
		Properties properties = new Properties();
		properties.load(fin);
	
		ConfigurationBean.HOSTFILE = properties.getProperty(HOST_FILE);
	
		loadWarningProperties(properties);
	
		loadProxyProperties(properties);
	
		loadLoggingProperties(properties);
	
		loadReportingProperties(properties);
	
		loadMailProperties(properties);
	
		loadValidationProperties(properties);

    }

    private void loadWarningProperties(Properties properties) throws ParserConfigurationException
    {
		String daysWarnList[] = properties.getProperty(EXPIRE_WARN).split(",");
		if (daysWarnList.length < 1)
		{
		    ConfigurationBean.EXPIRE_WARN = new Integer[] { 30 };
		}
	
		Integer daysList[] = new Integer[daysWarnList.length];
		for (int i = 0; i < daysWarnList.length; i++)
		{
		    daysList[i] = Integer.valueOf(daysWarnList[i]);
		}
		ConfigurationBean.EXPIRE_WARN = daysList;
	
		ConfigurationBean.WARN_MAIL_ADDRESS = properties.getProperty(WARN_MAIL_ADDRESS);
		
		ConfigurationBean.ENABLE_SEND_WARN_MAIL = false;
		if (properties.getProperty(ENABLE_SEND_WARN_MAIL).equalsIgnoreCase("1") == true)
		{
		    ConfigurationBean.ENABLE_SEND_WARN_MAIL = true;
		}
    }

    private void loadProxyProperties(Properties properties) throws ParserConfigurationException
    {
		ConfigurationBean.HTTP_PROXY_HOST = properties.getProperty(HTTP_PROXY_HOST);
		ConfigurationBean.HTTP_PROXY_PORT = properties.getProperty(HTTP_PROXY_PORT);
		ConfigurationBean.HTTP_PROXY_USER = properties.getProperty(HTTP_PROXY_USER);
		ConfigurationBean.HTTP_PROXY_PASSWORD = properties.getProperty(HTTP_PROXY_PASSWORD);
	
		ConfigurationBean.USE_PROXY = false;
		if (ConfigurationBean.HTTP_PROXY_HOST.length() > 0)
		{
		    ConfigurationBean.USE_PROXY = true;
		}
    }

    private void loadLoggingProperties(Properties properties) throws ParserConfigurationException
    {
		ConfigurationBean.LOG_DIR = properties.getProperty(LOG_DIR);
		if (ConfigurationBean.LOG_DIR.length() > 0)
		{
		    ConfigurationBean.LOG_DIR += System.getProperty("file.separator");
		}
	
		ConfigurationBean.ENABLE_LOGIN = false;
		if (properties.getProperty(ENABLE_LOGIN).equalsIgnoreCase("1") == true)
		{
		    ConfigurationBean.ENABLE_LOGIN = true;
		}
    }

    private void loadReportingProperties(Properties properties) throws ParserConfigurationException
    {
		ConfigurationBean.REPORTING_DIR = properties.getProperty(REPORTING_DIR);
		if (ConfigurationBean.REPORTING_DIR.length() > 0)
		{
		    ConfigurationBean.REPORTING_DIR += System.getProperty("file.separator");
		}
    }

    private void loadMailProperties(Properties properties) throws ParserConfigurationException
    {
		ConfigurationBean.MAIL_SMTP_HOST = properties.getProperty(MAIL_SMTP_HOST);
	
		if (properties.getProperty(MAIL_SMTP_START_TLS_ENABLE).equalsIgnoreCase("1") == true)
		{
		    ConfigurationBean.MAIL_SMTP_START_TLS_ENABLE = true;
		}
		else
		{
		    ConfigurationBean.MAIL_SMTP_START_TLS_ENABLE = false;
		}
	
		try
		{
		    Integer port = Integer.valueOf(properties.getProperty(MAIL_SMTP_PORT));
		    if ((port < 0) || (port > 65536))
		    {
			throw new ParserConfigurationException(MAIL_SMTP_PORT + " is not a valid port");
		    }
	
		    ConfigurationBean.MAIL_SMTP_PORT = properties.getProperty(MAIL_SMTP_PORT);
		}
		catch (NumberFormatException exc)
		{
		    throw new ParserConfigurationException(MAIL_SMTP_PORT + " is not a valid port");
		}
	
		ConfigurationBean.MAIL_SMTP_USER = properties.getProperty(MAIL_SMTP_USER);
	
		if (properties.getProperty(MAIL_SMTP_AUTH).equalsIgnoreCase("1") == true)
		{
		    ConfigurationBean.MAIL_SMTP_AUTH = true;
		}
		else
		{
		    ConfigurationBean.MAIL_SMTP_AUTH = false;
		}
	
		ConfigurationBean.MAIL_SMTP_PASSWORD = properties.getProperty(MAIL_SMTP_PASSWORD);
    }

    private void loadValidationProperties(Properties properties)
    {
		ConfigurationBean.VALIDATION_CRL_STATUS = false;
		if (properties.getProperty(VALIDATION_CRL_STATUS) != null)
		{
		    if (properties.getProperty(VALIDATION_CRL_STATUS).equalsIgnoreCase("1") == true)
		    {
			ConfigurationBean.VALIDATION_CRL_STATUS = true;
		    }
		}
    }
}

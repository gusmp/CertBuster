package org.certbuster.beans;

import java.util.ArrayList;

public class ConfigurationBean 
{
	// Misc
	public static String HOSTFILE;
	public static ArrayList<HostInfoBean> HOSTLIST;
	
	// Warnings
	public static Integer[] EXPIRE_WARN;
	public static String WARN_MAIL_ADDRESS;
	
	// Proxy
	public static Boolean USE_PROXY;
	public static String HTTP_PROXY_HOST;
	public static String HTTP_PROXY_PORT;
	public static String HTTP_PROXY_USER;
	public static String HTTP_PROXY_PASSWORD;
	
	// Logging
	public static String LOG_DIR;
	public static String LOG_NAME = "certBuster";
	public static Boolean ENABLE_LOGIN;
	
	// Reporting
	public static String REPORTING_DIR;
	public static String REPORTING_NAME = "certBusterReport";
	
	// Mail
	public static String  MAIL_SMTP_HOST;
	public static Boolean MAIL_SMTP_START_TLS_ENABLE;
	public static String  MAIL_SMTP_PORT;
	public static String  MAIL_SMTP_USER;
	public static Boolean MAIL_SMTP_AUTH;
	public static String  MAIL_SMTP_PASSWORD;
	
	// validations
	public static Boolean VALIDATION_CRL_STATUS;
	
	
}


package org.certbuster.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.certbuster.beans.ConfigurationBean;

public class LogService
{

    private static FileOutputStream flog = null;

    static
    {
	if (ConfigurationBean.ENABLE_LOGIN == true)
	{
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	    String logFile = ConfigurationBean.LOG_DIR + ConfigurationBean.LOG_NAME + sdf.format(new Date()) + ".txt";
	    try
	    {
		flog = new FileOutputStream(logFile);
	    }
	    catch (Exception exc)
	    {
	    }
	}
    }

    public static void writeLog(String message)
    {
	try
	{
	    if (ConfigurationBean.ENABLE_LOGIN == true)
	    {
		flog.write((message + "\n").getBytes());
	    }
	}
	catch (Exception exc)
	{
	    // keep silence
	}

    }

}

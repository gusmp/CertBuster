package org.certbuster.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.certbuster.beans.ConfigurationBean;
import org.certbuster.beans.HostInfoBean;

public class HostFileService 
{
	private String LINE_SEPARATOR  = ";";
	private String RANGE_SEPARATOR = "-";
	private Integer DEFAULT_PORT   = 443;
	
	public void load(String hostFile) throws FileNotFoundException, IOException
	{
		String line;
		Boolean isHeader = true;
		ArrayList<HostInfoBean> hostsList = new ArrayList<HostInfoBean>(100);
		HostInfoBean hostInfo;
		
		BufferedReader reader = new BufferedReader(new FileReader(hostFile));
		
		while((line = reader.readLine()) != null)
		{
			if (isHeader == true)
			{
				isHeader = false;
				continue;
			}
			
			hostInfo = processLine(line);
			if (hostInfo != null)
			{
				hostsList.add(hostInfo);
			}
		}
		
		if (hostsList.isEmpty() == false)
		{
			ConfigurationBean.HOSTLIST = hostsList;
		}
	}
	
	private HostInfoBean processLine(String line)
	{
		HostInfoBean hostInfo = null;
		
		try
		{
			String fields[] = line.split(LINE_SEPARATOR);
			
			if (fields.length == 1)
			{
				LogService.writeLog(fields[0] + " has no port. Use " + DEFAULT_PORT);
				hostInfo = new HostInfoBean(fields[0], DEFAULT_PORT, DEFAULT_PORT);
			}
			else if (fields.length >= 2)
			{
				String range[] = fields[1].split(RANGE_SEPARATOR);
	
				if (range.length == 1)
				{
					hostInfo = new HostInfoBean(fields[0],testPort(fields[0],range[0]),testPort(fields[0],range[0]));
				}
				else if (range.length == 2)
				{
					if (testPort(fields[0], range[0]) <= testPort(fields[0], range[1]))
					{
						hostInfo = new HostInfoBean(fields[0],testPort(fields[0], range[0]),testPort(fields[0], range[1]));
					}
					else
					{
						LogService.writeLog(fields[0] + " has wrong range " + range[0] + "/" + range[1]);
					}
				}
			}
		}
		catch(Exception exc) {}
		
		return(hostInfo);
		
	}
	
	private Integer testPort(String host, String port) throws Exception
	{
		Integer iPort = 0;
		
		try
		{
			iPort = Integer.valueOf(port);
		}
		catch(NumberFormatException exc)
		{
			LogService.writeLog(host + " the port " + port + " is not nummeric");
			throw exc;
		}
		
		if ((iPort < 0) || (iPort > 65536))
		{
			LogService.writeLog(host + " wrong port number");
			throw new Exception("Wrong port number");
		}
		
		return (iPort); 
	}
	
}

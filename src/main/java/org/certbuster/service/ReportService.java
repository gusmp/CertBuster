package org.certbuster.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.certbuster.beans.CertificateInfoBean;
import org.certbuster.beans.ConfigurationBean;

public class ReportService 
{
	public static enum HeaderValues 
	{
		HOST("Host"),
		PORT("Port"),
		
		ISSUER("Issuer"),
		SUBJECT("Subject"),
		
		NOT_BEFORE("Not before"),
		NOT_AFTER("Not after"),
		
		STATUS_CRL("Status (by CRL)");
		
		
		private String value;
		
		HeaderValues(String value)
		{
			this.value = value;
		}
		
		public String getValue()
		{
			return(this.value);
		}
	}
	

	public String generateReport(List<CertificateInfoBean> certificateInfoList) throws IOException
	{
		String reportFile = getReportFileName();
		
		FileOutputStream fout = new FileOutputStream(reportFile);
		
		// Write header
		StringBuffer header = new StringBuffer();
		header.append(HeaderValues.HOST.getValue() + ";");
		header.append(HeaderValues.PORT.getValue() + ";");
		header.append(HeaderValues.ISSUER.getValue() + ";");
		header.append(HeaderValues.NOT_BEFORE.getValue() + ";");
		header.append(HeaderValues.NOT_AFTER.getValue() + ";");
		if (ConfigurationBean.VALIDATION_CRL_STATUS == true)
		{
			header.append(HeaderValues.STATUS_CRL.getValue() + ";");
		}
		header.append(HeaderValues.SUBJECT.getValue() + ";");
		header.append("\n");
		
		fout.write(header.toString().getBytes());
		
		// Write contents
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		StringBuffer reportLine = new StringBuffer();
		for(CertificateInfoBean ciBean: certificateInfoList)
		{
			reportLine.delete(0, reportLine.length());
			
			reportLine.append(ciBean.getHost() + ";");
			reportLine.append(ciBean.getPort()  + ";");
			reportLine.append(ciBean.getIssuer() + ";");
			reportLine.append(sdf.format(ciBean.getNotBefore()) + ";");
			reportLine.append(sdf.format(ciBean.getNotAfter())  + ";");
			if (ConfigurationBean.VALIDATION_CRL_STATUS == true)
			{
				reportLine.append(ciBean.getCrlStatus().getValue()  + ";");
			}
			reportLine.append(ciBean.getSubject()  + ";");
			reportLine.append("\n");
			
			fout.write(reportLine.toString().getBytes());
		}
		
		fout.close();
		
		return(reportFile);
		
	}
	
	private String getReportFileName()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String reportFile = ConfigurationBean.REPORTING_DIR + ConfigurationBean.REPORTING_NAME + sdf.format(new Date()) + ".csv";
		return(reportFile);
	}
}

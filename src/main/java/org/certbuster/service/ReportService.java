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
		NOT_AFTER("Not after");
		
		
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
		fout.write((HeaderValues.HOST.getValue() + ";" +
				HeaderValues.PORT.getValue() + ";" +
				HeaderValues.ISSUER.getValue() + ";" +
				HeaderValues.NOT_AFTER.getValue() + ";" +
				HeaderValues.SUBJECT.getValue() + ";" +
				HeaderValues.NOT_BEFORE.getValue() + ";" + "\n").getBytes());
		
		// Write contents
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		for(CertificateInfoBean ciBean: certificateInfoList)
		{
			fout.write((
					ciBean.getHost() + ";" +
					ciBean.getPort()  + ";" +
					ciBean.getIssuer() + ";" +
					sdf.format(ciBean.getNotAfter())  + ";" +
					ciBean.getSubject()  + ";" +
					sdf.format(ciBean.getNotBefore()) + ";" + "\n").getBytes());
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

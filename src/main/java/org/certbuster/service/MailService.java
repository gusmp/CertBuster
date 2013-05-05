package org.certbuster.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.certbuster.beans.CertificateInfoBean;
import org.certbuster.beans.ConfigurationBean;
import org.certbuster.service.ReportService.HeaderValues;

public class MailService 
{
	public void sendMail(List<CertificateInfoBean> certificateInfoBeanList) throws AddressException, MessagingException
	{
		Properties props = new Properties();

		props.setProperty("mail.smtp.host", ConfigurationBean.MAIL_SMTP_HOST);
		
		if (ConfigurationBean.MAIL_SMTP_START_TLS_ENABLE == true)
		{
			props.setProperty("mail.smtp.starttls.enable", "true");
		}
		else
		{
			props.setProperty("mail.smtp.starttls.enable", "false");
		}
		
		props.setProperty("mail.smtp.port", ConfigurationBean.MAIL_SMTP_PORT);
		props.setProperty("mail.smtp.user", ConfigurationBean.MAIL_SMTP_USER);

		if (ConfigurationBean.MAIL_SMTP_AUTH == true)
		{
			props.setProperty("mail.smtp.auth", "true");
		}
		else
		{
			props.setProperty("mail.smtp.auth", "false");
		}
		
		Session session = Session.getDefaultInstance(props);
		session.setDebug(false);
		
		MimeMessage message = new MimeMessage(session);
		
		message.setFrom(new InternetAddress("certuster@foo.com"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(ConfigurationBean.WARN_MAIL_ADDRESS));
		
		message.setSubject("SSL certificates mail status");
		message.setText(buildMail(filterCertificates(certificateInfoBeanList)), "UTF-8", "html");
		
		Transport t = session.getTransport("smtp");
		t.connect(ConfigurationBean.MAIL_SMTP_USER, ConfigurationBean.MAIL_SMTP_PASSWORD);
		t.sendMessage(message,message.getAllRecipients());
		t.close();
	}
	
	public Map<Integer, ArrayList<CertificateInfoBean>> filterCertificates(List<CertificateInfoBean> certificateInfoBeanList)
	{
		Map<Integer, ArrayList<CertificateInfoBean>> certificateInfoBeanWarnMap = new HashMap<Integer, ArrayList<CertificateInfoBean>>(ConfigurationBean.EXPIRE_WARN.length+1);
		
		certificateInfoBeanWarnMap.put(0, new ArrayList<CertificateInfoBean>());
		for(Integer days: ConfigurationBean.EXPIRE_WARN)
		{
			certificateInfoBeanWarnMap.put(days, new ArrayList<CertificateInfoBean>());
		}
		
		for(CertificateInfoBean certificateInfoBean : certificateInfoBeanList)
		{
			Calendar todayLower = GregorianCalendar.getInstance();
			Calendar todayUpper = GregorianCalendar.getInstance();
			if (certificateInfoBean.getNotAfter().before(todayLower.getTime()) == true)
			{
				 certificateInfoBeanWarnMap.get(0).add(certificateInfoBean);
			}
			else
			{
				for(Integer daysToExpire: ConfigurationBean.EXPIRE_WARN)
				{
					todayLower = GregorianCalendar.getInstance();
					todayLower.add(Calendar.DAY_OF_MONTH, daysToExpire);
					todayLower.set(Calendar.HOUR_OF_DAY, 0);
					todayLower.set(Calendar.MINUTE, 0);
					todayLower.set(Calendar.SECOND, 0);
					
					todayUpper = (Calendar) todayLower.clone();
					todayUpper.set(Calendar.HOUR_OF_DAY, 23);
					todayUpper.set(Calendar.MINUTE, 59);
					todayUpper.set(Calendar.SECOND, 59);
					
					if ((certificateInfoBean.getNotAfter().after(todayLower.getTime())) &&
							(certificateInfoBean.getNotAfter().before(todayUpper.getTime())))
					{
						certificateInfoBeanWarnMap.get(daysToExpire).add(certificateInfoBean);
						break;
					}
				}
			}
		}
		
		return certificateInfoBeanWarnMap;
	}
	
	private String buildMail(Map<Integer, ArrayList<CertificateInfoBean>> certificateInfoBeanWarnMap)
	{
		// With velocity much better...
		StringBuilder sb = new StringBuilder();
		
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");	
		sb.append("<head>");
		sb.append("  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
		sb.append("  <title></title>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("  <h1>Certificates about to expire</h1>");
		
		List<Integer> listKeys = new ArrayList<Integer>(certificateInfoBeanWarnMap.keySet());
		Collections.sort(listKeys);
		for(Integer period: listKeys)
		{
			sb.append(buildCertificateTable(period, certificateInfoBeanWarnMap.get(period)));
		}

		sb.append("  </body>");
		sb.append("</html>");
		
		return(sb.toString());
	}
	
	private String buildCertificateTable(Integer period, List<CertificateInfoBean> certificateInfoBeanWarnList)
	{
		StringBuilder table = new StringBuilder();
		
		table.append("	   <table summary=\"\" border=\"1\">");
		table.append("      <thead>");
		table.append("        <tr>");
		
		if (period == 0)
		{
			table.append("          <th scope=\"col\" colspan=\"7\">Certificates expired</th>");
		}
		else
		{
			table.append("          <th scope=\"col\" colspan=\"7\">Certificates will expire in " + period + " days</th>");
		}
		table.append("        </tr>");
		table.append("        <tr>");
		table.append("	         <th scope=\"col\">" + HeaderValues.HOST.getValue() + "</th>");
		table.append("	         <th scope=\"col\">" + HeaderValues.PORT.getValue() +  "</th>");
		table.append("				<th scope=\"col\">" + HeaderValues.ISSUER.getValue() + "</th>");
		table.append("              <th scope=\"col\">" + HeaderValues.NOT_BEFORE.getValue() + "</th>");
		table.append("				<th scope=\"col\">" + HeaderValues.NOT_AFTER.getValue() + "</th>");
		if (ConfigurationBean.VALIDATION_CRL_STATUS == true)
		{
			table.append("              <th scope=\"col\">" + HeaderValues.STATUS_CRL.getValue() + "</th>");
		}
		table.append("              <th scope=\"col\">" + HeaderValues.SUBJECT.getValue() + "</th>");
		table.append("          </tr>");
		table.append("      </thead>");
		table.append("      <tbody>");
		
		
		if(certificateInfoBeanWarnList.size() == 0)
		{
			table.append("        <tr>");
			table.append("           <td colspan=\"7\">No certificates were found</td>");
			table.append("        </tr>");
		}
		else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			
			for(CertificateInfoBean ciBean: certificateInfoBeanWarnList)
			{
				table.append("         <tr>");
				table.append("           <td>" + ciBean.getHost() + "</td>");
				table.append("           <td>" + ciBean.getPort() + "</td>");
				table.append("           <td>" + ciBean.getIssuer() + "</td>");
				table.append("           <td>" + sdf.format(ciBean.getNotBefore()) + "</td>");
				table.append("           <td>" + sdf.format(ciBean.getNotAfter()) + "</td>");
				if (ConfigurationBean.VALIDATION_CRL_STATUS == true)
				{
					table.append("           <td>" + ciBean.getCrlStatus().getValue() + "</td>");
				}
				table.append("           <td>" + ciBean.getSubject() + "</td>");
				table.append("        </tr>");
			}
		}
		table.append("      </tbody>");
		table.append("    </table>");
		
		table.append("    <p></p>");
		
		return table.toString();
	}

}

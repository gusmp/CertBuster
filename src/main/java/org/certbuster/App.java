package org.certbuster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;
import org.certbuster.beans.CertificateInfoBean;
import org.certbuster.beans.ConfigurationBean;
import org.certbuster.beans.HostInfoBean;
import org.certbuster.beans.CertificateInfoBean.RESULT_CODE;
import org.certbuster.service.CertificateService;
import org.certbuster.service.ConfigService;
import org.certbuster.service.ConnectionService;
import org.certbuster.service.HostFileService;
import org.certbuster.service.MailService;
import org.certbuster.service.ReportService;

public class App
{
    private String configFile;

    public static void main(String[] args)
    {
	try
	{
	    showBanner();

	    if (args.length < 1)
	    {
		System.out.println("certBuster <PATH CONFIG FILE>");
	    }
	    else
	    {
		System.out.println("Config file " + args[0]);
		App app = new App(args[0]);
		app.run();
	    }
	}
	catch (Exception exc)
	{
	    System.out.println(exc.toString());
	}
    }

    public App(String configFile)
    {
	this.configFile = configFile;
    }

    private static void showBanner()
    {
	System.out.println("CertBuster v 0.0.1");
	System.out.println("==================");
    }

    public void run() throws IOException, ParserConfigurationException, MessagingException
    {
	ConfigService configService = new ConfigService();
	configService.load(configFile);

	HostFileService hostFileService = new HostFileService();
	hostFileService.load(ConfigurationBean.HOSTFILE);

	ConnectionService connectionService = new ConnectionService();
	List<CertificateInfoBean> certificateInfoList = new ArrayList<CertificateInfoBean>(100);
	CertificateInfoBean certificateInfoBean;

	for (HostInfoBean hbInfo : ConfigurationBean.HOSTLIST)
	{
	    for (Integer port = hbInfo.getLowerPort(); port <= hbInfo.getHigherPort(); port++)
	    {
		System.out.println("Analysing " + hbInfo.getHost() + ":" + port + "...");
		certificateInfoBean = connectionService.getCertificate(hbInfo.getHost(), port);
		if (certificateInfoBean.getResult() == RESULT_CODE.OK)
		{
		    certificateInfoList.add(certificateInfoBean);
		}
	    }
	}

	if (ConfigurationBean.VALIDATION_CRL_STATUS == true)
	{
	    CertificateService certificateService = new CertificateService();
	    certificateService.checkCrlStatus(certificateInfoList);
	}

	ReportService reportService = new ReportService();
	reportService.generateReport(certificateInfoList);

	if (ConfigurationBean.ENABLE_SEND_WARN_MAIL == true) { 
		MailService mailService = new MailService();
		mailService.sendMail(certificateInfoList);
	}
	}

}

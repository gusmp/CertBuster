package org.certbuster.service;

import java.security.cert.CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.certbuster.beans.CertificateInfoBean;

public class CertificateService 
{
	public static enum Crl_Status 
	{
		VALID("Valid"),
		REVOKED("Revoked"),
		EXPIRED("Expired"),
		NOT_VALID_YET("Not valid yet"),
		UNKNOWN("Unknown"),
		NO_CRL_DP("No CRL distribution points as available");
		
		private String value;
		
		Crl_Status(String value)
		{
			this.value = value;
		}
		
		public String getValue()
		{
			return(this.value);
		}
		
	}
	
	private List<String> getCrlDistributionPoints(byte[] crlDistributionPoints)
	{
		// As far as I know there is no support for DER encoding in JDK. And I do not want to use external libs
		// such as BC or IAIK
		final String START_CRL_DP = "http"; 
		final String END_CRL_DP = ".crl";
		
		String crldp = new String(crlDistributionPoints);
		List<String> crlDistributionPointList = new ArrayList<String>(2);
		
		int beginIdx = 0;
		int endIdx = 0;
		
		while(endIdx != -1)
		{
			beginIdx = crldp.indexOf(START_CRL_DP, endIdx);
			if (beginIdx == -1) break;
			endIdx = crldp.indexOf(END_CRL_DP, endIdx);
			endIdx += END_CRL_DP.length();
			crlDistributionPointList.add(crldp.substring(beginIdx, endIdx));
			LogService.writeLog("Found crl dp: " + crldp.substring(beginIdx, endIdx));
		}
		
		return crlDistributionPointList;
	}
	
	private Crl_Status certificateRevoked(X509Certificate certificate)
	{
		
		byte[] crlDpExtension = certificate.getExtensionValue("2.5.29.31");
		if (crlDpExtension != null)
		{
			List<String> crlDistributionPoints = getCrlDistributionPoints(certificate.getExtensionValue("2.5.29.31"));
			if (crlDistributionPoints.size() == 0)
			{
				LogService.writeLog("Crl distribution point exists but I could not find any url (not http and ended with .crl??)");
				return Crl_Status.NO_CRL_DP;
			}
			
			ConnectionService connectionService = new ConnectionService();
			for(String urlCrl : crlDistributionPoints)
			{
				CRL crl = connectionService.getCrl(urlCrl);
				if ((crl != null) && (crl.isRevoked(certificate) == true))
				{
					return Crl_Status.REVOKED;
				}
			}
			
			Date now = new Date();
			// expired
			if (certificate.getNotAfter().before(now) == true)
			{
				return Crl_Status.EXPIRED;
			}
			
			// not valid yet 
			if (certificate.getNotBefore().after(now) == true)
			{
				return Crl_Status.NOT_VALID_YET;
			}
			
			return Crl_Status.VALID;
		}
		else
		{
			LogService.writeLog("There is not crl distribution points (ext: 2.5.29.31)");
			return Crl_Status.NO_CRL_DP;
		}
	}
	
	public void checkCrlStatus(List<CertificateInfoBean> certificateList)
	{
		for(CertificateInfoBean cert : certificateList)
		{
			LogService.writeLog("Checking crl status for certificate " + cert.getHost() + ":" + cert.getPort());
			cert.setCrlStatus(certificateRevoked(cert.getSslCertificate()));
		}
	}

}

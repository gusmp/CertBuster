package org.certbuster.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.certbuster.beans.CertificateInfoBean;
import org.certbuster.beans.CertificateInfoBean.RESULT_CODE;

public class CertificateService
{
    public static enum Crl_Status
    {
	VALID("Valid"), REVOKED("Revoked"), EXPIRED("Expired"), NOT_VALID_YET("Not valid yet"), UNKNOWN("Unknown"), NO_CRL_DP("No CRL distribution points as available");

	private String value;

	Crl_Status(String value)
	{
	    this.value = value;
	}

	public String getValue()
	{
	    return (this.value);
	}

    }

    private List<List<String>> getCrlDistributionPoints(byte[] crlDistributionPoints)
    {
	List<List<String>> crlDistributionPointList = new ArrayList<List<String>>(2);

	try
	{
	    ASN1InputStream crlDPExtAsn1Stream = new ASN1InputStream(new ByteArrayInputStream(crlDistributionPoints));
	    DEROctetString crlDPExtDerObjStr = (DEROctetString) crlDPExtAsn1Stream.readObject();
	    ASN1InputStream asn1is = new ASN1InputStream(crlDPExtDerObjStr.getOctets());
	    ASN1Sequence crlDPSeq = (ASN1Sequence) asn1is.readObject();
	    for (int i = 0; i < crlDPSeq.size(); i++)
	    {
		DistributionPoint crldp = new DistributionPoint((ASN1Sequence) crlDPSeq.getObjectAt(i).toASN1Primitive());
		GeneralNames gns = (GeneralNames) crldp.getDistributionPoint().getName();
		List<String> listUrlCRL = new ArrayList<String>(gns.getNames().length);
		for (int j = 0; j < gns.getNames().length; j++)
		{
		    listUrlCRL.add(gns.getNames()[j].getName().toString());
		}

		crlDistributionPointList.add(listUrlCRL);
	    }

	    asn1is.close();
	    crlDPExtAsn1Stream.close();
	}
	catch (IOException exc)
	{
	    LogService.writeLog("CRL extension could not be parsed!");
	}

	return crlDistributionPointList;
    }

    private Crl_Status certificateRevoked(X509Certificate certificate)
    {

		byte[] crlDpExtension = certificate.getExtensionValue("2.5.29.31");
		if (crlDpExtension != null)
		{
		    List<List<String>> crlDistributionPoints = getCrlDistributionPoints(certificate.getExtensionValue("2.5.29.31"));
		    if (crlDistributionPoints.size() == 0)
		    {
		    	LogService.writeLog("Crl distribution point exists but I could not find any url (not http and ended with .crl??)");
		    	return Crl_Status.NO_CRL_DP;
		    }
	
		    ConnectionService connectionService = new ConnectionService();
		    for (List<String> crlDp : crlDistributionPoints)
		    {
		    	CRL crl = null;
		    	for (int i = 0; i < crlDp.size() && crl == null; i++)
		    	{
		    		crl = connectionService.getCrl(crlDp.get(i));
		    	}
	
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
		for (CertificateInfoBean cert : certificateList)
		{
			if (cert.getResult() == RESULT_CODE.OK) {
				LogService.writeLog("Checking crl status for certificate " + cert.getHost() + ":" + cert.getPort());
				cert.setCrlStatus(certificateRevoked(cert.getSslCertificate()));
			}
		}
    }
}

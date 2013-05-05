package org.certbuster.beans;

import java.security.cert.X509Certificate;
import java.util.Date;

import org.certbuster.service.CertificateService.Crl_Status;

public class CertificateInfoBean 
{
	private RESULT_CODE result;
	private String issuer;
	private String subject;
	private Date notBefore;
	private Date notAfter;
	
	private String host;
	private Integer port;
	private X509Certificate sslCertificate;
	private Crl_Status crlStatus;
	
	public enum RESULT_CODE {OK, ERROR, URL_WITHOUT_CERTS} 
	
	public RESULT_CODE getResult() { return result; }
	public void setResult(RESULT_CODE result) { this.result = result; }
	
	public String getIssuer() { return issuer; }
	public void setIssuer(String issuer) { this.issuer = issuer; }
	
	public String getSubject() { return subject; }
	public void setSubject(String subject) { this.subject = subject; }
	
	public Date getNotBefore() { return notBefore; }
	public void setNotBefore(Date notBefore) { this.notBefore = notBefore; }
	
	public Date getNotAfter() { return notAfter; }
	public void setNotAfter(Date notAfter) { this.notAfter = notAfter; }
	
	public String getHost() { return host; }
	public void setHost(String host) { this.host = host; }
	
	public Integer getPort() { return port; }
	public void setPort(Integer port) { this.port = port; }
	
	public X509Certificate getSslCertificate() { return sslCertificate; }
	public void setSslCertificate(X509Certificate sslCertificate) { this.sslCertificate = sslCertificate; }
	
	public Crl_Status getCrlStatus() { return crlStatus; }
	public void setCrlStatus(Crl_Status crlStatus) { this.crlStatus = crlStatus; }
	
}

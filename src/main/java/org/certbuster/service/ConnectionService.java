package org.certbuster.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.certbuster.beans.CertificateInfoBean;
import org.certbuster.beans.CertificateInfoBean.RESULT_CODE;
import org.certbuster.beans.ConfigurationBean;


public class ConnectionService
{
	private static class SavingTrustManager implements X509TrustManager 
	{

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) 
		{
		    this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers()
		{
		    throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException 
		{
		    throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException
		{
		    this.chain = chain;
		    tm.checkServerTrusted(chain, authType);
		}
	}
	
	public CertificateInfoBean getCertificate(String host, Integer port)
	{
		
		CertificateInfoBean certificateInfoBean = new CertificateInfoBean();
		certificateInfoBean.setResult(RESULT_CODE.ERROR);
			
		try
		{
			if (ConfigurationBean.USE_PROXY == true)
			{
				LogService.writeLog("Enable proxy for " + host + ":" + port);
				setProxyConfiguration();
			}
			
			// set up a temporal keystore
			KeyStore ks = KeyStore.getInstance("JKS");
			
			// get a SSLContext
			SSLContext context = SSLContext.getInstance("TLS");
			
			// create a TrustManagerFactory
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
			
			SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
			
			// initialize
			context.init(null, new TrustManager[] {tm}, null);
			
			// create a SSL socket and define properties
			SSLSocketFactory factory = context.getSocketFactory();
			SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
			socket.setSoTimeout(10000);
			
			// connect
			try 
			{
				socket.startHandshake();
				socket.close();
			} 
			catch (SSLException e) { }
			
			certificateInfoBean = new CertificateInfoBean();
			X509Certificate[] chain = tm.chain;
			if (chain == null) 
			{
			    LogService.writeLog(host + ":" + port + " is not an SSL port");
			    certificateInfoBean.setResult(RESULT_CODE.URL_WITHOUT_CERTS);
			    return(certificateInfoBean);
			}

			LogService.writeLog(host + ":" + port + " is an SSL port. Chain length: " + chain.length);
			
			certificateInfoBean.setResult(RESULT_CODE.OK);
			certificateInfoBean.setNotAfter(chain[0].getNotAfter());
			certificateInfoBean.setNotBefore(chain[0].getNotBefore());
			certificateInfoBean.setIssuer(chain[0].getIssuerDN().toString());
			certificateInfoBean.setSubject(chain[0].getSubjectDN().toString());
			certificateInfoBean.setHost(host);
			certificateInfoBean.setPort(port);
			certificateInfoBean.setSslCertificate(chain[0]);
			
		}
		catch(Exception exc)
		{
			LogService.writeLog(host + ":" + port + " " + exc.toString());
			certificateInfoBean.setResult(RESULT_CODE.ERROR);
		}
		
		return(certificateInfoBean);
	}
	
	private void setProxyConfiguration()
	{
		System.setProperty("http.proxyHost", ConfigurationBean.HTTP_PROXY_HOST);
		System.setProperty("http.proxyPort", ConfigurationBean.HTTP_PROXY_PORT);
		
		System.setProperty("https.proxyHost", ConfigurationBean.HTTP_PROXY_PORT);
		System.setProperty("https.proxyPort", ConfigurationBean.HTTP_PROXY_PORT);
		
		/*
		Add usr:pwd in "Proxy-Authorization" header (b64) for basic auth?
		String encoded = new String
		      (Base64.base64Encode(new String("username:password").getBytes()));
		uc.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
		uc.connect();
		*/
	}
	
	public X509CRL getCrl(String url)
	{
		X509CRL crl = null;
		LogService.writeLog("Downloading crl: " + url);
		try
		{
			if (ConfigurationBean.USE_PROXY == true)
			{
				LogService.writeLog("Enable proxy for " + url);
				setProxyConfiguration();
			}

			URL crlUrl = new URL(url);
			InputStream inStream = crlUrl.openConnection().getInputStream();
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			crl = (X509CRL)cf.generateCRL(inStream);
			inStream.close();
			LogService.writeLog("crl " + url + " downloaded successfully");
		}
		catch(MalformedURLException exc)
		{
			LogService.writeLog("Url " + url + " is not correct. " + exc.toString());
		}
		catch(IOException exc)
		{
			LogService.writeLog("Error downloading the crl " + url + ". " + exc.toString());
		}
		catch(CertificateException exc)
		{
			LogService.writeLog("The crl " + url +" seems not to be correct. " + exc.toString());
		}
		catch(CRLException exc)
		{
			LogService.writeLog("The crl " + url +" seems not to be correct. " + exc.toString());
		}
		
		return crl;
	}

}

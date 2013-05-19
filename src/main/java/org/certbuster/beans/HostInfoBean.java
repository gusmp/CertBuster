package org.certbuster.beans;

public class HostInfoBean
{
    private String host;
    private Integer lowerPort;
    private Integer higherPort;

    public HostInfoBean()
    {
    }

    public HostInfoBean(String host, Integer lowerPort, Integer higherPort)
    {
	this.host = host;
	this.lowerPort = lowerPort;
	this.higherPort = higherPort;
    }

    public String getHost()
    {
	return host;
    }

    public void setHost(String host)
    {
	this.host = host;
    }

    public Integer getLowerPort()
    {
	return lowerPort;
    }

    public void setLowerPort(Integer lowerPort)
    {
	this.lowerPort = lowerPort;
    }

    public Integer getHigherPort()
    {
	return higherPort;
    }

    public void setHigherPort(Integer higherPort)
    {
	this.higherPort = higherPort;
    }
}

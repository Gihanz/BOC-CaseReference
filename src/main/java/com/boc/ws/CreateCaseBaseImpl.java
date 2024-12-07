package com.boc.ws;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.annotation.Autowired;

import com.boc.service.impl.CheckSecurity;
import com.boc.service.impl.CheckSecurityService;
import com.boc.utils.PropertyReader;

/*
Created By SaiMadan on Jul 1, 2016
*/
public class CreateCaseBaseImpl 
{
	@Autowired CheckSecurity checkSecurityService;
	
	public static Logger log = Logger.getLogger("com.boc.ws.CreateCaseBaseImpl");
	static ResourceBundle configMsgBundle = ResourceBundle.getBundle("config",Locale.getDefault());
	static ResourceBundle appExceptionMsgBundle = ResourceBundle.getBundle("CaseExceptionMessages",Locale.getDefault());
	
    private static Calendar lastLog4jPropertiesReloadedOn = null;
    public static String log4jpath;
	
	public static void init()
	{
		PropertyReader property;
		try {
			property = new PropertyReader();
			Properties prop = property.loadPropertyFile();
	        log4jpath = PropertyReader.getProperty(prop, "LOGPATH");
	        if(log4jpath != null)
	        {
	            File fin = new File(log4jpath);
	            Calendar lastModCal = Calendar.getInstance();
	            lastModCal.setTimeInMillis(fin.lastModified());
	            if(lastLog4jPropertiesReloadedOn != null)
	                log.debug((new StringBuilder("Log4j property file last loaded on:[")).append(lastLog4jPropertiesReloadedOn.getTime()).append("] ").append("Log4j property file last modified on:[").append(lastModCal.getTime()).append("]").toString());
	            if(lastLog4jPropertiesReloadedOn == null || lastLog4jPropertiesReloadedOn.before(lastModCal))
	            {
	                DOMConfigurator.configure(log4jpath);
	                lastLog4jPropertiesReloadedOn = lastModCal;
	                log.debug("Reloaded the Log4j property file as it has been modified since its last loaded time");
	            }
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	private String error;
	
	public static ResourceBundle getAppExceptionMsgBundle() {
		return appExceptionMsgBundle;
	}
	public String getExcptnMesProperty(String key)
    {
		System.out.println("key is "+key);
		if(appExceptionMsgBundle != null && appExceptionMsgBundle.containsKey(key)){
			return appExceptionMsgBundle.getString(key).intern();
		} else{
			return "Error in processing your request";
		}
    }
	
	public String getExcptnMesProperty(String key,Object[] params)
    {
		try
		{
		if(appExceptionMsgBundle != null && appExceptionMsgBundle.containsKey(key)){
			
			return MessageFormat.format(appExceptionMsgBundle.getString(key).intern(), params);
		} else{
			return "Error in processing your request";
		}
		}catch(Exception e){
			return "Error in processing your request";
		}
    }
	public static void setAppExceptionMsgBundle(ResourceBundle appExceptionMsgBundle) {
		CreateCaseBaseImpl.appExceptionMsgBundle = appExceptionMsgBundle;
	}
	
}



package com.boc.ws;

import org.apache.commons.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boc.response.ResponseBean;
import com.boc.service.exceptions.BSLException;
import com.boc.service.impl.CheckSecurity;

/*
Created By SaiMadan on Jul 1, 2016

*/
@RestController
public class CreateCaseWsImpl extends CreateCaseBaseImpl 
{

	public static Logger log = Logger.getLogger("com.boc.ws.CreateCaseWsImpl");
	@RequestMapping(value="/test")
	public String testService()
	{
		return "it is working fine";
	}

	@RequestMapping(value="/CreatePersonalLoan", method=RequestMethod.POST,  produces="application/json", consumes="application/json")
	public ResponseBean createCase(@RequestHeader(value="token") String token,@RequestBody Object caseParameters)
	{
		ResponseBean responseBean = null;
		responseBean = new ResponseBean();
		log.info("******"+caseParameters);
		String referenceNumber = null;
		
		try
		{
		 referenceNumber = checkSecurityService.getCreateCase(token,caseParameters,configMsgBundle.getString("CMBRANCHCODE"),configMsgBundle.getString("CMPRODUCTCODE"),configMsgBundle.getString("RESTURL"),configMsgBundle.getString("CMPERSONALCASETYPE"));
		 responseBean.setErrorCode("0");
		 responseBean.setReferenceNumber(referenceNumber);
		}
		catch(BSLException bsle)
		{
			responseBean.setErrorCode("1");
			bsle.printStackTrace();
			log.error(bsle.getMessage());
			log.error("e.getMessage() "+getExcptnMesProperty(bsle.getMessage()));
			responseBean.setErrorDescription(getExcptnMesProperty(bsle.getMessage()));
			//throw new BSLException(getExcptnMesProperty(e.getMessage()));
			return responseBean;
		}
		catch(Exception e)
		{
			responseBean.setErrorCode("1");
			e.printStackTrace();
			log.error(e.getMessage()+""+getExcptnMesProperty(e.getMessage()));
			String exceptionMsg = getExcptnMesProperty(e.getMessage());
			log.error("exceptionMsg "+exceptionMsg);
			responseBean.setErrorDescription(exceptionMsg);
			//throw new BSLException(getExcptnMesProperty(e.getMessage()));
			return responseBean;
		}
		catch(Throwable e)
		{
			responseBean.setErrorCode("1");
			e.printStackTrace();
			log.error(e.getMessage());
			log.error("e.getMessage() "+e.getMessage());
			responseBean.setErrorDescription(getExcptnMesProperty(e.getMessage()));
		}
		return responseBean;
	}
	
	@RequestMapping(value="/CreateHomeLoan", method=RequestMethod.POST,  produces="application/json", consumes="application/json")
	public ResponseBean createHomeLoanCase(@RequestHeader(value="token") String token,@RequestBody Object caseParameters)
	{
		ResponseBean responseBean = null;
		responseBean = new ResponseBean();
		log.info("******"+caseParameters);
		String referenceNumber = null;
		try
		{
		 referenceNumber = checkSecurityService.getCreateCase(token,caseParameters,configMsgBundle.getString("CMBRANCHCODE"),configMsgBundle.getString("CMPRODUCTCODE"),configMsgBundle.getString("RESTURL"),configMsgBundle.getString("CMHOMECASETYPE"));
		 responseBean.setErrorCode("0");
		 responseBean.setReferenceNumber(referenceNumber);
		}
		catch(BSLException bsle)
		{
			responseBean.setErrorCode("1");
			bsle.printStackTrace();
			log.error(bsle.getMessage());
			log.error("e.getMessage() "+getExcptnMesProperty(bsle.getMessage()));
			responseBean.setErrorDescription(getExcptnMesProperty(bsle.getMessage()));
			//throw new BSLException(getExcptnMesProperty(e.getMessage()));
			return responseBean;
		}
		catch(Exception e)
		{
			responseBean.setErrorCode("1");
			e.printStackTrace();
			log.error(e.getMessage()+""+getExcptnMesProperty(e.getMessage()));
			String exceptionMsg = getExcptnMesProperty(e.getMessage());
			log.error("exceptionMsg "+exceptionMsg);
			responseBean.setErrorDescription(exceptionMsg);
			//throw new BSLException(getExcptnMesProperty(e.getMessage()));
			return responseBean;
		}
		catch(Throwable e)
		{
			responseBean.setErrorCode("1");
			e.printStackTrace();
			log.error(e.getMessage());
			log.error("e.getMessage() "+e.getMessage());
			responseBean.setErrorDescription(getExcptnMesProperty(e.getMessage()));
		}
		return responseBean;
	}
	
	@RequestMapping(value="/UpdateCaseProperty", method=RequestMethod.POST,  produces="application/json", consumes="application/json")
	public String updateCaseProperty(@RequestParam("caseType") String caseType,@RequestBody Object caseParameters)
	{
		String caseTypeName=null;
		String mesg = null;
		try
		{
		if(caseType.equalsIgnoreCase("PersonalLoan"))
			caseTypeName = configMsgBundle.getString("CMPERSONALCASETYPE");
		else if(caseType.equalsIgnoreCase("HomeLoan"))
			caseTypeName = configMsgBundle.getString("CMHOMECASETYPE");
		mesg = checkSecurityService.updateCaseProperty(caseTypeName,configMsgBundle.getString("CMREFERENCENO"),caseParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error(e.getMessage()+" "+e.fillInStackTrace());
			mesg="Exception Occured while updating case property";
			return mesg;
		}
		return mesg;
	}
	
}

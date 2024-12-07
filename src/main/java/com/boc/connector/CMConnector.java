

package com.boc.connector;

import java.util.Date;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.apache.log4j.Logger;

import com.boc.utils.CripUtils;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.ibm.casemgmt.api.Case;
import com.ibm.casemgmt.api.CaseType;
import com.ibm.casemgmt.api.DeployedSolution;
import com.ibm.casemgmt.api.constants.ModificationIntent;
import com.ibm.casemgmt.api.context.CaseMgmtContext;
import com.ibm.casemgmt.api.context.P8ConnectionCache;
import com.ibm.casemgmt.api.context.SimpleP8ConnectionCache;
import com.ibm.casemgmt.api.context.SimpleVWSessionCache;
import com.ibm.casemgmt.api.objectref.ObjectStoreReference;
import com.ibm.casemgmt.api.properties.CaseMgmtProperties;
import com.ibm.casemgmt.api.properties.CaseMgmtProperty;

/*
Created By SaiMadan on Jun 27, 2016
*/
public class CMConnector 
{
	public static Logger log = Logger.getLogger("com.boc.connector.CMConnector");
	private UserContext uc;
	private boolean userSubjectPushed;
	private Connection conn;
	private CaseMgmtContext origCmctx;
	
	public static void main(String a[])
	{
		CMConnector connect = new CMConnector();
		connect.getCMConnection();
		String accountNo;
		try {
			//accountNo = connect.getAccountNo("12345");
			//System.out.println("accountNo is "+accountNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		connect.releaseCMConnection();
	}
	
	public ResourceBundle getResourceBundle()
	{
		ResourceBundle rsbundle = ResourceBundle.getBundle("config");
		return rsbundle;
	}
	
	public Connection getCMConnection()
	{
		ResourceBundle rs = getResourceBundle();
		String ceURI = rs.getString("CEURI"); //"http://DRDMTEST01:9080/wsi/FNCEWS40MTOM/";
		String userId =  rs.getString("CMUSERID"); //"p8admin_test";
		String encryptedPassword =rs.getString("CMPASSWORD");  //"boc@123";
		String stanza = rs.getString("CMSTANZA") ;//"FileNetP8WSI";
		
		String password = CripUtils.decryptStr(encryptedPassword);
		
		P8ConnectionCache connCache = new SimpleP8ConnectionCache();
        conn = connCache.getP8Connection(ceURI);
        Subject subject = UserContext.createSubject(conn, userId,password, stanza);
        UserContext uc = UserContext.get();
        uc.pushSubject(subject);
        userSubjectPushed=true;
        Locale origLocale = uc.getLocale();
        uc.setLocale(Locale.ENGLISH);
        origCmctx = CaseMgmtContext.set(new CaseMgmtContext(new SimpleVWSessionCache(), connCache));
    return conn;
	}
	
	public void releaseCMConnection()
	{
		if (origCmctx != null && uc != null && userSubjectPushed)
        {
			CaseMgmtContext.set(origCmctx);
			Locale origLocale = uc.getLocale();
            uc.setLocale(origLocale);
            uc.popSubject();
            userSubjectPushed = false;
	        conn=null;
	        origCmctx=null;
	        System.out.println("Connection Released");
        }
	}
	
	//public String createCase()
	public String createCase(Object caseParametersBean,String referenceNumber,String caseType)
	{
		String newCaseIdentifier=null;
		Connection conn = getCMConnection();
		ResourceBundle rs = getResourceBundle();
		String solutionName = rs.getString("CMSOLUTIONNAME") ;//"Loan Application";
		//String caseTypename = rs.getString("CMPERSONALCASETYPE") ;//"BOC_PersonalLoan";
		String caseTypename = caseType;
		String targetOsname = rs.getString("CMTARGETOS") ;//"fnObjStr";
		String domainName = rs.getString("CMDOMAINNAME") ;//"fnp8domain";
		String dateOfBirth = rs.getString("CMDATEOFBIRTH");
		String dateOfBirthAF = rs.getString("CMDATEOFBIRTHAF");
		
		String dateFormat = rs.getString("DateFormat");
		//Locale origLocale = uc.getLocale();
		try
		{
			Domain domain = Factory.Domain.fetchInstance(conn, domainName, null);
	    	ObjectStore os = Factory.ObjectStore.fetchInstance(domain, targetOsname,null);
	        ObjectStoreReference osRef = new ObjectStoreReference(os);
	        DeployedSolution someSolution = DeployedSolution.fetchInstance(osRef, solutionName); 
	        log.info("Solution Name "+someSolution.getSolutionName());
	        CaseType caseTypeInstance = CaseType.fetchInstance(osRef, caseTypename);
	            {
	            	Case pendingCase = Case.createPendingInstanceFetchDefaults(caseTypeInstance);
	            	CaseMgmtProperties properties =  pendingCase.getProperties();
	            	
	            	JSONObject jObject = new JSONObject(caseParametersBean);
	            	for(Object key:jObject.keySet())
	            	{
	            		 String keyStr = (String)key;
	            		 Object keyvalue = null;
	            		 if(keyStr.equalsIgnoreCase(dateOfBirth) || keyStr.equalsIgnoreCase(dateOfBirthAF))
	            		{
	            			 DateFormat df = new SimpleDateFormat(dateFormat);
	            			 Date dob = (Date) df.parse((String)jObject.get(keyStr));
	            			 keyvalue = dob;
	            		}
	            		else 
	            			 keyvalue = jObject.get(keyStr);
	            	     //keyStr=keyStr.replaceFirst("b", "B");
	            	     log.info("@@@@@key: "+ keyStr + "@@@ value: " + keyvalue);
	            	     if(!keyStr.startsWith("_") && !keyStr.equalsIgnoreCase("class"))
	            	    	 properties.putObjectValue(keyStr, keyvalue);
	            	     
	            	     properties.putObjectValue(rs.getString("CMREFERENCENO"), referenceNumber);
	            	}
	            	
	            	pendingCase.save(RefreshMode.REFRESH, null, ModificationIntent.MODIFY);
	            	newCaseIdentifier = pendingCase.getIdentifier();
	            	
	            	log.info("newCaseIdentifier is "+newCaseIdentifier);
	            }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("JSONException is "+e.fillInStackTrace());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error(e.getMessage()+" "+e.fillInStackTrace());
		}
		finally {
			releaseCMConnection();
	    }
		return newCaseIdentifier;
	}
	
	
	/*public String getAccountNo(String referenceNumber) throws Exception
	{
		Connection conn = getCMConnection();		
		ResourceBundle rs = getResourceBundle();
		String solutionName = rs.getString("CMSOLUTIONNAME") ;//"Loan Application";
		String caseTypename = rs.getString("CMPERSONALCASETYPE") ;//"BOC_PersonalLoan";
		String targetOsname = rs.getString("CMTARGETOS") ;//"fnObjStr";
		String domainName = rs.getString("CMDOMAINNAME") ;//"fnp8domain";
		
		String accountNo = null;
		Domain domain = Factory.Domain.fetchInstance(conn, domainName, null);
    	ObjectStore os = Factory.ObjectStore.fetchInstance(domain, targetOsname,null);
    	ObjectStoreReference osRef = new ObjectStoreReference(os);
    	
    	
    	
    	DeployedSolution deployedSolution = DeployedSolution.fetchInstance(osRef, solutionName); 
        System.out.println("Solution Name "+deployedSolution.getSolutionName());
        List<CaseType> caseTypes = deployedSolution.getCaseTypes();
        for(CaseType ct : caseTypes) {
            System.out.println(ct.getName());
            if(ct.getName().equalsIgnoreCase(caseTypename))
            {
            	Id caseId = ct.getId();
            	CaseType caseTypeInstance =  ct.fetchInstance(osRef, caseTypename);
            	
            	System.out.println("caseId is "+caseId);
            	
            	searchCase(os,solutionName,caseTypename);
            }
        }
		
		//String collectionName="0072";
		//referenceNumber="12345";
		//String searchString = "SELECT t.[This],t.[FolderName], t.[LastModifier], t.[DateLastModified], t.[CmAcmCaseTypeFolder], t.[CmAcmCaseState],t.[CmAcmCaseIdentifier], t.[DateCreated], t.[Creator], t.[Id], t.[ContainerType],t.[LockToken], t.[LockTimeout], t.[ClassDescription], t.[DateLastModified], t.[FolderName] FROM [CmAcmCaseFolder] t where t.[CmAcmCaseIdentifier] LIKE '%%' AND t.[CmAcmParentSolution]=Object('/IBM Case Manager/Solution Deployments/"+solutionName+"') AND t.[CollectionName] LIKE '"+collectionName+"' ORDER BY t.[DateCreated] DESC";
		//String searchString = "SELECT t.[This],t.[FolderName], t.[LastModifier], t.[DateLastModified], t.[CmAcmCaseTypeFolder], t.[CmAcmCaseState],t.[CmAcmCaseIdentifier], t.[DateCreated], t.[Creator], t.[Id], t.[ContainerType],t.[LockToken], t.[LockTimeout], t.[ClassDescription], t.[DateLastModified], t.[FolderName] FROM [CmAcmCaseFolder] t where t.[CmAcmCaseIdentifier] LIKE '%%' AND t.[CmAcmParentSolution]=Object('/IBM Case Manager/Solution Deployments/"+solutionName+"')  ORDER BY t.[DateCreated] DESC";
		String searchString = "SELECT * FROM "+caseTypename+" WHERE (TST_ReferenceNumber = '" + referenceNumber + "' AND CmAcmCaseState = 2) ";
		System.out.println("searchString is "+searchString);
		SearchSQL sqlObject = new SearchSQL (searchString);
				// Executes the content search
				SearchScope searchScope = new SearchScope(os);
				RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
				
				Iterator iter = rowSet.iterator();
				if (iter.hasNext()) {
				RepositoryRow row = (RepositoryRow) iter.next();
				String folderName = row.getProperties().get("FolderName").getStringValue();
				String caseInstanceFolderName = folderName;
				System.out.println("caseInstanceFolderName is "+caseInstanceFolderName);
				Id id = row.getProperties().get("Id").getIdValue();
				System.out.println("Id is "+id.toString());
				Case caseObj =  Case.fetchInstance(osRef, id, null, null);
				CaseMgmtProperties properties =  caseObj.getProperties();
				List<CaseMgmtProperty> propertiesLst = properties.asList();
				if(propertiesLst !=null)
				{
					for(CaseMgmtProperty property:propertiesLst)
					{
						System.out.println("property Name is "+property.getDisplayName()+" "+property.getValue());
						if(property.getDisplayName().equalsIgnoreCase("AccountNo"))
								{
									accountNo = (String) property.getValue();
								}
					}
						
				}
			}
		releaseCMConnection();
		return accountNo;
	}*/
	
	public void updateLoanActId(String referenceNumber,String caseTypeName,String proprtyName,String propertyValue) throws Exception
	{
		Connection conn = getCMConnection();		
		ResourceBundle rs = getResourceBundle();
		HashMap  paramterValues = null;
		String solutionName = rs.getString("CMSOLUTIONNAME") ;//"Loan Application";
		String caseTypename = caseTypeName;//rs.getString("CMPERSONALCASETYPE") ;//"BOC_PersonalLoan";
		String targetOsname = rs.getString("CMTARGETOS") ;//"fnObjStr";
		String domainName = rs.getString("CMDOMAINNAME") ;//"fnp8domain";
		String propReferenceNumber = rs.getString("CMREFERENCENO");
		//String propLaonActId = rs.getString("LOANACTID");
		try
		{
			Domain domain = Factory.Domain.fetchInstance(conn, domainName, null);
	    	ObjectStore os = Factory.ObjectStore.fetchInstance(domain, targetOsname,null);
	    	ObjectStoreReference osRef = new ObjectStoreReference(os);
	    	String searchString = "SELECT * FROM "+caseTypename+" WHERE ("+propReferenceNumber+" = '" + referenceNumber + "' AND CmAcmCaseState = 2) ";
	    	log.info("updateLoanActId:searchString is "+searchString);
			SearchSQL sqlObject = new SearchSQL (searchString);
					// Executes the content search
			SearchScope searchScope = new SearchScope(os);
			RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
			
			Iterator iter = rowSet.iterator();
			if (iter.hasNext()) {
			RepositoryRow row = (RepositoryRow) iter.next();
			String folderName = row.getProperties().get("FolderName").getStringValue();
			String caseInstanceFolderName = folderName;
			log.info("updateLoanActId:caseInstanceFolderName is "+caseInstanceFolderName);
			Id id = row.getProperties().get("Id").getIdValue();
			Case caseObj =  Case.fetchInstance(osRef, id, null, null);
			CaseMgmtProperties taskProps = caseObj.getProperties();
			taskProps.putObjectValue(proprtyName, propertyValue);
			//#Added by Vikshith : After setting the loanActId property Case object was not saving so caseObj.save(RefreshMode.REFRESH, null, null); added to save case object.
			log.info("Case object saved.");
			caseObj.save(RefreshMode.REFRESH, null, null);
			log.info("updateLoanActId:"+ proprtyName +"Property update done succesfully");
			}
		}
		catch(Exception e)
		{
			log.error("Exception occured "+e.fillInStackTrace());
			throw new Exception(e);
		}
		finally
		{
			releaseCMConnection();
		}
	}
	
	public String updateCMProperty(String referenceNumber,String caseTypeName,HashMap propertyMap) throws Exception
	{
		String result = null;
		Connection conn = getCMConnection();		
		ResourceBundle rs = getResourceBundle();
		HashMap  paramterValues = null;
		String solutionName = rs.getString("CMSOLUTIONNAME") ;//"Loan Application";
		String caseTypename = caseTypeName;//rs.getString("CMPERSONALCASETYPE") ;//"BOC_PersonalLoan";
		String targetOsname = rs.getString("CMTARGETOS") ;//"fnObjStr";
		String domainName = rs.getString("CMDOMAINNAME") ;//"fnp8domain";
		String propReferenceNumber = rs.getString("CMREFERENCENO");
		try
		{
			Domain domain = Factory.Domain.fetchInstance(conn, domainName, null);
	    	ObjectStore os = Factory.ObjectStore.fetchInstance(domain, targetOsname,null);
	    	ObjectStoreReference osRef = new ObjectStoreReference(os);
	    	String searchString = "SELECT * FROM "+caseTypename+" WHERE ("+propReferenceNumber+" = '" + referenceNumber + "' AND CmAcmCaseState = 2) ";
	    	log.info("updateLoanActId:searchString is "+searchString);
			SearchSQL sqlObject = new SearchSQL (searchString);
					// Executes the content search
			SearchScope searchScope = new SearchScope(os);
			RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
			
			Iterator iter = rowSet.iterator();
			if (iter.hasNext()) {
			RepositoryRow row = (RepositoryRow) iter.next();
			String folderName = row.getProperties().get("FolderName").getStringValue();
			String caseInstanceFolderName = folderName;
			log.info("updateLoanActId:caseInstanceFolderName is "+caseInstanceFolderName);
			Id id = row.getProperties().get("Id").getIdValue();
			Case caseObj =  Case.fetchInstance(osRef, id, null, null);
			CaseMgmtProperties taskProps = caseObj.getProperties();
			if(null!=propertyMap)
			{
				Set keyset = propertyMap.keySet();
				Iterator propertyIter = keyset.iterator();
				while(propertyIter.hasNext())
				{
					String key = (String)propertyIter.next();
					Object value = (Object)propertyMap.get(key);
					taskProps.putObjectValue(key, value);
				}
			}
			result = "Property update done succesfully";
			log.info("updateLoanActId:LaonActNumber Property update done succesfully");
			}
			else
			{
				result = "Case ReferenceNumber "+referenceNumber+" not found to update case property";
			}
		}
		catch(Exception e)
		{
			log.error("Exception occured "+e.fillInStackTrace());
			throw new Exception(e);
		}
		finally
		{
			releaseCMConnection();
		}
		return result;
	}
	
}

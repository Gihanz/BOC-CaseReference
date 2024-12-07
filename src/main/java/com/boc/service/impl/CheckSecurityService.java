

package com.boc.service.impl;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.json.JSONArray;
import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boc.connector.CMConnector;
import com.boc.dao.ProductDao;
import com.boc.dao.impl.ProductDaoImpl;
import com.boc.service.exceptions.BSLException;

import sun.net.www.http.HttpClient;

/*
Created By SaiMadan on Jul 1, 2016
*/

@Service
public class CheckSecurityService  implements CheckSecurity
{
	@Autowired private ProductDao productDao;
	public static Logger log = Logger.getLogger("com.boc.service.impl.CheckSecurityService");
	
	@Override
	@Transactional(value="transactionManager",readOnly=true)
	public String getCreateCase(String token,Object caseParameters,String branchCodeKey,String productCodeKey,String restURL,String caseType) throws BSLException,Exception
	{
		String referenceNumber = null;
			String var= null;
			JSONObject jObject;
			try {
				jObject = new JSONObject(caseParameters);
				if(jObject.getString(branchCodeKey).isEmpty())
				{
					log.error("Branch Code value is empty, sent as input from web");
					throw new BSLException("er.db.branchCodeNotFound");
				}
				if(jObject.getString(productCodeKey).isEmpty())
				{
					log.error("Product Name value is empty, sent as input from web");
					throw new BSLException("er.db.productNotFound");
				}
				String branchCode = jObject.getString(branchCodeKey);
				if(null==branchCode)
				{
					log.error("Branch Code value is null, sent as input from web");
					throw new BSLException("er.db.branchCodeNull");
				}
				String productCode = jObject.getString(productCodeKey);
				if(null==productCode)
				{
					log.error("Product Name value is null, sent as input from web");
					throw new BSLException("er.db.productNull");
				}
				//referenceNumber = getReferenceNumber(branchCode,productCode,restURL);
				String productCodeDB = productDao.getProductCodebyName(productCode);
				if(null==productCodeDB)
					throw new BSLException("er.db.productCodeNotFoundForReferenceNo");
				Calendar cal = Calendar.getInstance();
				int years = cal.get(cal.YEAR);
				int refNo = productDao.getReferenceNo(productCodeDB,String.valueOf(years));
				String refNoStr = String.valueOf(refNo);
				String appendProductCode =  leftPad(productCodeDB,5,'0');
				String appendbranchCode = leftPad(branchCode,4,'0');
				String appendedRefNo = leftPad(refNoStr,6,'0');
				referenceNumber = appendbranchCode+appendProductCode+String.valueOf(years)+appendedRefNo;
				if(null==referenceNumber)
				{
					log.error("Unable to generate reference number,either Branch Code or Product name is invalid ");
					throw new BSLException("er.db.referenceNumberNotFound");
				}
				log.info("referenceNumber obtained is "+referenceNumber);
				CMConnector cmConnector = new CMConnector();
				log.info("caseParameters are "+caseParameters);
				var = cmConnector.createCase(caseParameters,referenceNumber,caseType);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				log.error(e.fillInStackTrace());
				e.printStackTrace();
				log.error("Either Branch Code or ProductName is not sent as input from web");
				throw new BSLException("er.db.productbranchCodeJSONNotFound");
			}
			catch(BSLException e)
			{
				
				e.printStackTrace();
				log.error(e.fillInStackTrace());
				throw new BSLException(e.getMessage());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log.error(e.fillInStackTrace());
				throw e;
			}
			return referenceNumber;
	}
	
	public String updateCaseProperty(String caseTypeName,String referenceNumberKey,Object caseParameters) throws BSLException,Exception
	{
		JSONObject jObject;
		String referenceNumber=null;
		String mesgStatus;
		try
		{
		jObject = new JSONObject(caseParameters);
		if(jObject.getString(referenceNumberKey).isEmpty())
		{
			log.error("referenceNumberKey value is empty, sent as input from web");
			throw new BSLException("er.db.referenceNumberNotExist");
		}
		else
			referenceNumber = jObject.getString(referenceNumberKey);
		

		String caseParamaetersStr = jObject.toString();
		HashMap<String, Object> jsonPropertyMap = (HashMap<String, Object>) jsonString2Map(caseParamaetersStr);
		//HashMap<String, String> jsonPropertyMap = jsonToMap(caseParamaetersStr);
		CMConnector cmConnector = new CMConnector();
		mesgStatus = cmConnector.updateCMProperty(referenceNumber, caseTypeName, jsonPropertyMap);
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.fillInStackTrace());
			e.printStackTrace();
			log.error("Exception occured while converting json to map");
			mesgStatus = "Property Update Failed";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error(e.fillInStackTrace());
			throw e;
		}
		return mesgStatus;
	}
	
	
	
	public static HashMap<String, String> jsonToMap(String t) throws JSONException {

        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = jObject.getString(key); 
            map.put(key, value);

        }

        System.out.println("json : "+jObject);
        System.out.println("map : "+map);
        return map;
    }
	public static Map<String, Object> jsonString2Map( String jsonString ) throws JSONException{
        Map<String, Object> keys = new HashMap<String, Object>(); 

        JSONObject jsonObject = new JSONObject( jsonString ); // HashMap
        Iterator<?> keyset = jsonObject.keys(); // HM

        while (keyset.hasNext()) {
            String key =  (String) keyset.next();
            Object value = jsonObject.get(key);
            System.out.print("\n Key : "+key);
            if ( value instanceof JSONObject ) {
                System.out.println("Incomin value is of JSONObject : ");
                keys.put( key, jsonString2Map( value.toString() ));
            }else if ( value instanceof JSONArray) {
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                //JSONArray jsonArray = new JSONArray(value.toString());
                keys.put( key, jsonArray2List( jsonArray ));
            } else {
                //keyNode( value);
                keys.put( key, value );
            }
        }
        return keys;
    }
	
	public static List<Object> jsonArray2List( JSONArray arrayOFKeys ) throws JSONException{
        System.out.println("Incoming value is of JSONArray : =========");
        List<Object> array2List = new ArrayList<Object>();
        for ( int i = 0; i < arrayOFKeys.length(); i++ )  {
            if ( arrayOFKeys.opt(i) instanceof JSONObject ) {
                Map<String, Object> subObj2Map = jsonString2Map(arrayOFKeys.opt(i).toString());
                array2List.add(subObj2Map);
            }else if ( arrayOFKeys.opt(i) instanceof JSONArray ) {
                List<Object> subarray2List = jsonArray2List((JSONArray) arrayOFKeys.opt(i));
                array2List.add(subarray2List);
            }else {
                //keyNode( arrayOFKeys.opt(i) );
                array2List.add( arrayOFKeys.opt(i) );
            }
        }
        return array2List;      
    }
	
	public static String leftPad(String originalString, int length,
	         char padCharacter) {
	      String paddedString = originalString;
	      while (paddedString.length() < length) {
	         paddedString = padCharacter + paddedString;
	      }
	      return paddedString;
	   }
	
}

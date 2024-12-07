

package com.boc.service.impl;

import com.boc.service.exceptions.BSLException;

/*
Created By SaiMadan on Sep 20, 2016
*/
public interface CheckSecurity 
{
	public String getCreateCase(String token,Object caseParameters,String branchCodeKey,String productCodeKey,String restURL,String caseType) throws BSLException,Exception;
	public String updateCaseProperty(String caseTypeName,String referenceNumberKey,Object caseParameters) throws BSLException,Exception;
}

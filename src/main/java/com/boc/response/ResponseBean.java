

package com.boc.response;

/*
Created By SaiMadan on Jul 11, 2016
*/
public class ResponseBean 
{
String ErrorCode;
String ErrorDescription;
String referenceNumber;
public String getErrorCode() {
	return ErrorCode;
}
public void setErrorCode(String errorCode) {
	ErrorCode = errorCode;
}
public String getErrorDescription() {
	return ErrorDescription;
}
public void setErrorDescription(String errorDescription) {
	ErrorDescription = errorDescription;
}
public String getReferenceNumber() {
	return referenceNumber;
}
public void setReferenceNumber(String referenceNumber) {
	this.referenceNumber = referenceNumber;
}

}

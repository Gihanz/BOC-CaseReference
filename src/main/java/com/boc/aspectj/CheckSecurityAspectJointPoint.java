

package com.boc.aspectj;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.boc.service.exceptions.BSLException;
import com.boc.utils.CripUtils;

/*
Created By SaiMadan on Jul 1, 2016
*/
@Aspect
public class CheckSecurityAspectJointPoint 
{
	public static Logger log = Logger.getLogger("com.boc.aspectj.CheckSecurityAspectJointPoint");
	/*@Before("execution(public String com.boc.service.impl..get*(*,*,*))")
	public void loggingAdvice(JoinPoint joinPoint){
		System.out.println("Before running loggingAdvice on method="+joinPoint.toString());
		//Array Arrays.toString(joinPoint.getArgs())
		System.out.println("Agruments Passed=" + Arrays.toString(joinPoint.getArgs()));
		
	}*/
	
	 /*@AfterReturning(pointcut = "execution(* com.boc.service.impl.isTokenValid(..))", returning= "result")
	 public void getNameReturningAdvice(JoinPoint joinPoint,String returnString){
			System.out.println("getNameReturningAdvice executed. Returned String="+returnString);
			System.out.println("Agruments Passed=" + Arrays.toString(joinPoint.getArgs()));
		}*/
	
	@Around("execution(public String com.boc.service.impl..get*(*,*,*,*,*,*))")
	public Object employeeAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
		Object value = null;
		boolean tokenValid = false;
		Object[] paramArry = proceedingJoinPoint.getArgs();
		try 
		{
			ResourceBundle configMsgBundle = ResourceBundle.getBundle("config");
			String tokenKey =  configMsgBundle.getString("TokenKey");
			String token = (String)paramArry[0];
			//String min = String.valueOf(toNearestWholeMinute());
			//String decyptToken = CripUtils.encryptStr(tokenKey+min);
			String decyptToken = CripUtils.encryptStr(tokenKey);
			log.info("token from request is "+token);
			log.info("Decrypted Token "+decyptToken);
			if(decyptToken.equalsIgnoreCase(token))
			{
				tokenValid=true;
				log.info("tokenValid flag is "+tokenValid);
			}
			/*if(!tokenValid) 
			{
				min = String.valueOf(toNearestWholeMinute()); //Trying for another time with nearest minute
				decyptToken = CripUtils.encryptStr(tokenKey+min);
				log.info("Second Minute roundOff");
				if(decyptToken.equalsIgnoreCase(token))
				{
					tokenValid=true;
				}
			}	*/		
			if(tokenValid)
			{
				value = proceedingJoinPoint.proceed();
			}
			else
			{
				
				value="You are not Authorized to add case";
				log.info("Oh,You are not Authorized to add case");
				throw new BSLException("er.db.authenticateException");
			}
			log.info("After invoking getName() method. Return value="+value);
			return value;
		}
		finally
		{
			
		}
		
		/*catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}*/

	}
	
	/*static Date toNearestWholeMinute() 
	{
		Calendar c = new GregorianCalendar();
		Date d = c.getTime();
        c.setTime(d);

        if (c.get(Calendar.SECOND) >= 30)
            c.add(Calendar.MINUTE, 1);

        c.set(Calendar.SECOND, 0);

        return c.getTime();
    }*/
	
	public static int toNearestWholeMinute() {
        Calendar c = new GregorianCalendar();
        //Date d = c.getTime();
        Date d = new Date();
        c.setTime(d);

        if (c.get(Calendar.SECOND) >= 30)
            c.add(Calendar.MINUTE, 1);

        c.set(Calendar.SECOND, 0);

        return c.get(Calendar.MINUTE);
    }
	
}

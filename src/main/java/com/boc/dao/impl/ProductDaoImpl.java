package com.boc.dao.impl;

/*
Create By SaiMadan on Jun 8, 2016
*/
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.jdbc.ReturningWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.boc.dao.ProductDao;

@Repository
public class ProductDaoImpl  extends abstractWFConfigdao implements ProductDao {
	private static Logger log =LoggerFactory.getLogger(ProductDaoImpl.class);
	
	
	public String getProductCodebyName(String productName)
	{
		String productCode = null;
		String productIdQry = null;
		try
		{
			productIdQry = "Select productBase.productCode from ProductBase productBase where productBase.productName=:productName";
			Query qry = session().createQuery(productIdQry).setString("productName", productName);
			List<String> lstProduct  = qry.list();
			System.out.println("^^^^^^^^^^^^^^"+lstProduct.size());
			if(lstProduct !=null && lstProduct.size() > 0)
			{
				productCode = lstProduct.get(0);
			}
		}
		catch(HibernateException hex)
		{
			hex.printStackTrace();
			log.error("Error ", hex.fillInStackTrace());
			log.error(hex.getMessage());
		}
		return productCode;
	}
	
	public int getReferenceNo(final String productName,final String years)
	{
		int referenceNo = 0;
		try
		{
			referenceNo = session().doReturningWork(
			new ReturningWork<Integer>() {
	
				@Override
				public Integer execute(Connection conn) throws SQLException {
					// TODO Auto-generated method stub
					CallableStatement callableStatement = conn.prepareCall("{Call WFCONFIG.GET_SEQUENCE_NUMBER(?,?,?)}");
					callableStatement.setString(1, productName);
					callableStatement.setString(2, years);
					callableStatement.registerOutParameter(3, new Integer(1));
					callableStatement.execute();
					log.info("OUTPUT IS "+callableStatement.getObject(3));
			        int seqNo = Integer.parseInt((String) callableStatement.getObject(3));
					return seqNo;
				}
				
			});
			log.info("referenceNo is "+referenceNo);
		}
		catch(HibernateException hex)
		{
			hex.printStackTrace();
			log.error(hex.getMessage());
			log.error("Error ", hex.fillInStackTrace());
		}
		return referenceNo;
	}

	
}

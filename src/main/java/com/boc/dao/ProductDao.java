package com.boc.dao;

public interface ProductDao {

	public String getProductCodebyName(String productName);
	public int getReferenceNo(final String productName,final String years);
	
	
}

package com.boc.dao.impl;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class abstractWFConfigdao {

	@Resource
	private SessionFactory sessionFactory;
	
	protected Session session()
	{
		return sessionFactory.getCurrentSession();
	}
	
}

package com.boc.model;
// Generated Jun 10, 2016 11:13:38 AM by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * ProfessionList generated by hbm2java
 */
@Entity
@Table(name = "PROFESSION_LIST", schema = "WFCONFIG", uniqueConstraints = @UniqueConstraint(columnNames = "PROFESSION_TYPE"))
public class ProfessionList implements java.io.Serializable {

	private int professionId;
	private String professionType;
	private Set<ProfessionProductMapping> professionProductMappings = new HashSet<ProfessionProductMapping>(0);

	public ProfessionList() {
	}

	public ProfessionList(int professionId, String professionType) {
		this.professionId = professionId;
		this.professionType = professionType;
	}

	public ProfessionList(int professionId, String professionType,
			Set<ProfessionProductMapping> professionProductMappings) {
		this.professionId = professionId;
		this.professionType = professionType;
		this.professionProductMappings = professionProductMappings;
	}

	@Id

	@Column(name = "PROFESSION_ID", unique = true, nullable = false)
	public int getProfessionId() {
		return this.professionId;
	}

	public void setProfessionId(int professionId) {
		this.professionId = professionId;
	}

	@Column(name = "PROFESSION_TYPE", unique = true, nullable = false, length = 100)
	public String getProfessionType() {
		return this.professionType;
	}

	public void setProfessionType(String professionType) {
		this.professionType = professionType;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "professionList")
	public Set<ProfessionProductMapping> getProfessionProductMappings() {
		return this.professionProductMappings;
	}

	public void setProfessionProductMappings(Set<ProfessionProductMapping> professionProductMappings) {
		this.professionProductMappings = professionProductMappings;
	}

}

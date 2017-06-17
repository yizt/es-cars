package org.es.framework.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Simple JavaBean domain object with an id property. Used as a base class for
 * objects needing this property.
 * 
 * @author bingo
 */
@MappedSuperclass
@JsonFilter("_Filter_Name")
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	protected Long id;
	
	@Version
	@Column(name="h_version")
	protected Integer hVersion;
	
	@Column(name = "created_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdTime = new Date();
	
	/**
	 * 1可用，2暂停,3禁用　４异常不能使用 -1删除
	 */
	@Column(name = "available")
	private int available = 1;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isNew() {
		return (this.id == null);
	}
	
	public Integer gethVersion() {
		return hVersion;
	}

	public void sethVersion(Integer hVersion) {
		this.hVersion = hVersion;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public int getAvailable() {
		return available;
	}

	public void setAvailable(int available) {
		this.available = available;
	}
	
}

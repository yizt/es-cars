package org.es.framework.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 视图
 * @author zhouqi
 *
 */
@Entity
@Table(name = "views")
public class Views extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 视图名称
	 */
	@NotBlank
	@Column(length = 200)
	private String name;

	/**
	 * 视图描述
	 */
	@Column(length = 2000)
	private String memo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
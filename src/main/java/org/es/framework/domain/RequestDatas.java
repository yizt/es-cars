package org.es.framework.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 接口发送数据
 * @author zhouqi
 *
 */
@Entity
@Table(name = "request_datas")
public class RequestDatas extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 参数名称
	 */
	@NotBlank
	@Column(length = 200)
	private String name;

	/**
	 * 是否必须
	 * 0 false; 1 true
	 */
	@NotNull
	@Column(name = "is_required")
	private Integer isRequired;

	/**
	 * 类型
	 * 1 string; 2 number; 3 object; 4 array; 5 boolean
	 */
	@NotNull
	private Integer type;

	/**
	 * 默认值
	 */
	@Column(name = "default_value", length = 500)
	private String defaultValue;

	/**
	 * 描述
	 */
	@Column(length = 2000)
	private String memo;

	/**
	 * 示例数据
	 */
	@Column(length = 5000)
	private String example;

	// @NotNull
	// @Column(name = "api_id")
	// private Long apiId;
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "api_id", insertable = true)
	@JsonIgnore
	private Apis apis;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Integer isRequired) {
		this.isRequired = isRequired;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	// public Long getApiId() {
	// return apiId;
	// }
	//
	// public void setApiId(Long apiId) {
	// this.apiId = apiId;
	// }

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public Apis getApis() {
		return apis;
	}

	public void setApis(Apis apis) {
		this.apis = apis;
	}

}
package org.es.framework.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 接口
 * @author zhouqi
 *
 */
@Entity
@Table(name = "apis")
public class Apis extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 接口名称
	 */
	@NotBlank
	@Column(length = 200)
	private String name;

	/**
	 * 接口地址
	 */
	@NotBlank
	@Column(length = 500)
	private String url;

	/**
	 * 接口描述
	 */
	@Column(length = 2000)
	private String memo;

	/**
	 * 接口类型
	 * 1 JSON; 2 REST; 3 JavaApi
	 */
	@NotNull
	private Integer type;

	/**
	 * 接口协议
	 * 1 HTTP; 2 WEBSOCKET
	 */
	@NotNull
	private Integer protocol;

	/**
	 * 请求方法
	 * 1 POST; 2 GET; 3 PUT; 4 DELETE
	 */
	@NotNull
	@Column(name = "request_method")
	private Integer requestMethod;

	/**
	 * 请求数据类型
	 * 1 JSON; 2 XML; 3 RAW; 4 BINARY
	 */
	@NotNull
	@Column(name = "request_datatype")
	private Integer requestDatatype;

	/**
	 * 响应数据类型
	 * 1 JSON; 2 XML; 3 RAW; 4 BINARY
	 */
	@NotNull
	@Column(name = "response_datatype")
	private Integer responseDatatype;

	/**
	 * 请求数据
	 */
	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinColumn(name = "api_id")
	@OneToMany(mappedBy = "apis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<RequestDatas> requestDatas = new HashSet<RequestDatas>();

	/**
	 * 响应数据
	 */
	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinColumn(name = "api_id")
	@OneToMany(mappedBy = "apis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ResponseDatas> responseDatas = new HashSet<ResponseDatas>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getProtocol() {
		return protocol;
	}

	public void setProtocol(Integer protocol) {
		this.protocol = protocol;
	}

	public Integer getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(Integer requestMethod) {
		this.requestMethod = requestMethod;
	}

	public Integer getRequestDatatype() {
		return requestDatatype;
	}

	public void setRequestDatatype(Integer requestDatatype) {
		this.requestDatatype = requestDatatype;
	}

	public Set<RequestDatas> getRequestDatas() {
		return requestDatas;
	}

	public void setRequestDatas(Set<RequestDatas> requestDatas) {
		this.requestDatas = requestDatas;
	}

	public Set<ResponseDatas> getResponseDatas() {
		return responseDatas;
	}

	public void setResponseDatas(Set<ResponseDatas> responseDatas) {
		this.responseDatas = responseDatas;
	}

	public Integer getResponseDatatype() {
		return responseDatatype;
	}

	public void setResponseDatatype(Integer responseDatatype) {
		this.responseDatatype = responseDatatype;
	}

}
package org.es.framework.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "users_unique_userName", columnNames = { "user_name" }) })
public class Users extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 用户姓名
	 */
	@Column(length = 50)
	private String name;
	
	/**
	 * 登陆名称
	 */
	@NotEmpty
	@Column(length = 50,name="user_name")
	private String userName;
	
	/**
	 * 登陆密码
	 */
	@NotEmpty
	@Column(length = 50)
	private String password;
	
	/**
	 * 电子邮箱
	 */
	@Column(length = 50)
	@Email
	private String email;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(name = "group_id")
	private Groups groups;
	
	@ManyToMany
	@JoinTable(name = "roles_users", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	private Set<Roles> roles;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Groups getGroups() {
		return groups;
	}
	
	public void setGroups(Groups groups) {
		this.groups = groups;
	}

	public Set<Roles> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<Roles> roles) {
		this.roles = roles;
	}
}

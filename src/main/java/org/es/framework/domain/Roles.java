package org.es.framework.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "roles", uniqueConstraints = { @UniqueConstraint(name = "roles_unique_code", columnNames = { "code" }),
		@UniqueConstraint(name = "roles_unique_name", columnNames = { "name" }) })
public class Roles extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	@Column(length = 50)
	private String name;
	
	@NotEmpty
	@Column(length = 50)
	private String code;
	
	@ManyToMany
	@JoinTable(name = "roles_menus", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "menu_id") })
	private Set<Menus> menus;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public Set<Menus> getMenus() {
		return menus;
	}

	public void setMenus(Set<Menus> menus) {
		this.menus = menus;
	}

}

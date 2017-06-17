package org.es.framework.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.es.framework.utils.EsConstants;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "groups")
public class Groups extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	@Column(length = 50)
	private String name;
	
	@NotEmpty
	@Column(length = 50)
	private String code;

	@Column(length = 255)
	private String description;

	@Column
	private int level = EsConstants.MenuLevel.root;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "p_groups_id", foreignKey = @ForeignKey(name = "groups_p_groups_id_fkey"))
	private Groups pGroups;
	
	/*	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pGroups", fetch = FetchType.LAZY)
	@OrderBy(value="code")
	private List<Groups> subGroups = new ArrayList<Groups>();*/

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Groups getpGroups() {
		return pGroups;
	}

	public void setpGroups(Groups pGroups) {
		this.pGroups = pGroups;
	}

	/*public List<Groups> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(List<Groups> subGroups) {
		this.subGroups = subGroups;
	}*/

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}

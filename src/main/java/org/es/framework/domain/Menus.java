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
@Table(name = "menus")
public class Menus extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Column(length = 50,name="display_name")
	private String displayName;

	@NotEmpty
	@Column(length = 50)
	private String name;

	@NotEmpty
	@Column(length = 50)
	private String icon;

	@Column(length = 200)
	private String url;
	
	@Column(length = 100,name="resource_url")
	private String resourceUrl;

	@Column
	private int level = EsConstants.MenuLevel.root;

	@Column(name="display_order")
	private int displayOrder;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "p_menu_id", foreignKey = @ForeignKey(name = "menus_p_menu_id_fkey"))
	private Menus pMenu;

	/*	// @OneToMany(cascade = CascadeType.ALL, mappedBy = "pMenu",fetch =
	// FetchType.LAZY)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pMenu", fetch = FetchType.LAZY)
	@OrderBy(value="displayOrder")
	// @org.hibernate.annotations.BatchSize(size = 20)
	private List<Menus> subMenus = new ArrayList<Menus>();*/

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Menus getpMenu() {
		return pMenu;
	}

	public void setpMenu(Menus pMenu) {
		this.pMenu = pMenu;
	}


	/*	public List<Menus> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<Menus> subMenus) {
		this.subMenus = subMenus;
	}*/

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	@Override
	public String toString() {
		return "Menus [displayName=" + displayName + ", name=" + name + ", icon=" + icon + ", url=" + url
				+ ", resourceUrl=" + resourceUrl + ", level=" + level + ", displayOrder=" + displayOrder + ", pMenu="
				+ pMenu + "]";
	}

}

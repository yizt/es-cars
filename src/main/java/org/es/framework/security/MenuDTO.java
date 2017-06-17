package org.es.framework.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("_Filter_Name")
public class MenuDTO implements Serializable, Comparable<MenuDTO> {

	private static final long serialVersionUID = 84040762768110824L;

	private Long id;

	private String displayName;

	private String name;

	private String icon;

	private String url;

	private String resourceUrl;

	private int level;

	private int displayOrder;

	private List<MenuDTO> subMenus = new ArrayList<MenuDTO>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public List<MenuDTO> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<MenuDTO> subMenus) {
		this.subMenus = subMenus;
	}

	@Override
	public int compareTo(MenuDTO o) {
		return this.displayOrder - o.displayOrder;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

}

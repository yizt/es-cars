package org.es.framework.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "roles_menus")
public class RolesMenus extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "roles_menus_role_id_fkey"))
	private Roles role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", foreignKey = @ForeignKey(name = "roles_menus_menu_id_fkey"))
	private Menus menu;

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public Menus getMenu() {
		return menu;
	}

	public void setMenu(Menus menu) {
		this.menu = menu;
	}


}

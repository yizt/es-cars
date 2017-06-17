package org.es.framework.repository;

import java.util.List;

import org.es.framework.domain.Menus;
import org.es.framework.domain.RolesMenus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesMenusRepository extends JpaRepository<RolesMenus, Long>, JpaSpecificationExecutor<RolesMenus> {

	@Query("select m.menu from RolesMenus m where m.role.id=?1")
	public List<Menus> findMenusByRoleId(Long roleId);

	/**
	 * 根据角色id删除记录
	 * @param roleId
	 * @return
	 */
	@Modifying
	@Query(("delete from RolesMenus where role.id=?1"))
	public int delByRoleId(Long roleId);

	/**
	 * 根据菜单id批量删除记录
	 * @param menuIds
	 * @return
	 */
	@Modifying
	@Query(("delete from RolesMenus where menu.id in ?1"))
	public int delByMenuIds(List<Long> menuIds);
	
	/**
	 * 根据角色id批量删除记录
	 * @param roleIds
	 * @return
	 */
	@Modifying
	@Query(("delete from RolesMenus where role.id in ?1"))
	public int delByRoleIds(List<Long> roleIds);
}

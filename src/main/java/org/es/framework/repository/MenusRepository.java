package org.es.framework.repository;

import java.util.List;

import org.es.framework.domain.Menus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MenusRepository extends JpaRepository<Menus, Long>, JpaSpecificationExecutor<Menus> {
	
	@Query("select count(1) as cnt from Menus where pMenu.id=?1")
	public int countSubMenus(Long id);

	/**
	 * 根据id批量删除
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from Menus where id in ?1"))
	public int delByIds(List<Long> ids);

	/**
	 * 获得用户菜单
	 * @param userId
	 * @return
	 */
	@Query("from Menus m where m.id in (select rm.menu.id from RolesMenus rm where rm.role.id in (select ru.role.id from RolesUsers ru where ru.user.id = ?1))")
	public List<Menus> getMenusByUser(Long userId);

	/**
	 * 获得角色绑定的菜单
	 * @param roleId
	 * @return
	 */
	@Query("from Menus m WHERE m.id in (select rm.menu.id FROM RolesMenus rm WHERE rm.role.id = ?1)")
	public List<Menus> getMenusByRole(Long roleId);
}

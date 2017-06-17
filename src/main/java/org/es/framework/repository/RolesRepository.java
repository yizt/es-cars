package org.es.framework.repository;

import java.util.List;

import org.es.framework.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long>, JpaSpecificationExecutor<Roles> {
	
	@Query("select count(1) as cnt from Roles where code=?1")
	public int countByCode(String code);
	
	@Query("select count(1) as cnt from Roles where name=?1")
	public int countByName(String name);
	
	@Query("select count(1) as cnt from Roles where code=?1 and id <>?2")
	public int countByCode(String code, Long id);
	
	@Query("select count(1) as cnt from Roles where name=?1 and id <>?2")
	public int countByName(String name, Long id);
	
	public Roles findOneByCode(String code);
	
	/**
	 * 根据id批量删除
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from Roles where id in ?1"))
	public int delByIds(List<Long> ids);

	@Query("from Roles r where r.id in (select ru.role.id from RolesUsers ru where ru.user.id = ?1)")
	public List<Roles> getRolesByUser(Long userId);
}

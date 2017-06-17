package org.es.framework.repository;

import java.util.List;

import org.es.framework.domain.Roles;
import org.es.framework.domain.RolesUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesUsersRepository extends JpaRepository<RolesUsers, Long>, JpaSpecificationExecutor<RolesUsers> {

	@Modifying
	@Query(("delete from RolesUsers where user.id=?1"))
	public int delByUserId(Long userId);

	@Query(("select r.role from RolesUsers r where r.user.id = ?1"))
	public List<Roles> findRolesByUserId(Long userId);

	@Modifying
	@Query(("delete from RolesUsers where user.id in ?1"))
	public int delByUserIds(List<Long> userIds);
}

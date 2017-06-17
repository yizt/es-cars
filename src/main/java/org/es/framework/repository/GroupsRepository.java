package org.es.framework.repository;

import java.util.List;

import org.es.framework.domain.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupsRepository extends JpaRepository<Groups, Long>, JpaSpecificationExecutor<Groups> {
	
	@Query("select count(1) as cnt from Groups where pGroups.id=?1")
	public int countSubGroups(Long id);

	@Modifying
	@Query(("delete from Groups where id in ?1"))
	public int delByIds(List<Long> ids);
}

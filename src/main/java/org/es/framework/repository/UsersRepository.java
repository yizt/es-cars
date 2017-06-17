package org.es.framework.repository;

import java.util.List;

import org.es.framework.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {

	@Query("select count(1) as cnt from Users where userName=?1")
	public int countByUserName(String code);

	@Query("select count(1) as cnt from Users where userName=?1 and id <>?2")
	public int countByUserName(String userName, Long id);

	public Users findByUserName(String userName);
	
	/**
	 * 统计机构下人员数量
	 * @param groupsId
	 * @return
	 */
	@Query("select count(1) as cnt from Users where groups.id=?1")
	public int countByGroup(Long groupsId);

	/**
	 * 根据id批量删除
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from Users where id in ?1"))
	public int delByIds(List<Long> ids);

}

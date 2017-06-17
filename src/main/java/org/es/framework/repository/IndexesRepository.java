package org.es.framework.repository;

import java.util.List;

import javax.annotation.Resource;

import org.es.framework.domain.Indexes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 指标
 * @author zhouqi
 *
 */
@Resource
public interface IndexesRepository extends JpaRepository<Indexes, Long>, JpaSpecificationExecutor<Indexes> {
	/**
	 * 查询是否包含子指标分类和子指标
	 * @param id
	 * @return
	 */
	@Query("select count(1) as cnt from Indexes where pIndexes.id=?1")
	public int countSub(Long id);
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from Indexes where id in ?1"))
	public int delByIds(List<Long> ids);
}

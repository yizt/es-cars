package org.es.framework.repository;

import java.util.List;

import javax.annotation.Resource;

import org.es.framework.domain.Views;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 视图
 * @author zhouqi
 *
 */
@Resource
public interface ViewsRespository extends JpaRepository<Views, Long>, JpaSpecificationExecutor<Views> {
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from Views where id in ?1"))
	public int delByIds(List<Long> ids);
}

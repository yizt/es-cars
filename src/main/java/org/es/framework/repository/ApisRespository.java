package org.es.framework.repository;

import java.util.List;

import javax.annotation.Resource;

import org.es.framework.domain.Apis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 接口
 * @author zhouqi
 *
 */
@Resource
public interface ApisRespository extends JpaRepository<Apis, Long>, JpaSpecificationExecutor<Apis> {
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from Apis where id in ?1"))
	public int delByIds(List<Long> ids);
}

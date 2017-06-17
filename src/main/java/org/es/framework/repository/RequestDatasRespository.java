package org.es.framework.repository;

import java.util.List;

import javax.annotation.Resource;

import org.es.framework.domain.RequestDatas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 接口请求
 * @author zhouqi
 *
 */
@Resource
public interface RequestDatasRespository extends JpaRepository<RequestDatas, Long>,
JpaSpecificationExecutor<RequestDatas> {
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from RequestDatas where id in ?1"))
	public int delByIds(List<Long> ids);

	/**
	 * 删除某接口的请求参数
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from RequestDatas where apis.id in ?1"))
	public int delByApiIds(List<Long> ids);
	
}

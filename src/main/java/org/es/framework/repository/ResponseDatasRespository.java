package org.es.framework.repository;

import java.util.List;

import javax.annotation.Resource;

import org.es.framework.domain.ResponseDatas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 接口响应
 * @author zhouqi
 *
 */
@Resource
public interface ResponseDatasRespository extends JpaRepository<ResponseDatas, Long>,
JpaSpecificationExecutor<ResponseDatas> {
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from ResponseDatas where id in ?1"))
	public int delByIds(List<Long> ids);

	/**
	 * 删除某接口的响应数据
	 * @param ids
	 * @return
	 */
	@Modifying
	@Query(("delete from ResponseDatas where apis.id in ?1"))
	public int delByApiIds(List<Long> ids);
	
}

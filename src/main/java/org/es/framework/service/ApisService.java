package org.es.framework.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.domain.Apis;
import org.es.framework.domain.RequestDatas;
import org.es.framework.domain.ResponseDatas;
import org.es.framework.repository.ApisRespository;
import org.es.framework.repository.RequestDatasRespository;
import org.es.framework.repository.ResponseDatasRespository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * 接口
 * @author zhouqi
 *
 */
@Service
@Transactional
public class ApisService {
	
	@Resource
	private ApisRespository apisRespository;
	
	@Resource
	private RequestDatasRespository requestDatasRespository;
	
	@Resource
	private ResponseDatasRespository responseDatasRespository;
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public Apis detail(Long id) {
		return apisRespository.findOne(id);
	}
	
	/**
	 * 查询
	 * @param name
	 * @param type
	 * @param available
	 * @param pageIndex
	 * @param size
	 * @param sort
	 * @param sortFieldName
	 * @return
	 */
	public Page<Apis> find(final String name, final Integer type, final int available, int pageIndex, int size,
			String sort, String... sortFieldName) {
		Specification<Apis> specification = new Specification<Apis>() {
			@Override
			public Predicate toPredicate(Root<Apis> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(name)) {
					predicates.add(cb.like(root.<String> get("name"), name + "%"));
				}
				predicates.add(cb.equal(root.<String> get("type"), type));
				predicates.add(cb.equal(root.<String> get("available"), available));
				query.where(predicates.toArray(new Predicate[] {}));
				return null;
			}
		};
		if (sortFieldName != null && sortFieldName.length > 0) {
			return apisRespository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf(sort), sortFieldName)));
		} else {
			return apisRespository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf("ASC"), "createdTime")));
		}
	}
	
	public void save(Apis apis) {
		// 新增
		if (apis.isNew()) {
			// 接口
			Date date = new Date();
			apis.setCreatedTime(date);
			apis.sethVersion(0);
			
			// 接口请求数据
			Set<RequestDatas> requestDatas = apis.getRequestDatas();
			if (null != requestDatas && requestDatas.size() > 0) {
				for (RequestDatas req : requestDatas) {
					req.setCreatedTime(date);
					req.sethVersion(0);
					req.setApis(apis);
				}
			}
			
			// 接口响应数据
			Set<ResponseDatas> responseDatas = apis.getResponseDatas();
			if (null != responseDatas && responseDatas.size() > 0) {
				for (ResponseDatas resp : responseDatas) {
					resp.setCreatedTime(date);
					resp.sethVersion(0);
					resp.setApis(apis);
				}
			}
			
			apis.setRequestDatas(requestDatas);
			apis.setResponseDatas(responseDatas);
		} else {
			// 修改
			Apis updateApis = apis;
			
			apis = apisRespository.findOne(apis.getId());
			if (!StringUtils.isBlank(updateApis.getName())) {
				apis.setName(updateApis.getName());
			}
			if (!StringUtils.isBlank(updateApis.getUrl())) {
				apis.setUrl(updateApis.getUrl());
			}
			if (!StringUtils.isBlank(updateApis.getMemo())) {
				apis.setMemo(updateApis.getMemo());
			}
			apis.setType(updateApis.getType());
			apis.setProtocol(updateApis.getProtocol());
			apis.setAvailable(updateApis.getAvailable());
			apis.setRequestMethod(updateApis.getRequestMethod());
			apis.setRequestDatatype(updateApis.getRequestDatatype());
			apis.setResponseDatatype(updateApis.getResponseDatatype());
			
			// 接口请求数据
			Set<RequestDatas> updateRequestDatas = updateApis.getRequestDatas();
			if (null != updateRequestDatas && updateRequestDatas.size() > 0) {
				Set<RequestDatas> requestDatasSet = new HashSet<RequestDatas>();
				for (RequestDatas req : updateRequestDatas) {
					RequestDatas requestDatas = requestDatasRespository.findOne(req.getId());
					if (!StringUtils.isBlank(req.getName())) {
						requestDatas.setName(req.getName());
					}
					requestDatas.setIsRequired(req.getIsRequired());
					requestDatas.setType(req.getType());
					if (!StringUtils.isBlank(req.getDefaultValue())) {
						requestDatas.setDefaultValue(req.getDefaultValue());
					}
					if (!StringUtils.isBlank(req.getMemo())) {
						requestDatas.setMemo(req.getMemo());
					}
					requestDatas.setApis(apis);
					requestDatasSet.add(requestDatas);
				}
				apis.setRequestDatas(requestDatasSet);
			}
			
			// 接口响应数据
			Set<ResponseDatas> updateResponseDatas = updateApis.getResponseDatas();
			if (null != updateResponseDatas && updateResponseDatas.size() > 0) {
				Set<ResponseDatas> responseDatasSet = new HashSet<ResponseDatas>();
				for (ResponseDatas resp : updateResponseDatas) {
					ResponseDatas responseDatas = responseDatasRespository.findOne(resp.getId());
					if (!StringUtils.isBlank(resp.getName())) {
						responseDatas.setName(resp.getName());
					}
					responseDatas.setIsRequired(resp.getIsRequired());
					responseDatas.setType(resp.getType());
					if (!StringUtils.isBlank(resp.getMemo())) {
						responseDatas.setMemo(resp.getMemo());
					}
					responseDatas.setApis(apis);
					responseDatasSet.add(responseDatas);
				}
				apis.setResponseDatas(responseDatasSet);
			}
		}
		
		apisRespository.save(apis);
	}
	
	/**
	 * 删除
	 * @param ids
	 */
	public void delBatch(List<Long> ids) {
		// 删除
		requestDatasRespository.delByApiIds(ids);
		responseDatasRespository.delByApiIds(ids);
		apisRespository.delByIds(ids);
	}

}

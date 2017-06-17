package org.es.framework.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.domain.Views;
import org.es.framework.repository.ViewsRespository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * 视图
 * @author zhouqi
 *
 */
@Service
@Transactional
public class ViewsService {
	
	@Resource
	private ViewsRespository viewsRespository;
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public Views detail(Long id) {
		return viewsRespository.findOne(id);
	}
	
	/**
	 * 查询
	 * @param name
	 * @param pageIndex
	 * @param size
	 * @param sort
	 * @param sortFieldName
	 * @return
	 */
	public Page<Views> find(final String name, int pageIndex, int size, String sort, String... sortFieldName) {
		Specification<Views> specification = new Specification<Views>() {
			@Override
			public Predicate toPredicate(Root<Views> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(name)) {
					predicates.add(cb.like(root.<String> get("name"), name + "%"));
				}
				query.where(predicates.toArray(new Predicate[] {}));
				return null;
			}
		};
		if (sortFieldName != null && sortFieldName.length > 0) {
			return viewsRespository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf(sort), sortFieldName)));
		} else {
			return viewsRespository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf("ASC"), "createdTime")));
		}
	}
	
	/**
	 * 新增/修改
	 * @param views
	 */
	public void save(Views views) {
		if (!views.isNew()) {
			// 修改
			Views updateViews = views;
			
			views = viewsRespository.findOne(views.getId());
			
			if (!StringUtils.isBlank(updateViews.getName())) {
				views.setName(updateViews.getName());
			}
			if (!StringUtils.isBlank(updateViews.getMemo())) {
				views.setMemo(updateViews.getMemo());
			}
			views.setAvailable(updateViews.getAvailable());
		} else {
			// 新增
			views.setCreatedTime(new Date());
			views.sethVersion(0);
		}
		
		viewsRespository.save(views);
	}
	
	/**
	 * 删除
	 * @param ids
	 */
	public void delBatch(List<Long> ids) {
		viewsRespository.delByIds(ids);
	}
}

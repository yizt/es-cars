package org.es.framework.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.domain.Indexes;
import org.es.framework.mvc.exception.EsRuntimeException;
import org.es.framework.repository.IndexesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * 指标
 * @author zhouqi
 *
 */
@Service
@Transactional
public class IndexesService {
	@Resource
	private IndexesRepository indexesRepository;
	
	/**
	 * 指标查询
	 * @param name
	 * @param isLeaf 是否叶子节点;1-是,0-否;非叶子节点为指标分类，叶子节点为指标名称
	 * @param pageIndex
	 * @param size
	 * @param sort
	 * @param sortFieldName
	 * @return
	 */
	public Page<Indexes> find(final String name, final Integer isLeaf, int pageIndex, int size, String sort,
			String... sortFieldName) {
		Specification<Indexes> specification = new Specification<Indexes>() {
			@Override
			public Predicate toPredicate(Root<Indexes> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(name)) {
					predicates.add(cb.like(root.<String> get("name"), name + "%"));
				}
				predicates.add(cb.equal(root.<String> get("isLeaf"), isLeaf));
				query.where(predicates.toArray(new Predicate[] {}));
				return null;
			}
		};
		if (sortFieldName != null && sortFieldName.length > 0) {
			return indexesRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf(sort), sortFieldName)));
		} else {
			return indexesRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf("ASC"), "createdTime")));
		}
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public Indexes detail(Long id) {
		return indexesRepository.findOne(id);
	}
	
	/**
	 * 新增/修改
	 * @param indexes
	 */
	public void save(Indexes indexes) {
		if (indexes.isNew()) {
			// 新增
			int indexLayer = 0;
			if (null != indexes.getpIndexes()) {
				Indexes pIndexes = indexesRepository.getOne(indexes.getpIndexes().getId());
				indexLayer = pIndexes.getIndexLayer();
				indexes.setpIndexes(pIndexes);
			}
			indexes.setIndexLayer((indexes.getIsLeaf() == 0) ? indexLayer + 1 : 99);
		} else {
			// 修改
			Indexes updateIndexes = indexes;
			Indexes updatepIndexes = updateIndexes.getpIndexes();
			
			indexes = indexesRepository.findOne(indexes.getId()); // 转持久状态
			
			indexes.setName(updateIndexes.getName());
			indexes.setCode(updateIndexes.getCode());
			indexes.setAvailable(updateIndexes.getAvailable());
			if (!StringUtils.isBlank(updateIndexes.getMemo())) {
				indexes.setMemo(updateIndexes.getMemo());
			}
			if (null != updatepIndexes) {
				Indexes pIndexes = indexesRepository.findOne(updatepIndexes.getId()); // 转持久状态
				pIndexes.setId(updatepIndexes.getId());
				indexes.setpIndexes(pIndexes);
				indexes.setIndexLayer((indexes.getIsLeaf() == 0) ? pIndexes.getIndexLayer() + 1 : 99);
			}
		}
		
		indexesRepository.save(indexes);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delBatch(List<Long> ids) {
		int size = ids.size();
		for (int i = 0; i < size; i++) {
			// 判断是否还有子指标分类
			int cntSub = indexesRepository.countSub(ids.get(i));
			if (cntSub > 0) {
				throw new EsRuntimeException(String.format("第[%s]条记录还有子指标分类或子组织，不能删除", i + 1));
			}
		}
		
		// 删除
		indexesRepository.delByIds(ids);
	}
}

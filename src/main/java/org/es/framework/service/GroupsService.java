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
import org.es.framework.domain.Groups;
import org.es.framework.mvc.exception.EsRuntimeException;
import org.es.framework.repository.GroupsRepository;
import org.es.framework.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GroupsService {

	@Resource
	private GroupsRepository groupsRepository;

	@Resource
	private UsersRepository usersRepository;

	/**
	 * 查询
	 * @param name
	 * @param available
	 * @param pageIndex
	 * @param size
	 * @param sort
	 * @param sortFieldName
	 * @return
	 */
	public Page<Groups> find(final String name, final Integer available, int pageIndex, int size, String sort,
			String... sortFieldName) {
		Specification<Groups> specification = new Specification<Groups>() {
			@Override
			public Predicate toPredicate(Root<Groups> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(name)) {
					predicates.add(cb.like(root.<String> get("name"), name + "%"));
				}
				if (null != available) {
					predicates.add(cb.equal(root.<String> get("available"), available));
				}
				query.where(predicates.toArray(new Predicate[] {}));
				return null;
			}
		};
		if (sortFieldName != null && sortFieldName.length > 0) {
			return groupsRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf(sort), sortFieldName)));
		} else {
			return groupsRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf("ASC"), "createdTime")));
		}
	}
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delBatch(List<Long> ids) {
		int size = ids.size();
		for (int i = 0; i < size; i++) {
			// 判断是否还有子机构
			int cntSub = groupsRepository.countSubGroups(ids.get(i));
			if (cntSub > 0) {
				throw new EsRuntimeException(String.format("第[%s]个组织还有子组织，不能删除", i + 1));
			}

			// 判断是否有员工在该机构
			int cntUsers = usersRepository.countByGroup(ids.get(i));
			if (cntUsers > 0) {
				throw new EsRuntimeException(String.format("第[%s]个组织还有员工，不能删除", i + 1));
			}
		}

		// 删除
		groupsRepository.delByIds(ids);
	}

	/**
	 * 新增/修改
	 * @param groups
	 */
	public void save(Groups groups) {
		if (!groups.isNew()) {
			// 修改
			Groups updateGroups = groups;
			Groups updatepGroups = updateGroups.getpGroups();
			
			groups = groupsRepository.findOne(groups.getId()); // 转持久状态
			
			groups.setName(updateGroups.getName());
			groups.setCode(updateGroups.getCode());
			groups.setAvailable(updateGroups.getAvailable());
			if (!StringUtils.isBlank(updateGroups.getDescription())) {
				groups.setDescription(updateGroups.getDescription());
			}
			if (null != updatepGroups) {
				Groups pGroups = groupsRepository.findOne(updatepGroups.getId()); // 转持久状态
				pGroups.setId(updatepGroups.getId());
				groups.setpGroups(pGroups);
				groups.setLevel(pGroups.getLevel() + 1);
			}
		} else {
			// 新增
			int level = 0;
			if (groups.getpGroups() != null) {
				Groups pGroups = groupsRepository.getOne(groups.getpGroups().getId());
				level = pGroups.getLevel();
				groups.setpGroups(pGroups);
			}
			groups.setLevel(level + 1);
		}
		
		groupsRepository.save(groups);
	}

	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public Groups detail(Long id) {
		return groupsRepository.findOne(id);
	}

	/**
	 * 获得所有组织机构
	 * @return
	 */
	public List<Groups> getAllGroups() {
		return groupsRepository.findAll();
	}
}

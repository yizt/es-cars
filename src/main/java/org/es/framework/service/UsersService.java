package org.es.framework.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.domain.Groups;
import org.es.framework.domain.Roles;
import org.es.framework.domain.RolesUsers;
import org.es.framework.domain.Users;
import org.es.framework.mvc.exception.EsRuntimeException;
import org.es.framework.mvc.validation.ValidatorUtil;
import org.es.framework.repository.GroupsRepository;
import org.es.framework.repository.MenusRepository;
import org.es.framework.repository.RolesRepository;
import org.es.framework.repository.RolesUsersRepository;
import org.es.framework.repository.UsersRepository;
import org.es.framework.security.MyPasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UsersService {
	
	@Resource
	private UsersRepository usersRepository;
	
	@Resource
	private RolesUsersRepository rolesUsersRepository;
	
	@Resource
	private RolesRepository rolesRepository;
	
	@Resource
	private GroupsRepository groupsRepository;
	
	@Resource
	private MenusRepository menusRepository;
	
	@Resource
	private MyPasswordEncoder myPasswordEncoder;
	
	public Page<Users> find(final String userName, final String name, int pageIndex, int size, String sort,
			String... fieldName) {
		Specification<Users> specification = new Specification<Users>() {
			@Override
			public Predicate toPredicate(Root<Users> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(userName)) {
					predicates.add(cb.like(root.<String> get("userName"), userName + "%"));
				}
				if (StringUtils.isNotEmpty(name)) {
					predicates.add(cb.like(root.<String> get("name"), name + "%"));
				}
				query.where(predicates.toArray(new Predicate[] {}));
				return null;
			}
		};
		if (fieldName != null && fieldName.length > 0) {
			return usersRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf(sort), fieldName)));
		} else {
			return usersRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf("ASC"), "userName")));
		}
		
	}
	
	public List<Roles> findRolesByUserId(Long userId) {
		return rolesUsersRepository.findRolesByUserId(userId);
	}
	
	public Users findByUserName(String userName) {
		return usersRepository.findByUserName(userName);
	}
	
	public void save(Users user) {
		int usernameCount = 0;
		if (user.isNew()) {
			usernameCount = usersRepository.countByUserName(user.getUserName());
		} else {
			usernameCount = usersRepository.countByUserName(user.getUserName(), user.getId());
		}
		if (usernameCount > 0) {
			throw new EsRuntimeException(String.format("用户名是%s的用户已经存在", user.getUserName()));
		}
		user.setPassword(myPasswordEncoder.encodePassword(user.getPassword(), user.getUserName()));
		usersRepository.save(user);
	}
	
	public void selectRole(Long userId, List<Roles> roles) {
		Users user = usersRepository.getOne(userId);
		if (user == null) {
			throw new EsRuntimeException("用户已经不存在");
		}
		rolesUsersRepository.delByUserId(userId);
		for (Roles role : roles) {
			RolesUsers roleUsers = new RolesUsers();
			role.sethVersion(0);// 避免org.hibernate.TransientPropertyValueException: object references an unsaved
			// transient instanc
			roleUsers.setRole(role);
			roleUsers.setUser(user);
			rolesUsersRepository.save(roleUsers);
		}
	}
	
	public void del(Long userId) {
		rolesUsersRepository.delByUserId(userId);
		usersRepository.delete(userId);
	}
	
	/**
	 * 查询
	 * @param userName
	 * @param available
	 * @param pageIndex
	 * @param size
	 * @param sort
	 * @param fieldName
	 * @return
	 */
	public Page<Users> find(final String userName, final Integer available, int pageIndex, int size, String sort,
			String... sortFieldName) {
		Specification<Users> specification = new Specification<Users>() {
			@Override
			public Predicate toPredicate(Root<Users> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(userName)) {
					predicates.add(cb.like(root.<String> get("userName"), userName + "%"));
				}
				if (null != available) {
					predicates.add(cb.equal(root.<String> get("available"), available));
				}
				query.where(predicates.toArray(new Predicate[] {}));
				return null;
			}
		};
		if (sortFieldName != null && sortFieldName.length > 0) {
			return usersRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf(sort), sortFieldName)));
		} else {
			return usersRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf("ASC"), "createdTime")));
		}
	}
	
	/**
	 * 删除
	 * @param ids
	 */
	public void delBatch(List<Long> ids) {
		// 删除中间表记录
		rolesUsersRepository.delByUserIds(ids);
		
		// 删除用户
		usersRepository.delByIds(ids);
	}
	
	/**
	 * 新增
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void add(Map<String, Object> paramMap) {
		// 处理参数
		String userName = String.valueOf(paramMap.get("userName"));
		String name = String.valueOf(paramMap.get("name"));
		String email = String.valueOf(paramMap.get("email"));
		Integer available = (Integer) paramMap.get("available");
		
		// 判断用户是否存在
		int usernameCount = 0;
		usernameCount = usersRepository.countByUserName(userName);
		if (usernameCount > 0) {
			throw new EsRuntimeException(String.format("用户名是%s的用户已经存在", userName));
		}
		
		// 保存users
		Users user = new Users();
		if (!StringUtils.isBlank(userName)) {
			user.setUserName(userName);
		}
		if (!StringUtils.isBlank(name)) {
			user.setName(name);
		}
		if (!StringUtils.isBlank(email)) {
			user.setEmail(email);
		}
		if (null != available) {
			user.setAvailable(available);
		}
		user.setPassword(myPasswordEncoder.encodePassword("88888", user.getUserName()));// 默认密码88888
		user.sethVersion(0);
		user.setCreatedTime(new Date());
		Groups groups = new Groups(); // 组织机构
		Long groupsId = Long.parseLong(String.valueOf(paramMap.get("groupsId")));
		groups = groupsRepository.findOne(groupsId);
		groups.setId(groupsId);
		user.setGroups(groups);
		ValidatorUtil.checkDomain(user);
		usersRepository.save(user);
		user = usersRepository.findByUserName(user.getUserName()); // 转持久化
		
		// 判断角色是否存在
		List<String> codes = (List<String>) paramMap.get("roleCodes");
		List<String> codeNames = (List<String>) paramMap.get("roleCodeName");
		int size = codes.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Roles role = rolesRepository.findOneByCode(codes.get(i));
				if (role == null) {
					throw new EsRuntimeException(String.format("角色名是%s角色不存在", codeNames.get(i)));
				}
				
				// 保存rolesusers（角色与用户中间表）
				RolesUsers rolesUsers = new RolesUsers();
				rolesUsers.setUser(user); // 用户
				rolesUsers.setRole(role); // 角色
				rolesUsers.setAvailable(1);
				rolesUsers.setCreatedTime(new Date());
				rolesUsers.sethVersion(0);
				rolesUsersRepository.save(rolesUsers);
			}
		}
	}
	
	/**
	 * 修改
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void update(Map<String, Object> paramMap) {
		// 处理参数
		Long id = Long.parseLong(String.valueOf(paramMap.get("id")));
		if (id == null) {
			throw new EsRuntimeException("系统错误");
		}
		String name = String.valueOf(paramMap.get("name"));
		String email = String.valueOf(paramMap.get("email"));
		Integer available = (Integer) paramMap.get("available");
		
		// 判断用户是否存在
		Users user = usersRepository.findOne(id);
		if (null == user) {
			throw new EsRuntimeException("用户不存在");
		}
		
		// 处理users
		if (!StringUtils.isBlank(name)) {
			user.setName(name);
		}
		if (!StringUtils.isBlank(email)) {
			user.setEmail(email);
		}
		if (null != available) {
			user.setAvailable(available);
		}
		Groups groups = new Groups(); // 组织机构
		Long groupsId = Long.parseLong(String.valueOf(paramMap.get("groupsId")));
		groups = groupsRepository.findOne(groupsId);
		groups.setId(groupsId);
		user.setGroups(groups);
		usersRepository.save(user);
		// user = usersRepository.findByUserName(user.getUserName()); // 转持久化
		
		// 处理rolesusers
		// 删除原用户角色映射
		rolesUsersRepository.delByUserId(id);
		// 判断角色是否存在
		List<String> codes = (List<String>) paramMap.get("roleCodes");
		List<String> codeNames = (List<String>) paramMap.get("roleCodeName");
		int size = codes.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Roles role = rolesRepository.findOneByCode(codes.get(i));
				if (role == null) {
					throw new EsRuntimeException(String.format("角色名是%s角色不存在", codeNames.get(i)));
				}
				
				// 保存rolesusers（角色与用户中间表）
				RolesUsers rolesUsers = new RolesUsers();
				rolesUsers.setUser(user); // 用户
				rolesUsers.setRole(role); // 角色
				rolesUsers.setAvailable(1);
				rolesUsers.setCreatedTime(new Date());
				rolesUsers.sethVersion(0);
				rolesUsersRepository.save(rolesUsers);
			}
		}
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public Users detail(Long id) {
		return usersRepository.findOne(id);
	}
}

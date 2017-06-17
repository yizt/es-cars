package org.es.initDB;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.es.framework.EsCarsApp;
import org.es.framework.domain.Menus;
import org.es.framework.domain.Roles;
import org.es.framework.domain.RolesMenus;
import org.es.framework.domain.RolesUsers;
import org.es.framework.domain.Users;
import org.es.framework.repository.MenusRepository;
import org.es.framework.repository.RolesMenusRepository;
import org.es.framework.repository.RolesRepository;
import org.es.framework.repository.RolesUsersRepository;
import org.es.framework.repository.UsersRepository;
import org.es.framework.security.MyPasswordEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EsCarsApp.class)
@Rollback(false)
@ActiveProfiles("dev")
public class TestAdminUsersService {
	
	@Resource
	private MenusRepository menusRepository;
	
	@Resource
	private MyPasswordEncoder myPasswordEncoder;
	
	@Resource
	private UsersRepository usersRepository;
	
	@Resource
	private RolesRepository rolesRepository;
	
	@Resource
	private RolesMenusRepository rolesMenusRepository;
	
	@Resource
	private RolesUsersRepository rolesUsersRepository;
	
	@Test
	public void printPassword(){
		System.out.println(myPasswordEncoder.encodePassword("123456","test"));
	}
	
	@Test
	@Transactional
	public void initMenu(){
		Menus menu = new Menus();
		menu.setName("系统管理");
		menu.setDisplayName("系统管理");
		menu.setIcon("fa fa-sitemap");
		menu.setLevel(1);
		menu.setDisplayOrder(1);
		menu.setUrl("");
		menusRepository.save(menu);
		Menus subMenu = new Menus();
		subMenu.setName("用户管理");
		subMenu.setDisplayName("用户管理");
		subMenu.setIcon("fa fa-user");
		subMenu.setUrl("views/users/users");
		subMenu.setpMenu(menu);
		subMenu.setLevel(2);
		subMenu.setDisplayOrder(2);
		menusRepository.save(subMenu);
		
		subMenu = new Menus();
		subMenu.setName("菜单管理");
		subMenu.setDisplayName("菜单管理");
		subMenu.setIcon("fa fa-sitemap");
		subMenu.setUrl("views/users/menus");
		subMenu.setLevel(2);
		subMenu.setpMenu(menu);
		subMenu.setDisplayOrder(3);
		menusRepository.save(subMenu);
		
		subMenu = new Menus();
		subMenu.setName("角色管理");
		subMenu.setDisplayName("角色管理");
		subMenu.setIcon("fa fa-group");
		subMenu.setUrl("views/users/roles");
		subMenu.setLevel(2);
		subMenu.setpMenu(menu);
		subMenu.setDisplayOrder(4);
		menusRepository.save(subMenu);
		
		subMenu = new Menus();
		subMenu.setName("权限管理");
		subMenu.setDisplayName("权限管理");
		subMenu.setIcon("fa fa-key");
		subMenu.setUrl("views/users/roles_menus");
		subMenu.setLevel(2);
		subMenu.setpMenu(menu);
		subMenu.setDisplayOrder(5);
		menusRepository.save(subMenu);
		
		
	}
	
	@Test
	@Transactional
	public void initAdmin(){
		Roles role = new Roles();
		if(rolesRepository.findAll().size() ==0 ){
			Users user = new Users();
			user.setUserName("admin");
			user.setPassword(myPasswordEncoder.encodePassword("123456","admin"));
			role.setCode("admin");
			role.setName("超级管理员");
			rolesRepository.save(role);
			usersRepository.save(user);
			RolesUsers ruser =new RolesUsers();
			ruser.setRole(role);
			ruser.setUser(user);
			rolesUsersRepository.save(ruser);
		}else{
			role = rolesRepository.findOneByCode("admin");
		}
		List<Menus> menus = menusRepository.findAll();
		for (Menus menu : menus) {
			if(menu.getLevel() == 2){
				RolesMenus rolesMenu = new RolesMenus();
				rolesMenu.setRole(role);
				rolesMenu.setMenu(menu);
				rolesMenusRepository.save(rolesMenu);
			}
		}
		
		
	}

}

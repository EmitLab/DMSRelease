package dao;

// REFERENCE LINK : http://www.tutorialspoint.com/design_pattern/data_access_object_pattern.htm

import java.util.ArrayList;
import java.util.List;

public class AdminUserDaoImpl implements AdminUserDao{
	
	List<AdminUser> adminUsers;
	
	public AdminUserDaoImpl(){
		adminUsers = new ArrayList<AdminUser>();
	}
	
	@Override
	public void addAdminUser(AdminUser adminUser){
		adminUsers.add(adminUser);
	}
	
	@Override
	public void deleteAdminUser(AdminUser adminUser){
		//adminUsers.remove(adminUser.getId());
		adminUsers.remove(adminUsers.indexOf(adminUser.getId()));
	}
	
	@Override
	public List<AdminUser> getAllAdminUsers(){
		return adminUsers;
	}
	
	@Override
	public AdminUser getAdminUserById(int id){
		if( adminUsers.contains(id)) {
			return adminUsers.get(adminUsers.indexOf(id));
		} else {
			return null;
		}
	}
	
	@Override
	public AdminUser getAdminUserByEmail(String email){
		if( adminUsers.contains(email)) {
			return adminUsers.get(adminUsers.indexOf(email));
		} else {
			return null;
		}
	}
}

package dao;

import java.util.List;

public interface AdminUserDao {

	public void addAdminUser(AdminUser adminUser);
	public List<AdminUser> getAllAdminUsers();
	public AdminUser getAdminUserById(int id);
	public AdminUser getAdminUserByEmail(String email);
	void deleteAdminUser(AdminUser adminUser);
	
}

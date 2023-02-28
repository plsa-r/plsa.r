package foo.bar.repo;

import foo.bar.model.Role;
import foo.bar.model.User;
import foo.bar.model.UserPermission;
import foo.bar.model.UserRole;
import net.plsar.Dao;
import net.plsar.annotations.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserRepo {

    Dao dao;

    public UserRepo(Dao dao){
        this.dao = dao;
    }

    public User getSaved() {
        String idSql = "select max(id) from users";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getId() {
        String sql = "select max(id) from users";
        long id = dao.getLong(sql, new Object[]{});
        return id;
    }

    public long getCount() {
        String sql = "select count(*) from users";
        Long count = dao.getLong(sql, new Object[] { });
        return count;
    }

    public User get(long id) {
        String sql = "select * from users where id = [+]";
        User user = dao.get(sql, new Object[] { id }, User.class);
        return user;
    }

    public User get(String email) {
        String sql = "select * from users where email = '[+]'";
        User user = dao.get(sql, new Object[] { email }, User.class);
        return user;
    }

    public User getUid(String uid) {
        String sql = "select * from users where uid = '[+]'";
        User user = dao.get(sql, new Object[] { uid }, User.class);
        return user;
    }

    public User getPhone(String phone) {
        String sql = "select * from users where phone = '[+]'";
        User user = dao.get(sql, new Object[] { phone }, User.class);
        return user;
    }

    public User getEmail(String email) {
        String sql = "select * from users where email = '[+]'";
        User user = dao.get(sql, new Object[] { email }, User.class);
        return user;
    }

    public List<User> all() {
        String sql = "select * from users";
        List<User> users = dao.getList(sql, new Object[]{}, User.class);
        return users;
    }

    public void save(User user) {
        String sql = "insert into users (name, phone, email, passwd, time_created) " +
                "values ('[+]','[+]','[+]','[+]',[+])";
        dao.save(sql, new Object[]{
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getPasswd(),
                user.getTimeCreated()
        });
    }

    public void update(User user) {
        String sql = "update users set name = '[+]', phone = '[+]', email = '[+]' where id = [+]";
        dao.update(sql, new Object[]{
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getId()
        });
    }

    public void delete(long id) {
        String sql = "delete from users where id = [+]";
        dao.update(sql, new Object[] { id });
    }

    public void saveUserRole(long userId, long roleId){
        String sql = "insert into user_roles (role_id, user_id) values ([+], [+])";
        dao.save(sql, new Object[]{roleId, userId});
    }

    public void saveUserRole(long userId, String roleName){
        Role role = dao.get("select * from roles where name = '[+]'", new Object[]{roleName}, Role.class);
        String sql = "insert into user_roles (role_id, user_id) values ([+], [+])";
        dao.save(sql, new Object[]{role.getId(), userId});
    }

    public void savePermission(long userId, String permission){
        String sql = "insert into user_permissions (user_id, permission) values ([+], '[+]')";
        dao.save(sql, new Object[]{ userId,  permission });
    }

    public Set<String> getUserRoles(long id) {
        String sql = "select r.name as name from user_roles ur inner join roles r on r.id = ur.role_id where ur.user_id = [+]";
        List<UserRole> rolesList = dao.getList(sql, new Object[]{ id }, UserRole.class);
        Set<String> roles = new HashSet<>();
        for(UserRole role: rolesList){
            roles.add(role.getName());
        }
        return roles;
    }

    public boolean hasPermission(Long id, String permission) {
        System.out.println("haspermission=>" + id + ":" + permission);
        String sql = "select * from user_permissions where user_id = [+] and permission = '[+]'";
        UserPermission userPermission = dao.get(sql, new Object[]{ id, permission }, UserPermission.class);
        return userPermission != null;
    }

    public Set<String> getUserPermissions(long id) {
        String sql = "select permission from user_permissions where user_id = [+]";
        List<UserPermission> permissionsList = dao.getList(sql, new Object[]{ id }, UserPermission.class);
        Set<String> permissions = new HashSet<>();
        for(UserPermission permission: permissionsList){
            permissions.add(permission.getPermission());
        }
        return permissions;
    }

}

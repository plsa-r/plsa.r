package foo.bar.repo;

import foo.bar.model.Activity;
import net.plsar.Dao;
import net.plsar.annotations.Repository;

import java.util.List;

@Repository
public class ActivityRepo {

    Dao dao;

    public ActivityRepo(Dao dao){
        this.dao = dao;
    }

    public Activity getSaved() {
        String idSql = "select max(id) from activities";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getId() {
        String sql = "select max(id) from activities";
        long id = dao.getLong(sql, new Object[]{});
        return id;
    }

    public long getCount() {
        String sql = "select count(*) from activities";
        Long count = dao.getLong(sql, new Object[] { });
        return count;
    }

    public Activity get(long id) {
        String sql = "select * from activities where id = [+]";
        Activity activity = dao.get(sql, new Object[] { id }, Activity.class);
        return activity;
    }

    public List<Activity> all() {
        String sql = "select * from activities";
        List<Activity> activities = dao.getList(sql, new Object[]{}, Activity.class);
        return activities;
    }

    public int save(Activity activity) {
        String sql = "insert into activities (description) values ('[+]')";
        int id = dao.save(sql, new Object[]{
                activity.getDescription()
        });
        return id;
    }

    public void update(Activity activity) {
        String sql = "update activities set description = '[+]' id = [+]";
        dao.update(sql, new Object[]{
                activity.getDescription(),
                activity.getId()
        });
    }

    public void delete(long id) {
        String sql = "delete from activities where id = [+]";
        dao.update(sql, new Object[] { id });
    }

}

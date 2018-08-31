package cn.newphy.orm.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.Page;
import cn.newphy.mate.PageMode;
import cn.newphy.mate.PageRequest;
import cn.newphy.mate.sql.Order;
import cn.newphy.orm.demo.entity.User;
import cn.newphy.orm.demo.entity.User;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.DigestUtils;

public class UserEntityDaoTest extends BaseTest {
    private Logger logger = LoggerFactory.getLogger(UserEntityDaoTest.class);

    @Autowired
    private EntityDao<User> userDao;

    private User concreteUser() {
        User user = new User();
        user.setName("张翼德");
        user.setLoginName("ZhangFly");
        user.setBirthday(new Date());
        user.setGender(1);
        user.setMaritalStatus(0);
        user.setAddress("蜀国德胜大街200号张公馆");
        user.setRegisterTime(new Date());
        user.setRemark("枉死");
        user.setVersion(0);
        user.setStatus(0);
        user.setLevel(0);
        return user;
    }


    @Test public void save() throws Exception {
        User user = concreteUser();
        int i = userDao.save(user);
        assertEquals(1, i);
        assertNotNull(user.getId());
    }

    @Test public void batchSaveArray() throws Exception {
        User[] users = {concreteUser(), concreteUser(), concreteUser(), concreteUser()};
        int i = userDao.batchSave(users);
        assertEquals(4, i);
        for (int j = 0; j < users.length; j++) {
            User user = users[j];
            assertNotNull(user.getId());
        }
    }

    @Test public void batchSaveCollection() throws Exception {
        User[] users = {concreteUser(), concreteUser()};
        int i = userDao.batchSave(Arrays.asList(users));
        assertEquals(2, i);
        for (int j = 0; j < users.length; j++) {
            User user = users[j];
            assertNotNull(user.getId());
        }
    }

    private User getLatestOne() {
        List<User> users = userDao.listAll(Order.asc("id"));
        if (users != null && users.size() > 0) {
            return users.get(users.size() - 1);
        }
        return null;
    }

    private List<User> getLatestSome() {
        List<User> users = userDao.listAll(Order.asc("id"));
        if (users != null && users.size() >= 2) {
            return users.subList(users.size()-2, users.size());
        }
        return users;
    }

    private void doUpdate(User user) {
        Long id = user.getId();
        user.setStatus(2);
        user.setName(user.getName() + RandomStringUtils.randomAlphanumeric(4));
        user.setLoginName("ZhangFly" + RandomStringUtils.randomAlphanumeric(4));
        user.setBirthday(new Date());
        user.setGender(1);
        user.setMaritalStatus(1);
        user.setRegisterTime(new Date());
        user.setRemark(RandomStringUtils.randomAlphanumeric(64));
    }

    @Test public void update() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            doUpdate(user);
            int c = userDao.update(user);
            assertEquals(1, c);
        } else {
            fail("no data to test");
        }
    }


    @Test public void updateOptimistic() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            doUpdate(user);
            int c = userDao.updateOptimistic(user);
            assertEquals(1, c);

            try {
                c = userDao.updateOptimistic(user);
                assertNotEquals(1, c);
            } catch (Exception e) {
                assertTrue(e instanceof OptimisticLockingFailureException);
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void batchUpdate() throws Exception {
        List<User> users = getLatestSome();
        if (users != null && users.size() > 0) {
            for (User user : users) {
                doUpdate(user);
            }
            int c = userDao.batchUpdate(users);
            assertEquals(users.size(), c);
        } else {
            fail("no data to test");
        }
    }


    @Test public void delete() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            int c = userDao.delete(user);
            assertEquals(1, c);
        } else {
            fail("no data to test");
        }
    }


    @Test public void batchDelete() throws Exception {
        List<User> users = getLatestSome();
        if (users != null && users.size() > 0) {
            users = users.subList(0, 2);
            int c = userDao.batchDelete(users);
            assertEquals(2, c);
        } else {
            fail("no data to test");
        }
    }


    @Test public void deleteById() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            int c = userDao.deleteById(user.getId());
            assertEquals(1, c);
        } else {
            fail("no data to test");
        }
    }

    @Test public void get() throws Exception {
        User user1 = getLatestOne();
        if (user1 != null) {
            User user2 = userDao.get(user1.getId());
            assertEquals(user1.getId(), user2.getId());
        } else {
            fail("no data to test");
        }
    }

    @Test public void getOneBy() throws Exception {
        User user1 = getLatestOne();
        if (user1 != null) {
            User user2 = userDao.getOneBy("id", user1.getId());
            assertEquals(user1.getId(), user2.getId());
        } else {
            fail("no data to test");
        }
    }

    @Test public void getOneByMultiProperties() throws Exception {
        User user1 = getLatestOne();
        if (user1 != null) {
            User user2 = userDao.getOneBy(
                new String[] {"gender", "name"},
                new Object[] {user1.getGender(), user1.getName()}
                );
            assertEquals(user1.getId(), user2.getId());
        } else {
            fail("no data to test");
        }
    }

    @Test public void getOneByTemplate() throws Exception {
        User user1 = getLatestOne();
        if (user1 != null) {
            Long id = user1.getId();
            user1.setId(null);
            User user2 = userDao.getOneByTemplate(user1);
            assertEquals(id, user2.getId());
        } else {
            fail("no data to test");
        }

    }

    @Test public void getUniqueBy() throws Exception {
        User user1 = getLatestOne();
        if (user1 != null) {
            User user2 = userDao.getUniqueBy("id", user1.getId());
            assertEquals(user1.getId(), user2.getId());

            try {
                userDao.getUniqueBy("gender", user1.getGender());
                fail("这里应该报错");
            } catch (Exception e) {
                assertTrue(e instanceof IncorrectResultSizeDataAccessException);
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void getUniqueBy1() throws Exception {
        User user1 = getLatestOne();
        if (user1 != null) {
            User user2 = userDao.getUniqueBy(
                new String[] {"gender", "name"},
                new Object[] {user1.getGender(), user1.getName()});
            assertEquals(user1.getId(), user2.getId());

            try {
                userDao.getUniqueBy(new String[]{"gender", "status"}, new Object[]{user1.getGender(), user1.getStatus()});
                fail("这里应该报错");
            } catch (Exception e) {
                assertTrue(e instanceof IncorrectResultSizeDataAccessException);
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void getUniqueByTemplate() throws Exception {
        User user1 = getLatestOne();
        if (user1 != null) {
            Long id = user1.getId();
            user1.setId(null);
            User user2 = userDao.getUniqueByTemplate(user1);
            assertEquals(id, user2.getId());
        } else {
            fail("no data to test");
        }
    }

    @Test public void listAll() throws Exception {
        List<User> users = userDao.listAll(Order.asc("id"));
        assertTrue(users != null && users.size() > 0);
    }


    @Test public void listBy() throws Exception {
        List<User> users1 = userDao.listAll(Order.asc("id"));
        if (users1 != null && users1.size() > 0) {
            User user = users1.get(0);
            List<User> users = userDao.listBy("level", user.getLevel(), Order.asc("name"));
            assertEquals(users1.size(), users.size());

            String name = "";
            for (User check : users) {
                assertEquals(user.getGender(), check.getGender());
                assertTrue(check.getName().toLowerCase().compareTo(name) >= 0);
                name = check.getName().toLowerCase();
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void listBy1() throws Exception {
        List<User> users1 = userDao.listAll(Order.asc("id"));
        if (users1 != null && users1.size() > 0) {
            User user = users1.get(0);
            List<User> users = userDao.listBy(
                new String[]{"gender", "status"},
                new Object[]{user.getGender(), user.getStatus()},
                Order.asc("name"));
            assertTrue(users != null && users.size() > 0 && users.size() <= users1.size());
            String name = "";
            for (User check : users) {
                assertEquals(user.getGender(), check.getGender());
                assertEquals(user.getStatus(), check.getStatus());
                assertTrue(check.getName().toLowerCase().compareTo(name) >= 0);
                name = check.getName().toLowerCase();
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void listByTemplate() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            User template = new User();
            template.setGender(user.getGender());
            template.setStatus(user.getStatus());
            List<User> users = userDao.listByTemplate(template, Order.asc("name"));
            assertTrue(users != null && users.size() > 1 );
            String name = "";
            for (User check : users) {
                assertEquals(user.getGender(), check.getGender());
                assertEquals(user.getStatus(), check.getStatus());
                assertTrue(check.getName().toLowerCase().compareTo(name) >= 0);
                name = check.getName().toLowerCase();
            }
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void page() throws Exception {
        PageRequest pageable = new PageRequest(1, 5, PageMode.INFINITE);
        Page<User> page = userDao.page(pageable);
        logger.info(page.toString());
        assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);

        pageable = new PageRequest(1, 5, PageMode.TOTAL);
        page = userDao.page(pageable);
        logger.info(page.toString());
        assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);
    }

    @Test public void pageByTemplate() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            User template = new User();
            template.setGender(user.getGender());
            template.setStatus(user.getStatus());

            PageRequest pageable = new PageRequest(1, 5, PageMode.INFINITE);
            Page<User> page = userDao.pageByTemplate(pageable, template, Order.asc("name"));
            logger.info(page.toString());
            assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);

            pageable = new PageRequest(1, 5, PageMode.TOTAL);
            page = userDao.pageByTemplate(pageable, template, Order.asc("name"));
            logger.info(page.toString());
            assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void pageBy() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            User template = new User();
            template.setGender(user.getGender());
            template.setStatus(user.getStatus());

            PageRequest pageable = new PageRequest(1, 5, PageMode.INFINITE);
            Page<User> page = userDao.pageBy(
                pageable,
                new String[]{"gender", "status"},
                new Object[]{user.getGender(), user.getStatus()},
                Order.asc("name"));
            logger.info(page.toString());
            assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);

            pageable = new PageRequest(1, 5, PageMode.TOTAL);
            page = userDao.pageBy(
                pageable,
                new String[]{"gender", "status"},
                new Object[]{user.getGender(), user.getStatus()},
                Order.asc("name"));
            logger.info(page.toString());
            assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void count() throws Exception {
        int count = userDao.count();
        assertTrue(count > 0);
    }

    @Test public void countBy() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            int count = userDao.count("parterId", user.getGender());
            assertTrue(count > 0);
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void countByMulitiProperties() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            int count = userDao.count(
                new String[]{"gender", "status"},
                new Object[]{user.getGender(), user.getStatus()}
            );
            assertTrue(count > 0);
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void countByTemplate() throws Exception {
        User user = getLatestOne();
        if (user != null) {
            User template = new User();
            template.setGender(user.getGender());
            template.setStatus(user.getStatus());

            int count = userDao.countByTemplate(template);
            assertTrue(count > 0);
        }
        else {
            fail("no data to test");
        }
    }

    @Ignore("Not yet implemented")
    @Test public void flush() throws Exception {

    }


}
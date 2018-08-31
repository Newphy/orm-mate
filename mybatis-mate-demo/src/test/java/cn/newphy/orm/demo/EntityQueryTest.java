package cn.newphy.orm.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.EntityQuery;
import cn.newphy.mate.Page;
import cn.newphy.mate.PageMode;
import cn.newphy.mate.PageRequest;
import cn.newphy.mate.QueryTemplate;
import cn.newphy.mate.sql.Order;
import cn.newphy.orm.demo.entity.User;
import cn.newphy.orm.demo.entity.User;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.DigestUtils;

public class EntityQueryTest extends BaseTest {
    private Logger logger = LoggerFactory.getLogger(EntityQueryTest.class);
    @Autowired
    private EntityDao<User> userDao;

    @Test public void querySimpleTest() throws Exception {
        List<User> list = userDao.query()
            .eq("gender", 1)
            .eq("status", 2)
            .ge("level", 0)
            .between("birthday", DateUtils.addDays(new Date(), -100), new Date())
            .orderDesc("name")
            .limit(3, 2)
            .list();
        assertTrue(!list.isEmpty());
    }

    @Test public void queryTemplateTest() throws Exception {
        List<User> list = userDao.query().queryTemplate(new QueryTemplate<User>() {
            @Override public void process(User template) {
                template.setGender(1);
                template.setStatus(2);
                template.getId();
                template.getName();
            }
        }).ge("level", 0)
            .between("birthday", DateUtils.addDays(new Date(), -100), new Date())
            .orderDesc("name")
            .limit(4)
            .list();
        assertTrue(!list.isEmpty());
    }

    @Test public void queryInTest() throws Exception {
        List<User> list = userDao.query().in("loginName", new String[]{"ZhangFly", "ZhangFlyctYw"})
            .orderAsc("name").list();
        assertTrue(!list.isEmpty());
    }


    @Test public void queryLikeTest() throws Exception {
        List<User> list = userDao.query().like("loginName", "ZhangFly%")
            .orderAsc("name").list();
        assertTrue(!list.isEmpty());
    }

    @Test public void queryNotNullTest() throws Exception {
        List<User> list = userDao.query().notNull("loginName")
            .orderAsc("name").list();
        assertTrue(!list.isEmpty());
    }


    @Test public void queryGroupByTest() throws Exception {
        List<User> list = userDao.query()
            .include("name", "loginName")
            .groupBy("name")
            .eq("level", 0)
            .orderAsc("name").list();
        assertTrue(!list.isEmpty());
    }

    @Test public void queryPageTest() throws Exception {
        Page<User> page = userDao.query().include("name", "loginName").groupBy("name").eq("level", 0).orderAsc("name")
            .page(new PageRequest(2, 5, PageMode.TOTAL));
        logger.info("分页结果: {}", page);
        assertTrue(page.getPageNumber() == 2 && page.getNumberOfElements() <= 5);
    }

    @Test public void queryPageInfiniteTest() throws Exception {
        Page<User> page = userDao.query().include("name", "loginName").groupBy("name").eq("level", 0).orderAsc("name")
            .page(new PageRequest(2, 5, PageMode.INFINITE));
        logger.info("分页结果: {}", page);
        assertTrue(
            page.getPageNumber() == 2
                && page.getNumberOfElements() <= 5
                && page.isHasNextPage());
    }

    @Test public void queryOneTest() throws Exception {
        User user = userDao.query().include("name", "loginName").groupBy("name").eq("level", 0).orderAsc("name")
            .one();
        logger.info("用户: {}", user);
        assertTrue(user != null);
    }

    @Test public void queryUniqueTest() throws Exception {
        User user = null;
        EntityQuery<User> entityQuery = userDao.query();
        try {
            user = entityQuery.groupBy("name").eq("level", 0).orderAsc("name")
                .unique();
            fail("不应该出现在这里");
        } catch (Exception e) {
            assertTrue(e instanceof IncorrectResultSizeDataAccessException);
        }
        user = entityQuery.one();
        user = entityQuery.eq("id", user.getId()).unique();
        logger.info("用户: {}", user);
        assertTrue(user != null);
    }

    @Test public void queryCountTest() throws Exception {
        long count = userDao.query().groupBy("name").eq("level", 0).orderAsc("name")
            .count();
        assertTrue(count > 0);
    }

    @Test public void queryDistinctTest() throws Exception {
        List<User> users = userDao.query().include("name").distinct().eq("level", 0).orderAsc("name")
            .list();
        assertTrue(users.size() > 0);
    }
}
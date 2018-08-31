package cn.newphy.orm.demo;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.EntityQuery;
import cn.newphy.mate.Page;
import cn.newphy.mate.PageMode;
import cn.newphy.mate.PageRequest;
import cn.newphy.mate.QueryTemplate;
import cn.newphy.mate.UpdateTemplate;
import cn.newphy.orm.demo.entity.User;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

public class EntityUpdateTest extends BaseTest {
    private Logger logger = LoggerFactory.getLogger(EntityUpdateTest.class);
    @Autowired
    private EntityDao<User> userDao;

    @Test public void updateTest() throws Exception {
        int c = userDao.update()
            .eq("gender", 1)
            .eq("status", 2)
            .ge("level", 0)
            .set("remark", RandomStringUtils.randomNumeric(32))
            .update();
        assertTrue(c > 0);
    }

    @Test public void updateTemplateTest() throws Exception {
        int c = userDao.update().updateTemplate(new UpdateTemplate<User>() {
            @Override public void process(User template) {
                template.setRemark(RandomStringUtils.randomNumeric(32));
                template.setGender(1);
            }
        }).queryTemplate(new QueryTemplate<User>() {
            @Override public void process(User template) {
                template.setGender(1);
                template.setLoginName("ZhangFly");
            }
        }).update();
        assertTrue(c > 0);
    }

    @Test public void updateOptimisticTest() throws Exception {
        User user = userDao.query().one();
        int c = userDao.update().set("name", user.getName() + RandomStringUtils.randomAlphanumeric(6))
            .updateOptimistic(user.getId(), user.getVersion());
        assertTrue(c > 0);
    }


}
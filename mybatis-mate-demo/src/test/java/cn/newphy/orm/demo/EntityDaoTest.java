package cn.newphy.orm.demo;

import static org.junit.Assert.*;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.Page;
import cn.newphy.mate.PageMode;
import cn.newphy.mate.PageRequest;
import cn.newphy.mate.Pageable;
import cn.newphy.mate.sql.Order;
import cn.newphy.orm.demo.entity.Attest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

public class EntityDaoTest extends BaseTest {
    private Logger logger = LoggerFactory.getLogger(EntityDaoTest.class);
    @Autowired
    private EntityDao<Attest> attestDao;

    private Attest concreteAttest() {
        Attest attest = new Attest();
        attest.setPartnerId("partnerId");
        attest.setSupplierId("supplierId");
        attest.setAttestId("attestId");
        attest.setFileName("fileName");
        attest.setFileHash("fileHash");
        attest.setFileId("fileId");
        attest.setFileTime(new Date());
        attest.setHashAlgorithm("SHA-1");
        attest.setRemark("remark");
        attest.setRequestId(123L);
        attest.setStatus(1);
        attest.setFileLength(123L);
        attest.setVersion(0);
        return attest;
    }


    @Test public void save() throws Exception {
        Attest attest = concreteAttest();
        int i = attestDao.save(attest);
        assertEquals(1, i);
        assertNotNull(attest.getId());
    }

    @Test public void batchSaveArray() throws Exception {
        Attest[] attests = {concreteAttest(), concreteAttest(), concreteAttest(), concreteAttest()};
        int i = attestDao.batchSave(attests);
        assertEquals(2, i);
        for (int j = 0; j < attests.length; j++) {
            Attest attest = attests[j];
            assertNotNull(attest.getId());
        }
    }

    @Test public void batchSaveCollection() throws Exception {
        Attest[] attests = {concreteAttest(), concreteAttest()};
        int i = attestDao.batchSave(Arrays.asList(attests));
        assertEquals(2, i);
        for (int j = 0; j < attests.length; j++) {
            Attest attest = attests[j];
            assertNotNull(attest.getId());
        }
    }

    private Attest getLatestOne() {
        List<Attest> attests = attestDao.listAll(Order.asc("id"));
        if (attests != null && attests.size() > 0) {
            return attests.get(attests.size() - 1);
        }
        return null;
    }

    private List<Attest> getLatestSome() {
        List<Attest> attests = attestDao.listAll(Order.asc("id"));
        if (attests != null && attests.size() >= 2) {
            return attests.subList(attests.size()-2, attests.size());
        }
        return attests;
    }

    private void doUpdate(Attest attest) {
        Long id = attest.getId();
        attest.setStatus(2);
        attest.setPartnerId("XNZX");
        attest.setRequestId(id);
        attest.setRemark("更新过");
        attest.setHashAlgorithm("MD5");
        attest.setFileId("FILE-" + id);
        attest.setAttestId("Attest-" + id);
        attest.setFileHash(DigestUtils.md5DigestAsHex((id + "").getBytes()));
        attest.setFileLength(id);
        attest.setFileName("file-" + id);
        attest.setSupplierId("ATTCLOUD");
        attest.setFileTime(new Date());
    }

    @Test public void update() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            doUpdate(attest);
            int c = attestDao.update(attest);
            assertEquals(1, c);
        } else {
            fail("no data to test");
        }
    }


    @Test public void updateOptimistic() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            doUpdate(attest);
            int c = attestDao.updateOptimistic(attest);
            assertEquals(1, c);

            try {
                c = attestDao.updateOptimistic(attest);
                assertNotEquals(1, c);
            } catch (Exception e) {
                assertTrue(e instanceof OptimisticLockingFailureException);
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void batchUpdate() throws Exception {
        List<Attest> attests = getLatestSome();
        if (attests != null && attests.size() > 0) {
            for (Attest attest : attests) {
                doUpdate(attest);
            }
            int c = attestDao.batchUpdate(attests);
            assertEquals(attests.size(), c);
        } else {
            fail("no data to test");
        }
    }


    @Test public void delete() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            int c = attestDao.delete(attest);
            assertEquals(1, c);
        } else {
            fail("no data to test");
        }
    }


    @Test public void batchDelete() throws Exception {
        List<Attest> attests = getLatestSome();
        if (attests != null && attests.size() > 0) {
            attests = attests.subList(0, 2);
            int c = attestDao.batchDelete(attests);
            assertEquals(2, c);
        } else {
            fail("no data to test");
        }
    }


    @Test public void deleteById() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            int c = attestDao.deleteById(attest.getId());
            assertEquals(1, c);
        } else {
            fail("no data to test");
        }
    }

    @Test public void get() throws Exception {
        Attest attest1 = getLatestOne();
        if (attest1 != null) {
            Attest attest2 = attestDao.get(attest1.getId());
            assertEquals(attest1.getId(), attest2.getId());
        } else {
            fail("no data to test");
        }
    }

    @Test public void getOneBy() throws Exception {
        Attest attest1 = getLatestOne();
        if (attest1 != null) {
            Attest attest2 = attestDao.getOneBy("id", attest1.getId());
            assertEquals(attest1.getId(), attest2.getId());
        } else {
            fail("no data to test");
        }
    }

    @Test public void getOneByMultiProperties() throws Exception {
        Attest attest1 = getLatestOne();
        if (attest1 != null) {
            Attest attest2 = attestDao.getOneBy(
                new String[] {"partnerId", "supplierId", "requestId", "fileId", "fileName", "fileLength",
                    "hashAlgorithm", "fileHash", "fileTime", "attestId", "status", "version", "remark"},
                new Object[] {attest1.getPartnerId(), attest1.getSupplierId(), attest1.getRequestId(), attest1.getFileId(), attest1.getFileName(),
                    attest1.getFileLength(), attest1.getHashAlgorithm(), attest1.getFileHash(), attest1.getFileTime(), attest1.getAttestId(),
                    attest1.getStatus(), attest1.getVersion(), attest1.getRemark()});
            assertEquals(attest1.getId(), attest2.getId());
        } else {
            fail("no data to test");
        }
    }

    @Test public void getOneByTemplate() throws Exception {
        Attest attest1 = getLatestOne();
        if (attest1 != null) {
            Long id = attest1.getId();
            attest1.setId(null);
            Attest attest2 = attestDao.getOneByTemplate(attest1);
            assertEquals(id, attest2.getId());
        } else {
            fail("no data to test");
        }

    }

    @Test public void getUniqueBy() throws Exception {
        Attest attest1 = getLatestOne();
        if (attest1 != null) {
            Attest attest2 = attestDao.getUniqueBy("id", attest1.getId());
            assertEquals(attest1.getId(), attest2.getId());

            try {
                attestDao.getUniqueBy("partnerId", attest1.getPartnerId());
                fail("这里应该报错");
            } catch (Exception e) {
                assertTrue(e instanceof IncorrectResultSizeDataAccessException);
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void getUniqueBy1() throws Exception {
        Attest attest1 = getLatestOne();
        if (attest1 != null) {
            Attest attest2 = attestDao.getUniqueBy(
                new String[] {"partnerId", "supplierId", "requestId", "fileId", "fileName", "fileLength",
                    "hashAlgorithm", "fileHash", "fileTime", "attestId", "status", "version", "remark"},
                new Object[] {attest1.getPartnerId(), attest1.getSupplierId(), attest1.getRequestId(), attest1.getFileId(), attest1.getFileName(),
                    attest1.getFileLength(), attest1.getHashAlgorithm(), attest1.getFileHash(), attest1.getFileTime(), attest1.getAttestId(),
                    attest1.getStatus(), attest1.getVersion(), attest1.getRemark()});
            assertEquals(attest1.getId(), attest2.getId());

            try {
                attestDao.getUniqueBy(new String[]{"partnerId", "status"}, new Object[]{attest1.getPartnerId(), attest1.getStatus()});
                fail("这里应该报错");
            } catch (Exception e) {
                assertTrue(e instanceof IncorrectResultSizeDataAccessException);
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void getUniqueByTemplate() throws Exception {
        Attest attest1 = getLatestOne();
        if (attest1 != null) {
            Long id = attest1.getId();
            attest1.setId(null);
            Attest attest2 = attestDao.getUniqueByTemplate(attest1);
            assertEquals(id, attest2.getId());
        } else {
            fail("no data to test");
        }
    }

    @Test public void listAll() throws Exception {
        List<Attest> attests = attestDao.listAll(Order.asc("id"));
        assertTrue(attests != null && attests.size() > 0);
    }


    @Test public void listBy() throws Exception {
        List<Attest> attests1 = attestDao.listAll(Order.asc("id"));
        if (attests1 != null && attests1.size() > 0) {
            Attest attest = attests1.get(0);
            List<Attest> attests = attestDao.listBy("partnerId", attest.getPartnerId(), Order.asc("fileHash"));
            assertEquals(attests1.size(), attests.size());

            String fileHash = "";
            for (Attest check : attests) {
                assertEquals(attest.getPartnerId(), check.getPartnerId());
                assertEquals(attest.getStatus(), check.getStatus());
                assertTrue(check.getFileHash().compareTo(fileHash) > 0);
                fileHash = check.getFileHash();
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void listBy1() throws Exception {
        List<Attest> attests1 = attestDao.listAll(Order.asc("id"));
        if (attests1 != null && attests1.size() > 0) {
            Attest attest = attests1.get(0);
            List<Attest> attests = attestDao.listBy(
                new String[]{"partnerId", "status"},
                new Object[]{attest.getPartnerId(), attest.getStatus()},
                Order.asc("fileHash"));
            assertTrue(attests != null && attests.size() > 0 && attests.size() <= attests1.size());
            String fileHash = "";
            for (Attest check : attests) {
                assertEquals(attest.getPartnerId(), check.getPartnerId());
                assertEquals(attest.getStatus(), check.getStatus());
                assertTrue(check.getFileHash().compareTo(fileHash) > 0);
                fileHash = check.getFileHash();
            }
        } else {
            fail("no data to test");
        }
    }

    @Test public void listByTemplate() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            Attest template = new Attest();
            template.setPartnerId(attest.getPartnerId());
            template.setStatus(attest.getStatus());
            List<Attest> attests = attestDao.listByTemplate(template, Order.asc("fileHash"));
            assertTrue(attests != null && attests.size() > 1 );
            String fileHash = "";
            for (Attest check : attests) {
                assertEquals(attest.getPartnerId(), check.getPartnerId());
                assertEquals(attest.getStatus(), check.getStatus());
                assertTrue(check.getFileHash().compareTo(fileHash) > 0);
                fileHash = check.getFileHash();
            }
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void page() throws Exception {
        PageRequest pageable = new PageRequest(1, 5, PageMode.INFINITE);
        Page<Attest> page = attestDao.page(pageable);
        logger.info(page.toString());
        assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);

        pageable = new PageRequest(1, 5, PageMode.TOTAL);
        page = attestDao.page(pageable);
        logger.info(page.toString());
        assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);
    }

    @Test public void pageByTemplate() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            Attest template = new Attest();
            template.setPartnerId(attest.getPartnerId());
            template.setStatus(attest.getStatus());

            PageRequest pageable = new PageRequest(1, 5, PageMode.INFINITE);
            Page<Attest> page = attestDao.pageByTemplate(pageable, template, Order.asc("fileHash"));
            logger.info(page.toString());
            assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);

            pageable = new PageRequest(1, 5, PageMode.TOTAL);
            page = attestDao.pageByTemplate(pageable, template, Order.asc("fileHash"));
            logger.info(page.toString());
            assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void pageBy() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            Attest template = new Attest();
            template.setPartnerId(attest.getPartnerId());
            template.setStatus(attest.getStatus());

            PageRequest pageable = new PageRequest(1, 5, PageMode.INFINITE);
            Page<Attest> page = attestDao.pageBy(
                pageable,
                new String[]{"partnerId", "status"},
                new Object[]{attest.getPartnerId(), attest.getStatus()},
                Order.asc("fileHash"));
            logger.info(page.toString());
            assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);

            pageable = new PageRequest(1, 5, PageMode.TOTAL);
            page = attestDao.pageBy(
                pageable,
                new String[]{"partnerId", "status"},
                new Object[]{attest.getPartnerId(), attest.getStatus()},
                Order.asc("fileHash"));
            logger.info(page.toString());
            assertTrue(page != null && page.size() <= 5 && page.getPageNumber() == 1);
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void count() throws Exception {
        int count = attestDao.count();
        assertTrue(count > 0);
    }

    @Test public void countBy() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            int count = attestDao.count("parterId", attest.getPartnerId());
            assertTrue(count > 0);
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void countByMulitiProperties() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            int count = attestDao.count(
                new String[]{"partnerId", "status"},
                new Object[]{attest.getPartnerId(), attest.getStatus()}
            );
            assertTrue(count > 0);
        }
        else {
            fail("no data to test");
        }
    }

    @Test public void countByTemplate() throws Exception {
        Attest attest = getLatestOne();
        if (attest != null) {
            Attest template = new Attest();
            template.setPartnerId(attest.getPartnerId());
            template.setStatus(attest.getStatus());

            int count = attestDao.countByTemplate(template);
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
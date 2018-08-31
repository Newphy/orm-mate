package cn.newphy.orm.demo.service.impl;

import cn.newphy.mate.EntityDao;
import cn.newphy.orm.demo.entity.Attest;
import cn.newphy.orm.demo.entity.User;
import cn.newphy.orm.demo.service.AttestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Newphy
 * @createTime 2018/8/8
 */
@Service
public class AttestServiceImpl implements AttestService {

    @Autowired
    private EntityDao<Attest> attestDao;
    @Autowired
    private EntityDao<User> userDao;

    @Override public void save(Attest attest) {
        attestDao.save(attest);
    }


    public static void main(String[] args) {
        String s1 = "张翼德NBFW";
        String s2 = "张翼德jM6S";
        System.out.println(s1.compareTo(s2));
    }
}

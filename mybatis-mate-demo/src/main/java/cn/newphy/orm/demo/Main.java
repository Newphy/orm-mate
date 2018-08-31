package cn.newphy.orm.demo;

import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @creater Newphy
 * @createTime 2018/7/25
 */
public class Main {

    private static volatile boolean running = true;

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);

        try {
            final AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("spring-bean.xml");
            ctx.registerShutdownHook();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    ctx.stop();
                    ctx.close();
                    synchronized (Main.class) {
                        Main.class.notify();
                    }
                }
            });
            logger.info("Service start success!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
        synchronized (Main.class) {
            while (running) {
                try {
                    Main.class.wait();
                } catch (Throwable e) {
                }
            }
        }

    }


}

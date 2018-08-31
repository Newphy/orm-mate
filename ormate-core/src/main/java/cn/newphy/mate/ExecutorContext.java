package cn.newphy.mate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Newphy
 * @createTime 2018/8/30
 */
public class ExecutorContext {
    private static ThreadLocal<ExecutorContext> tl = new ThreadLocal<>();

    public static ExecutorContext getExecutorContext() {
        ExecutorContext context = tl.get();
        if (context == null) {
            context = new ExecutorContext();
            tl.set(context);
        }
        return context;
    }

    public static void clear() {
        tl.remove();
    }

    private AtomicInteger counter = new AtomicInteger(0);

    private ExecutorContext() {
    }

    public int getIndex() {
        return counter.incrementAndGet();
    }
}

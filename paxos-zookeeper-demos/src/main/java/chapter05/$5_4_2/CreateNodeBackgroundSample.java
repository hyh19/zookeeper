package chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.zookeeper.CreateMode.PERSISTENT;

/**
 * 5.4.2 清单 5-33 Curator 异步 API 使用示例
 */
public class CreateNodeBackgroundSample {

    private static final CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    private static final CountDownLatch semaphore = new CountDownLatch(2);
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {

        client.start();
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // 传入自定义的 Executor
        String apple = "/apple";
        client.create().creatingParentsIfNeeded().withMode(PERSISTENT)
                .inBackground((client, event) -> {
                    System.out.println("path: " + apple + " event[code: " + event.getResultCode() + ", type: " + event.getType() + "]");
                    System.out.println("path: " + apple + " Thread of processResult: " + Thread.currentThread().getName());
                    semaphore.countDown();
                }, threadPool).forPath(apple, "apple".getBytes());


        // 没有传入自定义的 Executor
        String banana = "/banana";
        client.create().creatingParentsIfNeeded().withMode(PERSISTENT)
                .inBackground((client, event) -> {
                    System.out.println("path: " + banana + " event[code: " + event.getResultCode() + ", type: " + event.getType() + "]");
                    System.out.println("path: " + banana + " Thread of processResult: " + Thread.currentThread().getName());
                    semaphore.countDown();
                }).forPath("/banana", "banana".getBytes());

        semaphore.await();

        threadPool.shutdown();
    }
}

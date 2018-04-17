package chapter05.$5_4_2;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 5.4.2 清单 5-21 使用 Curator 创建会话
 */
public class CreateSessionSample {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", 5000,
                3000, retryPolicy);
        client.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}

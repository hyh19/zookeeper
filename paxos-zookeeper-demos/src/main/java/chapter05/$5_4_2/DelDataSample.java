package chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 5.4.2 清单 5-26 Curator 删除节点 API 示例
 */
public class DelDataSample {

    static String path = "/zk-book/c1";

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {

        client.start();
        /**
         * 创建测试节点
         * create /zk-book hello
         * create /zk-book/c1 world
         */
        client.delete().deletingChildrenIfNeeded().forPath(path);

        Thread.sleep(Integer.MAX_VALUE);
    }
}

package chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;

/**
 * 5.4.2 清单 5-24 Curator 创建节点 API 示例
 */
public class CreateNodeSample {

    private static final CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {

        client.start();
        client.create()
                .creatingParentsIfNeeded() // 自动创建父节点
                .withMode(EPHEMERAL)
                .forPath("/fruit/apple", "apple".getBytes());

        Thread.sleep(Integer.MAX_VALUE);
    }
}

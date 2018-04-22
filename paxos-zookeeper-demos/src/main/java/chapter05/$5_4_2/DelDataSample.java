package chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 5.4.2 清单 5-26 Curator 删除节点 API 示例
 */
public class DelDataSample {

    private static final CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {

        client.start();
        /*
          创建测试节点
          create /fruit fruit
          create /fruit/apple apple
         */
        client.delete().deletingChildrenIfNeeded().forPath("/fruit/apple");

        Thread.sleep(Integer.MAX_VALUE);
    }
}

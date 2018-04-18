package chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 5.4.2 清单 5-35 NodeCache 使用示例
 */
public class NodeCacheSample {

    static String path = "/pet";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("domain1.book.zookeeper:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {
        /**
         * 创建节点 create /pet dog
         * 更新数据 set /pet cat
         * 更新数据 set /pet pig
         * 删除节点 delete /pet
         */
        client.start();
        final NodeCache cache = new NodeCache(client, path, false);
        // true 表示第一次启动时立即读取节点的数据，并保存在 Cache 中。
        cache.start(true);
        // 注册监听器
        cache.getListenable().addListener(() -> {
            // 数据变更通知
            System.out.println("Node data update, new data: " +
                    new String(cache.getCurrentData().getData()));
        });

        Thread.sleep(Integer.MAX_VALUE);
    }
}

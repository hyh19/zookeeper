package chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * 5.4.2 清单 5-30 Curator 更新数据 API 示例
 */
public class SetDataSample {

    private static final CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {

        /*
          创建测试节点
          create /fruit pear
         */
        client.start();

        // 获取当前数据版本，后面测试异常情况要用到。
        Stat stat = new Stat();
        String path = "/fruit";
        client.getData().storingStatIn(stat).forPath(path);

        // 更新获取到的版本的数据，成功后数据版本将发生改变。
        client.setData().withVersion(stat.getVersion()).forPath(path, "apple".getBytes());

        try {
            // 更新已过期的版本的数据，将出现异常。
            client.setData().withVersion(stat.getVersion()).forPath(path, "banana".getBytes());
        } catch (Exception e) {
            System.out.println(e); // BadVersionException: KeeperErrorCode = BadVersion for /fruit
        }

        Thread.sleep(Integer.MAX_VALUE);
    }
}

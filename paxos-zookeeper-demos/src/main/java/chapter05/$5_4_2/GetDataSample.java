package chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * 5.4.2 清单 5-28 Curator 读取数据 API 示例
 */
public class GetDataSample {

    private static final CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {

        /*
          创建测试节点
          create /fruit orange
         */
        client.start();
        Stat stat = new Stat();
        byte[] data = client.getData().storingStatIn(stat).forPath("/fruit");
        System.out.println("data: " + new String(data));
        System.out.println("stat: " + stat);

        Thread.sleep(Integer.MAX_VALUE);
    }
}

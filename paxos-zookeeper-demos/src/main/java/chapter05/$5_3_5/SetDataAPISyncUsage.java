package chapter05.$5_3_5;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.EventType.None;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

/**
 * 5.3.5 清单 5-10 使用同步 API 更新节点数据内容
 */
public class SetDataAPISyncUsage {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    if (SyncConnected == event.getState()) {
                        // 成功连接服务器
                        if (None == event.getType() && null == event.getPath()) {
                            // 解除阻塞
                            connectedSemaphore.countDown();
                        }
                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        // 创建测试节点 create /fruit apple
        String path = "/fruit";

        // 第一次更新
        Stat stat1 = zooKeeper.setData(path, "banana".getBytes(), -1);
        System.out.println("第一次更新 " + stat1);

        // 第二次更新，对上次记录的版本进行更新（注意：上一版本未必是最新的版本，因为别的客户端也在更新数据）。
        Stat stat2 = zooKeeper.setData(path, "pear".getBytes(), stat1.getVersion());
        System.out.println("第二次更新 " + stat2);

        // 第二次更新后，数据版本已经发生了变化，下面的更新仍然针对第一次记录的版本，会出现错误。
        try {
            zooKeeper.setData(path, "grape".getBytes(), stat1.getVersion());
        } catch (KeeperException e) {
            System.out.println("第三次更新 " + e);
        }

        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

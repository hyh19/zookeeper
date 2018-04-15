package chapter05.$5_3_5;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.5 清单 5-10 使用同步 API 更新节点数据内容
 */
public class SetDataAPISyncUsage {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        // 成功连接服务器
                        if (Watcher.Event.EventType.None == event.getType() && null == event.getPath()) {
                            // 解除阻塞
                            connectedSemaphore.countDown();
                        }
                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        // 在服务器创建测试节点 create /zk-test Hello
        String path = "/zk-test";

        // 第一次更新
        Stat stat1 = zooKeeper.setData(path, "World-1".getBytes(), -1);
        System.out.println("第一次更新 " + stat1.getCzxid() + " " +
                stat1.getMzxid() + " " +
                stat1.getVersion());

        // 第二次更新，对上次记录的版本进行更新（注意：上一版本未必是最新的版本，因为别的客户端也在更新数据）。
        Stat stat2 = zooKeeper.setData(path, "World-2".getBytes(), stat1.getVersion());
        System.out.println("第二次更新 " + stat2.getCzxid() + ", " +
                stat2.getMzxid() + ", " +
                stat2.getVersion());

        // 第二次更新后，数据版本已经发生了变化，下面的更新仍然针对第一次更新后记录的版本，会出现错误。
        try {
            zooKeeper.setData(path, "World-3".getBytes(), stat1.getVersion());
        } catch (KeeperException e) {
            System.out.println("Error " + e.code() + ", " + e.getMessage());
        }

        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

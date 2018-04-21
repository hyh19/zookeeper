package chapter05.$5_3_2;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.*;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * 5.3.2 清单 5-4 使用同步 API 创建一个节点
 */
public class ZooKeeperCreateAPISyncUsage {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("localhost:2181",
                5000, event -> {
            if (SyncConnected == event.getState()) {
                connectedSemaphore.countDown();
            }
        });

        connectedSemaphore.await();

        // 创建临时节点
        zookeeper.create("/ephemeral", "cat".getBytes(), OPEN_ACL_UNSAFE,
                EPHEMERAL);

        // 创建临时顺序节点
        zookeeper.create("/ephemeral-sequential-", "dog".getBytes(), OPEN_ACL_UNSAFE,
                EPHEMERAL_SEQUENTIAL);

        // 创建持久节点
        zookeeper.create("/persistent", "tiger".getBytes(), OPEN_ACL_UNSAFE,
                PERSISTENT);

        // 创建持久顺序节点
        zookeeper.create("/persistent-sequential-", "pig".getBytes(), OPEN_ACL_UNSAFE,
                PERSISTENT_SEQUENTIAL);

        Thread.sleep(Integer.MAX_VALUE);
    }
}

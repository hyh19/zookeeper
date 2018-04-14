package chapter05.$5_3_2;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.2 清单 5-4 使用同步 API 创建一个节点
 */
public class ZooKeeperCreateAPISyncUsage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("domain1.book.zookeeper:2181",
                5000, new ZooKeeperCreateAPISyncUsage());

        connectedSemaphore.await();

        // 创建临时节点
        String path1 = zookeeper.create("/zk-test-ephemeral", "ephemeral znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        System.out.println("Success create znode: " + path1);

        // 创建临时顺序节点
        String path2 = zookeeper.create("/zk-test-ephemeral-", "ephemeral sequential znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Success create znode: " + path2);

        // 创建持久节点
        String path3 = zookeeper.create("/zk-test-persistent", "persistent znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        System.out.println("Success create znode: " + path3);

        // 创建持久顺序节点
        String path4 = zookeeper.create("/zk-test-persistent-", "persistent sequential znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println("Success create znode: " + path4);

    }

    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}

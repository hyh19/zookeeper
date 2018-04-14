package chapter05.$5_3_2;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.2 清单 5-5 使用异步 API 创建一个节点
 */
public class ZooKeeperCreateAPIASyncUsage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("domain1.book.zookeeper:2181",
                5000, new ZooKeeperCreateAPIASyncUsage());

        connectedSemaphore.await();

        zookeeper.create("/zk-test-ephemeral-", "ephemeral znode".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                new IStringCallback(), "I am context.");

        // 节点已经存在，将返回一个错误码。
        zookeeper.create("/zk-test-ephemeral-", "ephemeral znode".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                new IStringCallback(), "I am context.");

        zookeeper.create("/zk-test-ephemeral-", "ephemeral sequential znode".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
                new IStringCallback(), "I am context.");

        Thread.sleep(Integer.MAX_VALUE);
    }

    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}

class IStringCallback implements AsyncCallback.StringCallback {
    public void processResult(int rc, String path, Object ctx, String name) {
        if (rc == KeeperException.Code.OK.intValue()) {
            System.out.println("Create path result: [" + rc + ", " + path + ", "
                    + ctx + ", real path name: " + name);
        } else if (rc == KeeperException.Code.NODEEXISTS.intValue()) {
            System.out.println("节点已经存在");
        }
    }
}

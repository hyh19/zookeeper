package chapter05.$5_3_2;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;
import static org.apache.zookeeper.KeeperException.Code.NODEEXISTS;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

/**
 * 5.3.2 清单 5-5 使用异步 API 创建一个节点
 */
public class ZooKeeperCreateAPIAsyncUsage {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("localhost:2181",
                5000, event -> {
            if (SyncConnected == event.getState()) {
                connectedSemaphore.countDown();
            }
        });

        connectedSemaphore.await();

        AsyncCallback.StringCallback callback = (rc, path, ctx, name) -> {
            if (rc == KeeperException.Code.OK.intValue()) {
                // 顺序节点的 name 只有创建成功后才确定下来
                System.out.println("节点创建成功 [" + rc + ", " + path + ", "
                        + ctx + ", " + name);
            } else if (rc == NODEEXISTS.intValue()) {
                System.out.println("节点已经存在");
            }
        };

        zookeeper.create("/ephemeral", "cat".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, EPHEMERAL,
                callback, "I am context.");

        // 节点已经存在，将返回一个错误码。
        zookeeper.create("/ephemeral", "dog".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, EPHEMERAL,
                callback, "I am context.");

        // 临时顺序节点
        zookeeper.create("/ephemeral-sequential-", "pig".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL,
                callback, "I am context.");

        Thread.sleep(Integer.MAX_VALUE);
    }
}

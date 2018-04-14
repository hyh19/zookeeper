package chapter05.$5_3_1;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.1 清单 5-3 复用 sessionId 和 sessionPasswd 来创建一个 ZooKeeper 对象实例
 */
public class ZooKeeperConstructorUsageWithSIDPASSWD implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("domain1.book.zookeeper:2181", 5000,
                new ZooKeeperConstructorUsageWithSIDPASSWD());

        connectedSemaphore.await();

        long sessionId = zooKeeper.getSessionId();
        byte[] passwd = zooKeeper.getSessionPasswd();

        // 使用错误的会话 ID 和 密码，服务器将返回连接超时事件。
        zooKeeper = new ZooKeeper("domain1.book.zookeeper:2181", 5000,
                new ZooKeeperConstructorUsageWithSIDPASSWD(), 1l, "wrong_passwd".getBytes());

        // 使用正确的会话 ID 和 密码，服务器将返回连接成功事件。
        zooKeeper = new ZooKeeper("domain1.book.zookeeper:2181", 5000,
                new ZooKeeperConstructorUsageWithSIDPASSWD(), sessionId, passwd);

        Thread.sleep(Integer.MAX_VALUE);
    }

    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}

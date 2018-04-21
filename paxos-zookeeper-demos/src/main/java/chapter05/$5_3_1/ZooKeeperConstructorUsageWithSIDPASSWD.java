package chapter05.$5_3_1;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.1 清单 5-3 复用 sessionId 和 sessionPasswd 来创建一个 ZooKeeper 对象实例
 */
public class ZooKeeperConstructorUsageWithSIDPASSWD {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        String connectString = "localhost:2181";

        // 会话一
        ZooKeeper zooKeeper = new ZooKeeper(connectString, 5000,
                event -> {
                    System.out.println("会话一 " + event);
                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        connectedSemaphore.countDown();
                    }
                });

        connectedSemaphore.await();

        long sessionId = zooKeeper.getSessionId();
        byte[] passwd = zooKeeper.getSessionPasswd();

        // 会话二，使用错误的会话 ID 和 密码，服务器将返回连接超时事件。
        zooKeeper = new ZooKeeper(connectString, 5000,
                event -> {
                    System.out.println("会话二 " + event);
                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        connectedSemaphore.countDown();
                    }
                }, 1L, "apple".getBytes());

        // 会话三，使用正确的会话 ID 和 密码，服务器将返回连接成功事件。
        zooKeeper = new ZooKeeper(connectString, 5000,
                event -> {
                    System.out.println("会话三 " + event);
                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        connectedSemaphore.countDown();
                    }
                }, sessionId, passwd);

        Thread.sleep(Integer.MAX_VALUE);
    }
}

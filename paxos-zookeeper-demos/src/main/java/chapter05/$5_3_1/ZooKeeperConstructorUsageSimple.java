package chapter05.$5_3_1;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.1 清单 5-2 创建一个最基本的 ZooKeeper 会话实例
 */
public class ZooKeeperConstructorUsageSimple implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("domain1.book.zookeeper:2181", 5000,
                new ZooKeeperConstructorUsageSimple());

        System.out.println(zooKeeper.getState());

        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
        }
        System.out.println("ZooKeeper session established.");
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        // 收到会话连接成功通知
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}

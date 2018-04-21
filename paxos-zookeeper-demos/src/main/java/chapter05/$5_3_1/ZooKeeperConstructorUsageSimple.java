package chapter05.$5_3_1;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.1 清单 5-2 创建一个最基本的 ZooKeeper 会话实例
 */
public class ZooKeeperConstructorUsageSimple {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {
                    System.out.println("Receive watched event：" + event);
                    // 收到会话连接成功通知
                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        // 解除阻塞
                        connectedSemaphore.countDown();
                    }

                });

        System.out.println(zooKeeper.getState());

        try {
            // 阻塞线程，等待会话连接成功。
            connectedSemaphore.await();
        } catch (InterruptedException e) {
        }

        System.out.println("ZooKeeper session established.");
    }
}

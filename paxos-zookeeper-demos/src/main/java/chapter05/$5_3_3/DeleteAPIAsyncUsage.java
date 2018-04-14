package chapter05.$5_3_3;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.3 使用异步方式删除节点
 */
public class DeleteAPIAsyncUsage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("domain1.book.zookeeper:2181",
                5000, new DeleteAPIAsyncUsage());

        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
        }
        System.out.println("ZooKeeper session established.");

        // 在服务器上直接用命令行创建节点 create /zk-test Hello
        // -1 表示匹配任何版本
        zookeeper.delete("/zk-test", -1, (rc, path, ctx) -> System.out.println("Success delete znode: " + path), "Hello ZooKeeper");

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}

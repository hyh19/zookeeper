package chapter05.$5_3_3;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.3 使用同步方式删除节点
 */
public class DeleteAPISyncUsage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("domain1.book.zookeeper:2181",
                5000, new DeleteAPISyncUsage());

        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
        }
        System.out.println("ZooKeeper session established.");

        // 在服务器上直接用命令行创建节点 create /zk-test Hello
        String path = "/zk-test";
        // -1 表示匹配任何版本
        zookeeper.delete(path, -1);
        System.out.println("Success delete znode: " + path);
    }

    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}

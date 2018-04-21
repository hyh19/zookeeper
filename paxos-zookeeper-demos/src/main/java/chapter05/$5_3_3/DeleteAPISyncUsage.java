package chapter05.$5_3_3;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.3 使用同步方式删除节点
 */
public class DeleteAPISyncUsage {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("localhost:2181",
                5000, event -> {
            if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                connectedSemaphore.countDown();
            }
        });

        connectedSemaphore.await();

        // 创建测试节点 create -e /fruit apple
        // -1 表示匹配任何版本
        zookeeper.delete("/fruit", -1);

        Thread.sleep(Integer.MAX_VALUE);
    }
}

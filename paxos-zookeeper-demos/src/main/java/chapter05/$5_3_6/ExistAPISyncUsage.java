package chapter05.$5_3_6;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.6 清单 5-12 使用同步 API 检测节点是否存在
 */
public class ExistAPISyncUsage {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    try {
                        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                            // 成功连接服务器
                            if (Watcher.Event.EventType.None == event.getType() && null == event.getPath()) {
                                // 解除阻塞
                                connectedSemaphore.countDown();
                                // 节点创建
                            } else if (Watcher.Event.EventType.NodeCreated == event.getType()) {
                                System.out.println("Node " + event.getPath() + " Created");
                                zooKeeper.exists(event.getPath(), true);
                                // 节点删除
                            } else if (Watcher.Event.EventType.NodeDeleted == event.getType()) {
                                System.out.println("Node " + event.getPath() + " Deleted");
                                zooKeeper.exists(event.getPath(), true);
                                // 节点更新
                            } else if (Watcher.Event.EventType.NodeDataChanged == event.getType()) {
                                System.out.println("Node " + event.getPath() + " DataChanged");
                                zooKeeper.exists(event.getPath(), true);
                            }
                        }
                    } catch (Exception e) {

                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        /**
         * 开始时，服务器上是没有测试节点的 ls /zk-test Node does not exist: /zk-test
         * 观察以下事件通知：
         * 创建节点 create /zk-test Hello
         * 更新节点 set /zk-test World
         * 删除节点 delete /zk-test
         */
        String path = "/zk-test";

        Stat stat = zooKeeper.exists(path, true);
        System.out.println(stat);

        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

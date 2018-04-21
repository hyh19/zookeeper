package chapter05.$5_3_6;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.EventType.*;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

/**
 * 5.3.6 使用异步 API 检测节点是否存在
 */
public class ExistAPIAsyncUsage {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    try {
                        if (SyncConnected == event.getState()) {
                            // 成功连接服务器
                            if (None == event.getType() && null == event.getPath()) {
                                // 解除阻塞
                                connectedSemaphore.countDown();
                                // 节点创建
                            } else if (NodeCreated == event.getType()) {
                                System.out.println("Node " + event.getPath() + " Created");
                                // 需要重复监听
                                zooKeeper.exists(event.getPath(), true);
                                // 节点删除
                            } else if (NodeDeleted == event.getType()) {
                                System.out.println("Node " + event.getPath() + " Deleted");
                                zooKeeper.exists(event.getPath(), true);
                                // 节点更新
                            } else if (NodeDataChanged == event.getType()) {
                                System.out.println("Node " + event.getPath() + " DataChanged");
                                zooKeeper.exists(event.getPath(), true);
                            }
                        }
                    } catch (Exception ignored) {

                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        /*
          开始时，服务器上是没有测试节点的 ls /fruit Node does not exist: /fruit
          观察以下事件通知
          创建节点 create /fruit apple
          更新节点 set /fruit banana
          删除节点 delete /fruit
         */
        zooKeeper.exists("/fruit", true, (rc, path, ctx, stat) -> System.out.println("rc: " + rc + ", path: " + path + ", stat: " + stat), null);

        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

package chapter05.$5_3_5;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.5 清单 5-11 使用异步 API 更新节点数据内容
 */
public class SetDataAPIAsyncUsage {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        // 成功连接服务器
                        if (Watcher.Event.EventType.None == event.getType() && null == event.getPath()) {
                            // 解除阻塞
                            connectedSemaphore.countDown();
                        }
                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        // 在服务器创建测试节点 create /zk-test Hello
        zooKeeper.setData("/zk-test", "World".getBytes(), -1, (rc, path, ctx, stat) -> {
            if (rc == 0) {
                System.out.println(stat.getCzxid() + " " +
                        stat.getMzxid() + " " +
                        stat.getVersion());
            }
        }, null);


        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

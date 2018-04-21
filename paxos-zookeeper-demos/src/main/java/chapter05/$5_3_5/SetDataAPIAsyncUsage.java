package chapter05.$5_3_5;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.KeeperException.Code.OK;
import static org.apache.zookeeper.Watcher.Event.EventType.None;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

/**
 * 5.3.5 清单 5-11 使用异步 API 更新节点数据内容
 */
public class SetDataAPIAsyncUsage {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    if (SyncConnected == event.getState()) {
                        // 成功连接服务器
                        if (None == event.getType() && null == event.getPath()) {
                            // 解除阻塞
                            connectedSemaphore.countDown();
                        }
                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        // 创建测试节点 create /fruit apple
        zooKeeper.setData("/fruit", "banana".getBytes(), -1,
                (rc, path, ctx, stat) -> {
                    if (OK.intValue() == rc) {
                        System.out.println(stat);
                    }
                }, null);


        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

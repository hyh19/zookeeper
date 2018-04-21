package chapter05.$5_3_4;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeDataChanged;
import static org.apache.zookeeper.Watcher.Event.EventType.None;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

/**
 * 5.3.4 清单 5-9 使用异步 API 获取节点数据内容
 */
public class GetDataAPIAsyncUsage {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {

        DataCallback callback = (rc, path, ctx, data, stat) -> {
            System.out.println(ctx);
            System.out.println("data: " + rc + ", " + path + ", " + new String(data));
            System.out.println("stat: " + stat);
        };

        String path = "/animal";

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    if (SyncConnected == event.getState()) {
                        // 成功连接服务器
                        if (None == event.getType() && null == event.getPath()) {
                            // 解除阻塞
                            connectedSemaphore.countDown();
                            // 节点数据变更
                        } else if (NodeDataChanged == event.getType()) {
                            try {
                                // 重新读取节点数据
                                zooKeeper.getData(path, true, callback, "重新读取节点数据");
                            } catch (Exception ignored) {
                            }
                        }
                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        /*
        * 创建测试节点 create /animal cat
        * 程序运行过程中修改节点数据
        * set /animal cat
        * set /animal dog
        * 观察子节点变更通知
        * */
        zooKeeper.getData(path, true, callback, "第一次读取节点数据");

        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

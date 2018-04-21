package chapter05.$5_3_4;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeDataChanged;
import static org.apache.zookeeper.Watcher.Event.EventType.None;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

/**
 * 5.3.4 清单 5-8 使用同步 API 获取节点数据内容
 */
public class GetDataAPISyncUsage {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;
    private static final Stat stat = new Stat();

    public static void main(String[] args) throws Exception {

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
                                byte[] data = zooKeeper.getData(event.getPath(), true, stat);
                                System.out.println("重新读取节点数据 " + new String(data));
                                System.out.println("重新读取节点状态信息 " + stat);
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
        String path = "/animal";
        byte[] data = zooKeeper.getData(path, true, stat);
        System.out.println("第一次读取节点数据 " + new String(data));
        System.out.println("第一次读取节点状态信息 " + stat);

        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

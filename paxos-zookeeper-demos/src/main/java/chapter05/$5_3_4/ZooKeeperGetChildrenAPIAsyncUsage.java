package chapter05.$5_3_4;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeChildrenChanged;
import static org.apache.zookeeper.Watcher.Event.EventType.None;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

/**
 * 5.3.4 清单 5-7 使用异步 API 获取子节点列表
 */
public class ZooKeeperGetChildrenAPIAsyncUsage {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    if (SyncConnected == event.getState()) {
                        // 成功连接服务器
                        if (None == event.getType() && null == event.getPath()) {
                            // 解除阻塞
                            connectedSemaphore.countDown();
                            // 子节点变更
                        } else if (NodeChildrenChanged == event.getType()) {
                            try {
                                // 重新获取子节点
                                System.out.println("重新获取子节点列表 " + zooKeeper.getChildren(event.getPath(), true));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        /*
        * 创建测试节点 create /fruit fruit
        * 程序运行过程中创建子节点（注意：临时节点不可以创建子节点）
        * create /fruit/apple apple
        * create /fruit/banana banana
        * 观察子节点变更通知
        * */
        zooKeeper.getChildren("/fruit", true,
                ((rc, path, ctx, children, stat) -> System.out.println("第一次获取子节点列表 [rc: " + rc + ", path: " + path
                        + ", ctx: " + ctx + ", children: " + children + ", stat: " + stat)),
                null);

        // 阻塞，不要让程序结束，因为要监听事件通知。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

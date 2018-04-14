package chapter05.$5_3_4;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 5.3.4 清单 5-8 使用同步 API 获取节点数据内容
 */
public class GetDataAPISyncUsage {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {

        zooKeeper = new ZooKeeper("localhost:2181", 5000,
                event -> {

                    System.out.println(event);

                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        // 成功连接服务器
                        if (Watcher.Event.EventType.None == event.getType() && null == event.getPath()) {
                            // 解除阻塞
                            connectedSemaphore.countDown();
                            // 节点数据变更
                        } else if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                            try {
                                // 重新获取节点数据
                                byte[] data = zooKeeper.getData(event.getPath(), true, stat);
                                System.out.println("重新获取节点数据：" + new String(data));
                                System.out.println("状态信息：" + stat.getCzxid() + " " +
                                        stat.getMzxid() + " " +
                                        stat.getVersion());
                            } catch (Exception e) {
                            }
                        }
                    }
                });

        // 阻塞，成功连接服务器后再解除。
        connectedSemaphore.await();

        /*
        * 在服务器创建测试节点 create /zk-test Hello
        * 程序运行中修改节点数据
        * set /zk-test World
        * 观察节点数据变化通知
        * */
        String path = "/zk-test";
        byte[] data = zooKeeper.getData(path, true, stat);
        System.out.println("第一次获取节点数据：" + new String(data));

        // 阻塞，不要让程序结束，因为要监听子节点的变化。
        Thread.sleep(Integer.MAX_VALUE);
    }
}

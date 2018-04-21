package chapter05.$5_3_7;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.apache.zookeeper.ZooDefs.Ids.CREATOR_ALL_ACL;

/**
 * 5.3.7 清单 5-16 删除节点接口的权限控制
 */
public class AuthSampleDelete {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private final static String connectString = "localhost:2181";
    private final static String FRUIT = "/fruit";
    private final static String FRUIT_APPLE = "/fruit/apple";

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper1 = new ZooKeeper(connectString, 5000, event -> {
            System.out.println("zookeeper1: " + event);
            connectedSemaphore.countDown();
        });
        connectedSemaphore.await();
        zookeeper1.addAuthInfo("digest", "tom:123456".getBytes());
        // 创建父节点
        zookeeper1.create(FRUIT, "fruit".getBytes(), CREATOR_ALL_ACL, PERSISTENT);
        // 创建子节点
        zookeeper1.create(FRUIT_APPLE, "apple".getBytes(), CREATOR_ALL_ACL, EPHEMERAL);

        // 未添加认证信息，删除子节点失败。
        ZooKeeper zookeeper2 = new ZooKeeper(connectString, 5000, event -> {
            System.out.println("zookeeper2: " + event);
            connectedSemaphore.countDown();
        });
        connectedSemaphore.await();
        try {
            zookeeper2.delete(FRUIT_APPLE, -1);
        } catch (Exception e) {
            System.out.println("删除【子】节点失败（未添加认证信息）" + e);
        }

        // 添加正确的认证信息，删除子节点成功。
        ZooKeeper zookeeper3 = new ZooKeeper(connectString, 5000, event -> {
            System.out.println("zookeeper3: " + event);
            connectedSemaphore.countDown();
        });
        connectedSemaphore.await();
        zookeeper3.addAuthInfo("digest", "tom:123456".getBytes());
        zookeeper3.delete(FRUIT_APPLE, -1);
        System.out.println("成功删除【子】节点（添加正确的认证信息）" + FRUIT_APPLE);

        // 删除父节点不需要认证
        ZooKeeper zookeeper4 = new ZooKeeper(connectString, 5000, event -> {
            System.out.println("zookeeper4: " + event);
            connectedSemaphore.countDown();
        });
        connectedSemaphore.await();
        zookeeper4.delete(FRUIT, -1);
        System.out.println("成功删除【父】节点（未添加认证信息）" + FRUIT);

        Thread.sleep(Integer.MAX_VALUE);
    }
}

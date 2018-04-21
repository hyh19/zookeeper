package chapter05.$5_3_7;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.ZooDefs.Ids.CREATOR_ALL_ACL;

/**
 * 5.3.7 访问含权限信息的数据节点
 */
public class AuthSampleGet {

    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private final static String PATH = "/fruit";
    private final static String connectString = "localhost:2181";

    public static void main(String[] args) throws Exception {

        // 创建第一个会话
        ZooKeeper zookeeper1 = new ZooKeeper(connectString, 5000,
                event -> {
                    System.out.println("zookeeper1: " + event);
                    connectedSemaphore.countDown();
                });

        connectedSemaphore.await();

        // 添加认证信息
        zookeeper1.addAuthInfo("digest", "tom:123456".getBytes());
        // 创建一个带 ACL 的临时节点
        zookeeper1.create(PATH, "apple".getBytes(), CREATOR_ALL_ACL, EPHEMERAL);

        // 创建第二个会话，不添加认证信息。
        ZooKeeper zookeeper2 = new ZooKeeper(connectString, 5000,
                event -> {
                    System.out.println("zookeeper2: " + event);
                    connectedSemaphore.countDown();
                });

        connectedSemaphore.await();

        try {
            zookeeper2.getData(PATH, false, null);
        } catch (Exception e) { // 认证不通过，抛出异常。
            System.out.println("不添加认证信息 " + e); // NoAuthException: KeeperErrorCode = NoAuth for /fruit
        }

        // 创建第三个会话，添加错误的认证信息。
        ZooKeeper zookeeper3 = new ZooKeeper(connectString, 5000,
                event -> {
                    System.out.println("zookeeper3: " + event);
                    connectedSemaphore.countDown();
                });

        connectedSemaphore.await();

        zookeeper3.addAuthInfo("digest", "tom:654321".getBytes());

        try {
            zookeeper3.getData(PATH, false, null);
        } catch (Exception e) { // 认证不通过，抛出异常。
            System.out.println("添加错误的认证信息 " + e); // NoAuthException: KeeperErrorCode = NoAuth for /fruit
        }

        // 创建第四个会话，添加正确的认证信息。
        ZooKeeper zookeeper4 = new ZooKeeper(connectString, 5000,
                event -> {
                    System.out.println("zookeeper4: " + event);
                    connectedSemaphore.countDown();
                });

        connectedSemaphore.await();

        zookeeper4.addAuthInfo("digest", "tom:123456".getBytes());

        try {
            System.out.println("添加正确的认证信息 " + new String(zookeeper4.getData(PATH, false, null))); // apple
        } catch (Exception e) {
            System.out.println(e);
        }

        Thread.sleep(Integer.MAX_VALUE);
    }
}

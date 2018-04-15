package chapter05.$5_3_7;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * 5.3.7 访问含权限信息的数据节点
 */
public class AuthSampleGet {

    final static String PATH = "/apple";

    public static void main(String[] args) throws Exception {

        // 创建第一个会话
        ZooKeeper zookeeper1 = new ZooKeeper("localhost:2181", 5000,
                event -> System.out.println("zookeeper1: " + event));

        // 添加认证信息
        zookeeper1.addAuthInfo("digest", "tom:123456".getBytes());
        // 创建一个带 ACL 的临时节点
        zookeeper1.create(PATH, "apple".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

        // 创建第二个会话，不添加认证信息。
        ZooKeeper zookeeper2 = new ZooKeeper("localhost:2181", 5000,
                event -> System.out.println("zookeeper2: " + event));
        try {
            zookeeper2.getData(PATH, false, null);
        } catch (Exception e) { // 认证不通过，抛出异常。

            System.out.println("不添加认证信息 " + e); // NoAuthException: KeeperErrorCode = NoAuth for /apple
        }

        // 创建第三个会话，添加错误的认证信息。
        ZooKeeper zookeeper3 = new ZooKeeper("localhost:2181", 5000,
                event -> System.out.println("zookeeper3: " + event));
        zookeeper3.addAuthInfo("digest", "tom:654321".getBytes());
        try {
            zookeeper3.getData(PATH, false, null);
        } catch (Exception e) { // 认证不通过，抛出异常。

            System.out.println("添加错误的认证信息 " + e); // NoAuthException: KeeperErrorCode = NoAuth for /apple
        }

        // 创建第四个会话，添加正确的认证信息。
        ZooKeeper zookeeper4 = new ZooKeeper("localhost:2181", 5000,
                event -> System.out.println("zookeeper4: " + event));
        zookeeper4.addAuthInfo("digest", "tom:123456".getBytes());
        try {
            System.out.println(new String(zookeeper4.getData(PATH, false, null))); // apple
        } catch (Exception e) {
            System.out.println(e);
        }

        Thread.sleep(Integer.MAX_VALUE);
    }
}

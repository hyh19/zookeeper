package chapter05.$5_3_7;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * 5.3.7 清单 5-13 使用包含权限信息的 ZooKeeper 会话创建数据节点
 */
public class AuthSample {

    private final static String PATH = "/fruit";

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("localhost:2181", 5000,
                System.out::println);

        // 添加认证信息，ID 是 tom，密码是 123456。
        zookeeper.addAuthInfo("digest", "tom:123456".getBytes());

        // 在 Java API 上添加了认证信息后，后续对节点的 ACL 操作会使用这些认证信息。
        // 创建临时节点并设置 ACL 为 CREATOR_ALL_ACL（表示创建者 tom 有全部权限）。
        zookeeper.create(PATH, "apple".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

        Thread.sleep(Integer.MAX_VALUE);
    }
}

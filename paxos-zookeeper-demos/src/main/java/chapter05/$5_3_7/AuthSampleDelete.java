package chapter05.$5_3_7;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * 5.3.7 清单 5-16 删除节点接口的权限控制
 */
public class AuthSampleDelete {

    final static String FRUIT = "/fruit";
    final static String FRUIT_APPLE = "/fruit/apple";

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper1 = new ZooKeeper("localhost:2181", 5000, null);
        zookeeper1.addAuthInfo("digest", "tom:123456".getBytes());
        zookeeper1.create(FRUIT, "fruit".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        zookeeper1.create(FRUIT_APPLE, "apple".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

        try {
            ZooKeeper zookeeper2 = new ZooKeeper("localhost:2181", 5000, null);
            zookeeper2.delete(FRUIT_APPLE, -1);
        } catch (Exception e) {
            System.out.println("删除节点失败（未添加认证信息）" + e.getMessage());
        }

        ZooKeeper zookeeper3 = new ZooKeeper("localhost:2181", 5000, null);
        zookeeper3.addAuthInfo("digest", "tom:123456".getBytes());
        zookeeper3.delete(FRUIT_APPLE, -1);
        System.out.println("成功删除节点（添加正确的认证信息）" + FRUIT_APPLE);

        ZooKeeper zookeeper4 = new ZooKeeper("localhost:2181", 5000, null);
        zookeeper4.delete(FRUIT, -1);
        System.out.println("成功删除节点（未添加认证信息）" + FRUIT);

        Thread.sleep(Integer.MAX_VALUE);
    }
}

package chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode.POST_INITIALIZED_EVENT;

/**
 * 5.4.2 清单 5-37 PathChildrenCache 使用示例
 */
public class PathChildrenCacheSample {

    private static final CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {

        client.start();

        /*
          创建测试节点 create /fruit fruit
          程序运行过程中
          创建子节点 create /fruit/apple apple
          更新子节点 set /fruit/apple apple2
          删除子节点 delete /fruit/apple
          更新节点本身的数据 set /fruit fruit2
          创建二级子节点
          create /fruit/banana banana
          create /fruit/banana/japan_banana japan_banana
          注意观察事件通知
         */
        PathChildrenCache cache = new PathChildrenCache(client, "/fruit", true);
        cache.start(POST_INITIALIZED_EVENT);
        cache.getListenable().addListener((client, event) -> {
            switch (event.getType()) {
                case CHILD_ADDED:
                    System.out.println("CHILD_ADDED path: " + event.getData().getPath() + " data: " + new String(event.getData().getData()));
                    break;
                case CHILD_UPDATED:
                    System.out.println("CHILD_UPDATED path: " + event.getData().getPath() + " data: " + new String(event.getData().getData()));
                    break;
                case CHILD_REMOVED:
                    System.out.println("CHILD_REMOVED path: " + event.getData().getPath() + " data: " + new String(event.getData().getData()));
                    break;
                default:
                    break;
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }
}

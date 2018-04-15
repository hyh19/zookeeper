package chapter07.$7_1_5;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;

/**
 * 7.1.5 清单7-4 对 password 进行编码
 */
public class DigestAuthenticationProviderUsage {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(DigestAuthenticationProvider.generateDigest("tom:123456")); // tom:3YvKnq60bERLJOlabQFeB1f+/n0=
    }
}

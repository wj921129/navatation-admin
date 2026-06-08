package com.navatation.business;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 密钥对生成与管理辅助测试类
 * 
 * - 用于为前后端安全通信提供 RSA 公私钥的生成、覆盖与备份功能。
 * - 默认输出路径为 "./rsa"（与 application.yml 中的默认路径 app.rsa.key-path 保持一致）。
 * - 运行此测试用例将自动备份旧密钥为 `.pem.bak`，并生成新的公私钥，实现密钥的快速切换。
 */
public class RsaKeyGenTest {

    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String TARGET_DIR = "./rsa";

    private static final String PRIVATE_KEY_FILE = "rsa_private_key.pem";
    private static final String PUBLIC_KEY_FILE = "rsa_public_key.pem";

    private static final String PEM_PRIVATE_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_PRIVATE_FOOTER = "-----END PRIVATE KEY-----";
    private static final String PEM_PUBLIC_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PEM_PUBLIC_FOOTER = "-----END PUBLIC KEY-----";

    @Test
    public void generateKeyPair() {
        System.out.println("==================================================");
        System.out.println("开始生成并切换 RSA 密钥对...");
        System.out.println("目标输出目录: " + new File(TARGET_DIR).getAbsolutePath());
        System.out.println("==================================================");

        try {
            // 1. 创建密钥保存目录
            File keyDir = new File(TARGET_DIR);
            if (!keyDir.exists() && !keyDir.mkdirs()) {
                throw new IOException("无法创建目标密钥目录: " + TARGET_DIR);
            }

            // 2. 备份旧的密钥文件（若存在）
            backupOldFile(new File(keyDir, PRIVATE_KEY_FILE));
            backupOldFile(new File(keyDir, PUBLIC_KEY_FILE));

            // 3. 生成 RSA 2048 密钥对
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            generator.initialize(KEY_SIZE);
            KeyPair keyPair = generator.generateKeyPair();

            // 4. PEM 格式化并保存私钥
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
            String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKeySpec.getEncoded());
            File privateKeyFile = new File(keyDir, PRIVATE_KEY_FILE);
            writePemFile(privateKeyFile, PEM_PRIVATE_HEADER, PEM_PRIVATE_FOOTER, privateKeyBase64);
            System.out.println("✅ 新私钥已成功保存至: " + privateKeyFile.getCanonicalPath());

            // 5. PEM 格式化并保存公钥
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeySpec.getEncoded());
            File publicKeyFile = new File(keyDir, PUBLIC_KEY_FILE);
            writePemFile(publicKeyFile, PEM_PUBLIC_HEADER, PEM_PUBLIC_FOOTER, publicKeyBase64);
            System.out.println("✅ 新公钥已成功保存至: " + publicKeyFile.getCanonicalPath());

            System.out.println("==================================================");
            System.out.println("🎉 RSA 密钥对切换完成！系统将在下次重启时自动加载新密钥。");
            System.out.println("==================================================");

        } catch (NoSuchAlgorithmException e) {
            System.err.println("❌ 算法错误，不支持的算法: RSA");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ 文件读写错误，生成密钥失败");
            e.printStackTrace();
        }
    }

    /**
     * 备份已存在的密钥文件
     */
    private void backupOldFile(File file) {
        if (file.exists()) {
            File backupFile = new File(file.getAbsolutePath() + ".bak");
            try {
                Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("📦 检测到已有文件，备份为: " + backupFile.getName());
            } catch (IOException e) {
                System.err.println("⚠️ 备份旧密钥文件失败: " + file.getName());
            }
        }
    }

    /**
     * 写入标准 PEM 格式文件（每 64 字符换行）
     */
    private void writePemFile(File file, String header, String footer, String base64Content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(header);
            writer.write(System.lineSeparator());
            
            int index = 0;
            while (index < base64Content.length()) {
                int endIndex = Math.min(index + 64, base64Content.length());
                writer.write(base64Content.substring(index, endIndex));
                writer.write(System.lineSeparator());
                index = endIndex;
            }
            
            writer.write(footer);
            writer.write(System.lineSeparator());
        }
    }
}

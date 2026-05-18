package com.navatation.framework.security;

import com.navatation.common.BizException;
import com.navatation.common.ResultCode;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 密钥服务，负责服务器 RSA 密钥对的生命周期管理
 * - 启动时检查密钥文件是否存在，存在则加载，不存在则生成
 * - 提供公钥 PEM 字符串用于前端加密
 * - 提供私钥解密功能
 */
@Component
public class RsaKeyService {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyService.class);

    private static final String PRIVATE_KEY_FILE = "rsa_private_key.pem";
    private static final String PUBLIC_KEY_FILE = "rsa_public_key.pem";
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private static final String PEM_PRIVATE_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_PRIVATE_FOOTER = "-----END PRIVATE KEY-----";
    private static final String PEM_PUBLIC_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PEM_PUBLIC_FOOTER = "-----END PUBLIC KEY-----";

    private final String keyPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RsaKeyService(@Value("${app.rsa.key-path:./rsa}") String keyPath) {
        this.keyPath = keyPath;
    }

    /**
     * 初始化：加载或生成 RSA 密钥对
     */
    @PostConstruct
    public void init() {
        File keyDir = new File(keyPath);
        File privateKeyFile = new File(keyDir, PRIVATE_KEY_FILE);
        File publicKeyFile = new File(keyDir, PUBLIC_KEY_FILE);

        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            log.info("检测到密钥文件，从 {} 加载 RSA 密钥对", keyPath);
            loadKeys(privateKeyFile, publicKeyFile);
        } else {
            log.info("未检测到密钥文件，在 {} 生成新的 RSA 密钥对", keyPath);
            generateAndSaveKeys(keyDir, privateKeyFile, publicKeyFile);
        }
    }

    /**
     * 从文件中加载 RSA 密钥对
     */
    private void loadKeys(File privateKeyFile, File publicKeyFile) {
        try {
            String privateKeyPem = readPemFile(privateKeyFile);
            String publicKeyPem = readPemFile(publicKeyFile);

            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = keyFactory.generatePrivate(privateKeySpec);

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPem);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = keyFactory.generatePublic(publicKeySpec);

            log.info("RSA 密钥对加载成功");
        } catch (Exception e) {
            log.error("加载 RSA 密钥对失败", e);
            throw new RuntimeException("加载 RSA 密钥对失败", e);
        }
    }

    /**
     * 生成新的 RSA 密钥对并持久化到文件
     */
    private void generateAndSaveKeys(File keyDir, File privateKeyFile, File publicKeyFile) {
        try {
            if (!keyDir.exists() && !keyDir.mkdirs()) {
                throw new IOException("无法创建密钥目录: " + keyPath);
            }

            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            generator.initialize(KEY_SIZE);
            KeyPair keyPair = generator.generateKeyPair();

            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

            // 私钥 PKCS#8 编码并写入 PEM 文件
            PKCS8EncodedKeySpec pkcs8Spec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            String privateKeyBase64 = Base64.getEncoder().encodeToString(pkcs8Spec.getEncoded());
            writePemFile(privateKeyFile, PEM_PRIVATE_HEADER, PEM_PRIVATE_FOOTER, privateKeyBase64);
            log.info("私钥已写入: {}", privateKeyFile.getAbsolutePath());

            // 公钥 X.509 编码并写入 PEM 文件
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(publicKey.getEncoded());
            String publicKeyBase64 = Base64.getEncoder().encodeToString(x509Spec.getEncoded());
            writePemFile(publicKeyFile, PEM_PUBLIC_HEADER, PEM_PUBLIC_FOOTER, publicKeyBase64);
            log.info("公钥已写入: {}", publicKeyFile.getAbsolutePath());

            // 设置私钥文件权限（类 Unix 系统）
            setFilePermissions(privateKeyFile);

            log.info("RSA 密钥对生成并持久化成功");
        } catch (NoSuchAlgorithmException e) {
            log.error("不支持的算法: RSA", e);
            throw new RuntimeException("RSA 算法不可用", e);
        } catch (IOException e) {
            log.error("写入密钥文件失败", e);
            throw new RuntimeException("写入密钥文件失败", e);
        }
    }

    /**
     * 获取公钥 PEM 字符串
     * @return X.509 SubjectPublicKeyInfo 格式的 PEM 字符串
     */
    public String getPublicKeyPem() {
        try {
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(publicKey.getEncoded());
            String base64 = Base64.getEncoder().encodeToString(x509Spec.getEncoded());
            return formatPem(PEM_PUBLIC_HEADER, PEM_PUBLIC_FOOTER, base64);
        } catch (Exception e) {
            log.error("获取公钥失败", e);
            throw new BizException(ResultCode.INTERNAL_ERROR);
        }
    }

    /**
     * 使用私钥解密 Base64 编码的密文
     * @param base64EncryptedData Base64 编码的 RSA 加密数据
     * @return 解密后的明文字符串
     */
    public String decrypt(String base64EncryptedData) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(base64EncryptedData);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RSA 解密失败: {}", e.getMessage());
            throw new BizException(ResultCode.ENCRYPTION_ERROR);
        }
    }

    /**
     * 读取 PEM 文件，去除头尾标记和换行符
     */
    private String readPemFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过 PEM 头尾标记行
                if (line.startsWith("-----")) {
                    continue;
                }
                content.append(line);
            }
        }
        return content.toString();
    }

    /**
     * 将 Base64 编码的密钥数据写入 PEM 文件
     */
    private void writePemFile(File file, String header, String footer, String base64Content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(header);
            writer.write(System.lineSeparator());
            // 每 64 个字符换行（标准 PEM 格式）
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

    /**
     * 格式化 PEM 字符串（用于内存中返回）
     */
    private String formatPem(String header, String footer, String base64Content) {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append(System.lineSeparator());
        int index = 0;
        while (index < base64Content.length()) {
            int endIndex = Math.min(index + 64, base64Content.length());
            sb.append(base64Content, index, endIndex).append(System.lineSeparator());
            index = endIndex;
        }
        sb.append(footer).append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * 设置私钥文件权限，仅属主可读写
     */
    private void setFilePermissions(File file) {
        try {
            if (file.setReadable(true, true) && file.setWritable(true, true) && file.setExecutable(false, true)) {
                log.debug("私钥文件权限已设置为 600");
            }
        } catch (Exception e) {
            log.warn("设置私钥文件权限失败（非关键，仅类 Unix 系统支持）: {}", e.getMessage());
        }
    }
}

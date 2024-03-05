package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 流程服务应用程序
 *
 * @author wangsen_a
 * @date 2024/02/22
 */
@SpringBootApplication
@Slf4j
public class ProcessServiceApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(ProcessServiceApplication.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String port = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        // 控制台打印ip端口
        String localPath = "http://localhost:" + port + contextPath;
        String networkPath = "http://" + hostAddress + ":" + port + contextPath;
        log.info("-local:{}", localPath);
        log.info("-Network:{}", networkPath);
    }
}

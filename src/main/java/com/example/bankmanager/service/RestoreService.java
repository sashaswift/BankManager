package com.example.bankmanager.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class RestoreService {
    @Value("${app.backup.db}")
    private String db;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;


    @Value("${app.backup.dir}")
    private String backupFilePath;

    public void restoreDatabase() {

        String sqlFile=backupFilePath.concat("/DBBS.sql");
        // 构建mysql命令行命令
        List<String> mysqlCommand = Arrays.asList(
                "mysql", "-hlocalhost", "-u" + databaseUsername, "-p" + databasePassword, db
        );

        ProcessBuilder processBuilder = new ProcessBuilder(mysqlCommand);

        try {
            // 执行命令
            Process process = processBuilder.start();

            try (BufferedReader sqlReader = new BufferedReader(new FileReader(sqlFile));
                 BufferedWriter mysqlInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {

                String line;
                while ((line = sqlReader.readLine()) != null) {
                    mysqlInput.write(line);
                    mysqlInput.newLine();
                }
                mysqlInput.flush(); // 确保所有数据都被写入
            }

            // 读取命令输出（标准输出和错误输出，如果需要的话）
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                String outputLine, errorLine;
                while ((outputLine = reader.readLine()) != null) {
                    System.out.println(outputLine);
                }
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println(errorLine);
                }
            }

            // 等待命令执行完成并检查退出状态
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Database restore failed with exit code: " + exitCode);
            }

            System.out.println("Database restore completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error restoring database", e);
        }
    }


}

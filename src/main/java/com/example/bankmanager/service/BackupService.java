package com.example.bankmanager.service;
import org.springframework.stereotype.Component;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class BackupService {
    static final Logger log = LoggerFactory.getLogger(BackupService.class);

    // 用户名
    @Value("${spring.datasource.username}")
    private String username;

    // 密码
    @Value("${spring.datasource.password}")
    private String password;

    // 备份数据库
    @Value("${app.backup.db}")
    private String db;

    // 备份目录
    @Value("${app.backup.dir}")
    private String dir;

    // 最大备份文件数量
    @Value("${app.backup.max-age}")
    private Duration maxAge;

    // 锁，防止并发备份
    private Lock lock = new ReentrantLock();

    // 日期格式化
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss.SSS");

    /**
     * 备份文件
     * @return
     * @throws Exception
     */
    public Path backup() throws Exception {

        if (!this.lock.tryLock()) {
            throw new Exception("备份任务进行中！");
        }

        try {

            LocalDateTime now = LocalDateTime.now();

            Path dir = Paths.get(this.dir);

            // 备份的SQL文件
            Path sqlFile = dir.resolve(Path.of("DBBS" + ".sql"));

            /*if (Files.exists(sqlFile)) {
                // 文件已经存在，则添加后缀
                for (int i = 1; i >= 1; i ++) {
                    sqlFile = dir.resolve(Path.of(now.format(formatter) + "-" + i + ".sql"));
                    if (!Files.exists(sqlFile)) {
                        break;
                    }
                }
            }*/

            // 初始化目录
            if (!Files.isDirectory(sqlFile.getParent())) {
                Files.createDirectories(sqlFile.getParent());
            }

            // 创建备份文件文件
            //Files.createFile(sqlFile);

            // 标准流输出的内容就是 SQL 的备份内容
            try (OutputStream stdOut = new BufferedOutputStream(
                    Files.newOutputStream(sqlFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
                log.info("questtion1");
                // 监视狗。执行超时时间，1小时
                ExecuteWatchdog watchdog = new ExecuteWatchdog(TimeUnit.HOURS.toMillis(1));

                // 子进程执行器
                DefaultExecutor defaultExecutor = new DefaultExecutor();
                // defaultExecutor.setWorkingDirectory(null); // 工作目录
                defaultExecutor.setWatchdog(watchdog);
                defaultExecutor.setStreamHandler(new PumpStreamHandler(stdOut, System.err));
                 log.info("question2");
                // 进程执行命令
                CommandLine commandLine = new CommandLine("mysqldump");
                commandLine.addArgument("-u" + this.username); 	// 用户名
                commandLine.addArgument("-p" + this.password);// 密码
                commandLine.addArgument(this.db); 				// 数据库

                log.info("备份 SQL 数据");

                // 同步执行，阻塞直到子进程执行完毕。
                int exitCode = defaultExecutor.execute(commandLine);

                if (defaultExecutor.isFailure(exitCode)) {
                    throw new Exception("备份任务执行异常：exitCode=" + exitCode);
                }
            }


            /*if (this.maxAge.compareTo(Duration.ZERO)>0 && !this.maxAge.isZero()) {


                List<Path> filesList = Files.list(dir).collect(Collectors.toList());


                for (Path file : filesList) {
                    // 获取文件的创建时间
                    LocalDateTime createTime = LocalDateTime.ofInstant(Files.readAttributes(file, BasicFileAttributes.class).creationTime().toInstant(), ZoneId.systemDefault());

                    if (createTime.plus(this.maxAge).isBefore(now)) {

                        log.info("删除过期文件：{}", file.toAbsolutePath().toString());

                        // 删除文件
                        Files.delete(file);
                    }
                }
            }*/

            return sqlFile;
        } finally {
            this.lock.unlock();
        }
    }
}
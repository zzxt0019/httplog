package com.github.zzxt0019.httplog.example.exporter;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.github.zzxt0019.httplog.example.common.mapper.HttpLogMapper;
import com.github.zzxt0019.httplog.example.common.mapper.LogTargetMapper;
import com.github.zzxt0019.httplog.example.exporter.service.LogService;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

public class Main {
    private static String host = "127.0.0.1";
    private static String mysqlHost = "127.0.0.1";
    private static String mysqlPort = "3306";
    private static String mysqlDatabase = "log2";
    private static String mysqlUsername = "root";
    private static String mysqlPassword = "root";

    public static void main(String[] args) {
        initParams(args);
        LogService logService = new LogService(host, sqlSessionFactory);
        logService.start();
    }

    private static void initParams(String[] args) {
        for (String str : args) {
            if (str.startsWith("--") && str.contains("=")) {
                String[] split = str.split("=");
                String key = split[0].substring(2);
                String value = split[1];
                if (key.equalsIgnoreCase("mysqlHost")) {
                    mysqlHost = value;
                } else if (key.equalsIgnoreCase("mysqlPort") || key.equalsIgnoreCase("port")) {
                    mysqlPort = value;
                } else if (key.equalsIgnoreCase("mysqlDatabase") || key.equalsIgnoreCase("database")) {
                    mysqlDatabase = value;
                } else if (key.equalsIgnoreCase("mysqlUsername") || key.equalsIgnoreCase("username")) {
                    mysqlUsername = value;
                } else if (key.equalsIgnoreCase("mysqlPassword") || key.equalsIgnoreCase("password")) {
                    mysqlPassword = value;
                } else if (key.equalsIgnoreCase("host")) {
                    host = value;
                }
            }
        }
    }

    private static SqlSessionFactory sqlSessionFactory = initSqlSessionFactory();


    public static SqlSessionFactory initSqlSessionFactory() {
        DataSource dataSource = dataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("Production", transactionFactory, dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        configuration.addMapper(LogTargetMapper.class);
        configuration.addMapper(HttpLogMapper.class);
        configuration.setLogImpl(StdOutImpl.class);
        return new MybatisSqlSessionFactoryBuilder().build(configuration);
    }

    public static DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDatabase + "?characterEncoding=UTF-8&useSSL=false");
        dataSource.setUsername(mysqlUsername);
        dataSource.setPassword(mysqlPassword);
        return dataSource;
    }
}

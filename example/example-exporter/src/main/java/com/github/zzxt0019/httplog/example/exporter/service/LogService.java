package com.github.zzxt0019.httplog.example.exporter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.zzxt0019.httplog.example.common.bean.LogTarget;
import com.github.zzxt0019.httplog.example.common.mapper.HttpLogMapper;
import com.github.zzxt0019.httplog.example.common.mapper.LogTargetMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogService {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private PcapHandle pcapHandle;

    private final SqlSessionFactory sqlSessionFactory;
    private final SqlSession sqlSession;

    private String host;

    public LogService(String host, SqlSessionFactory sqlSessionFactory) {
        this.host = host;
        this.sqlSessionFactory = sqlSessionFactory;
        this.sqlSession = sqlSessionFactory.openSession(true);
    }

    public void start() {
        List<LogTarget> logTargetList = sqlSession.getMapper(LogTargetMapper.class).selectList(new QueryWrapper<LogTarget>().eq("host", host));
        for (LogTarget logTarget : logTargetList) {
            executorService.execute(() -> {
                try {
                    PcapNetworkInterface nif = Pcaps.getDevByName(logTarget.getNetworkInterface());
                    pcapHandle = nif.openLive(99999,
                            PcapNetworkInterface.PromiscuousMode.PROMISCUOUS,
                            10);
                    if (logTarget.getPort() != null) {
                        pcapHandle.setFilter(
                                "tcp port " + logTarget.getPort()
                                , BpfProgram.BpfCompileMode.OPTIMIZE);
                    }
                    try {
                        pcapHandle.loop(-1, new HttpListener(sqlSessionFactory)); // COUNT设置为抓包个数，当为-1时无限抓包
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pcapHandle.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void close() {
        try {
            pcapHandle.breakLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

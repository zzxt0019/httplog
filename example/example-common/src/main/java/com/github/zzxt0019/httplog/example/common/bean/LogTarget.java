package com.github.zzxt0019.httplog.example.common.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName
public class LogTarget {
    @TableId
    private Integer id;
    private String host;
    private Integer port;
    private String networkInterface;
}

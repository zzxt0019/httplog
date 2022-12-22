package com.github.zzxt0019.httplog.example.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 接口统一的返回格式
 *
 * @param <T>
 */
@Data
@Accessors(chain = true)
public class RetDTO<T> {
    private Integer code;
    private String message;
    private T data;

    @AllArgsConstructor
    public enum Code {
        success(0, "操作成功", "操作成功"),
        error(-1, "操作出错", "通常为后端程序报错"),
        unauthorized(-2, "权限", "权限相关报错, 如: 没有权限/认证失败"),
        no_update(1, "没有更新", "通常为修改操作后数据库修改条数为0"),
        duplicate(2, "重复", "重名报错, 如: 名称重复导致的添加失败"),
        used(3, "正在使用中", "修改的内容正在被其他使用, 如: 删除关联子项"),
        ;
        /**
         * 返回在code中
         */
        @Getter
        private final int code;
        /**
         * 返回在message中
         */
        @Getter
        private final String name;
        /**
         * 只做说明, 不返回
         */
        private final String describe;
    }

    public static <T> RetDTO<T> instance() {
        return new RetDTO<T>();
    }

    public static <T> RetDTO<T> instance(Code code) {
        return new RetDTO<T>().setCode(code.code).setMessage(code.name);
    }

    public static <T> RetDTO<T> data(T data) {
        return new RetDTO<T>().setCode(Code.success.code).setData(data);
    }

    public static <T> RetDTO<T> success() {
        return new RetDTO<T>().setCode(Code.success.code).setMessage(Code.success.name);
    }

    public static <T> RetDTO<T> error() {
        return new RetDTO<T>().setCode(Code.error.code).setMessage(Code.error.name);
    }
}

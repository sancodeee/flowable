package org.example.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.common.ResultCodeEnum;

/**
 * 自定义异常
 *
 * @author wangsen
 * @date 2024/02/22
 */
@Setter
@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    /**
     * 代码
     */
    private String code;
    /**
     * 消息
     */
    private String msg;

    public CustomException(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.code;
        this.msg = resultCodeEnum.msg;
    }

}

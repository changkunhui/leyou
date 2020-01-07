package com.leyou.common.exceptions;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;

/**
 * 自定义异常类
 * @author changkunhui
 * @date 2019/12/22 17:41
 */

@Getter
public class LyException extends RuntimeException {

    private Integer status;

    public LyException(Integer status,String message) {
        super(message);
        this.status = status;
    }

    public LyException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.status = exceptionEnum.getStatus();
    }
}

package com.leyou.common.advice;

import com.leyou.common.exceptions.ExceptionResult;
import com.leyou.common.exceptions.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常增强
 * @author changkunhui
 * @date 2019/12/22 17:46
 */
@Slf4j
@ControllerAdvice
public class BasicExceptionAdvice {

    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleException(LyException ex){
        return ResponseEntity.status(ex.getStatus()).body(new ExceptionResult(ex));
    }

}

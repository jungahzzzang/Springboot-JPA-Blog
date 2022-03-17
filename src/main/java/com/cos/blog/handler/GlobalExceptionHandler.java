package com.cos.blog.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.dto.ResponseDto;

@ControllerAdvice	//모든 exception 이곳으로 들어오게 하는 어노테이션
@RestController
public class GlobalExceptionHandler {

		@ExceptionHandler(value =Exception.class)
		public ResponseDto<String> handleArgumentException(Exception e) {
			return new ResponseDto<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		}
}
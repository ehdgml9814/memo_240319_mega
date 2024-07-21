package com.memo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.memo.common.FileManagerService;

@Configuration // 설정을 위한 Spring bean(매번 이미지 요청인지 확인해야 하기 때문)
public class WebMvcConfig implements WebMvcConfigurer {

	// 이미지 path(주소에 있음)와 서버에 업로드 된 실제 이미지와 매핑 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry
		.addResourceHandler("/images/**") // web path - http://localhost/images/aaaa_1721311714837/leaf-8867169_1280.jpg
		.addResourceLocations("file:///" + FileManagerService.FILE_UPLOAD_PATH); // 실제 이미지 파일 위치(슬래시 개수 주의!)
	}
}

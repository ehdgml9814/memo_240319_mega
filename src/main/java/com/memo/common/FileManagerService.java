package com.memo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // 스프링 빈 등록
public class FileManagerService {

	// 실제 업로드가 된 이미지가 저장될 서버의 경로 images뒤에 / 붙이기
	public static final String FILE_UPLOAD_PATH = "D:\\6_spring_project\\memo\\memo_workspace\\images/";
	// public static final String FILE_UPROAD_PATH = ""; 학원
	
	// input: MultipartFile(업로드할 파일), userLoginId
	// output: String(이미지 경로)
	public String uploadFile(MultipartFile file, String loginId) {
		// 폴더(디렉토리) 생성
		// 폴더명 예: aaaa_173409834/sun.png
		String directoryName = loginId + "_" + System.currentTimeMillis();
		// D:\\6_spring_project\\memo\\memo_workspace\\images/aaaa_173409834/sun.png/
		String filePath = FILE_UPLOAD_PATH + directoryName + "/";
		
		// 폴더 생성
		File directory = new File(filePath);
		if (directory.mkdir() == false) {
			// 폴더 생성 시 실패하면 경로를 null로 리턴
			return null;
		}
		
		// 파일 업로드
		try {
			byte[] bytes = file.getBytes();
			// ★★★★★★★ 한글명으로 된 이미지는 업로드 불가하므로 나중에 영문자로 바꿔야 함
			Path path = Paths.get(filePath + file.getOriginalFilename()); // filePath에 이미지 파일 이름 붙임
			Files.write(path, bytes); // 실제 업로드
		} catch (IOException e) {
			e.printStackTrace();
			return null; // 이미지 업로드 실패시 null 리턴
		}
		
		// 파일 업로드 성공시 이미지 url path를 리턴
		// 주소는 이렇게 될 것이다. 지금은 그냥 컴퓨터에 저장만 하고 끝
		// /images/aaaa_173409834/sun.png
		return "/images/" + directoryName + "/" + file.getOriginalFilename();
	}
	
	// 파일 삭제
	// input: 이미지 경로
	// output: X
	public void deleteFile(String imagePath) { // /images/aaaa_1721311714837/leaf-8867169_1280.jpg
		// D:\6_spring_project\memo\memo_workspace\images\aaaa_1721311714837/leaf-8867169_1280.jpg
		
		// FILE_UPLOAD_PATH = D:\\6_spring_project\\memo\\memo_workspace\\images/
		// 주소에 겹치는 /images/를 지워야 함
		Path path = Paths.get(FILE_UPLOAD_PATH + imagePath.replace("/images/", ""));
		
		// 삭제할 이미지 존재하는지 확인
		if (Files.exists(path)) {
			// 이미지 삭제
			try {
				Files.delete(path);
			} catch (IOException e) {
				log.info("[FileManagerService 파일 삭제] 삭제 실패. path:{}", path.toString());
				return;
			}
			
			// 폴더(디렉토리) 삭제
			path = path.getParent();
			if (Files.exists(path)) {
				try {
					Files.delete(path);
				} catch (IOException e) {
					log.info("[FileManagerService 디렉토리 삭제] 삭제 실패. path:{}", path.toString());
				}
			}
		}
	}
}

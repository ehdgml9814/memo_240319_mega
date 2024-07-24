package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostBO {
	
	// private Logger log = LoggerFactory.getLogger(PostBO.class);
	// private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private FileManagerService fileManagerService;
	
	// 글 조회
	// input: 로그인된 유저 id
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId) {
		return postMapper.selectPostListByUserId(userId);
	}
	
	// 글 수정 페이지 갈 때 글 불러오기
	// input: userId, postId
	// output: Post or null
	public Post getPostByUserIdPostId(int userId, int postId) {
		return postMapper.selectPostByUserIdPostId(userId, postId);
	}
	
	// 글 추가
	// input: 파라미터들
	// output: X
	public void addPost(int userId, String userLoginId, 
			String subject, String content, MultipartFile file) {
		
		String imagePath = null;
		if (file != null) {
			// 업로드 할 이미지가 있을 때
			imagePath = fileManagerService.uploadFile(file, userLoginId);
		}
		
		postMapper.insertPost(userId, subject, content, imagePath);
	}
	
	// input: 파라미터들
	// output: X
	public void updatePostByPostId(
			int userId, String loginId, 
			int postId, String subject, String content, 
			MultipartFile file) {
		
		// 업데이트 할 기존 글 가져오기(1. 이미지 교체 시 기존 이미지 삭제, 2. 업데이트 대상 확인)
		Post post = postMapper.selectPostByUserIdPostId(userId, postId);
		if (post == null) {
			log.warn("[글 수정] post is null. userId:{}, postId:{}", userId, postId);
			return;
		}
		
		// 파일이 있으면
		// 1) 새 이미지를 업로드
		// 2) 1번 단계가 성공하면 기존 이미지가 있으면 삭제
		String imagePath = null;
		if (file != null) {
			// 새 이미지 업로드
			imagePath = fileManagerService.uploadFile(file, loginId);
			
			// 업로드 성공 시(imagePath != null) 기존 이미지가 있으면 삭제
			if (imagePath != null && post.getImagePath() != null) {
				// 실제 폴더와 이미지 제거
				fileManagerService.deleteFile(post.getImagePath());
			}
		}
		
		// db 업데이트
		postMapper.updatePostByPostId(postId, subject, content, imagePath);
	}
}

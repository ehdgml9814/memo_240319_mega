package com.memo.post.bo;

import java.util.Collections;
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
	
	// 페이징 정보 필드(limit)
	private static final int POST_MAX_SIZE = 3;
	
	// 글 조회
	// input: 로그인된 유저 id
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId) {
		// 게시글 번호 10 9 8 | 7 6 5 | 4 3 2 | 1
		// 만약 432 페이지에 있을 때
		// 1) 다음: 2보다 작은 3개 DESC
		// 2) 이전: 4보다 큰 3개 ASC => 5 6 7 => BO에서 reverse해서 7 6 5
		// 3) 페이징X: 최신순 3개 DESC
		Integer standardId = null; // 기준 postId
		String direction = null; // 방향
		if (prevId != null) { // 2) 이전
			standardId = prevId;
			direction = "prev";
			
			List<Post> postList = postMapper.selectPostListByUserId(userId, standardId, direction, POST_MAX_SIZE);
			// 순서 뒤집기
			Collections.reverse(postList);
			
			return postList;
		} else if (nextId != null) { // 1) 다음
			standardId = nextId;
			direction = "next";
		} 
		
		// 3) 페이징X, 1) 다음에서 변수 세팅 후
		return postMapper.selectPostListByUserId(userId, standardId, direction, POST_MAX_SIZE);
	}
	
	// 이전 페이지의 마지막인지 체크
	public boolean isPrevLastPageByUserId(int userId, int prevId) {
		int maxPostId = postMapper.selectPostIdByUserIdAsSort(userId, "DESC");
		return maxPostId == prevId; // 같으면 마지막 글인 것
	}
	
	// 다음 페이지의 마지막인지 체크
	public boolean isNextLastPageByUserId(int userId, int nextId) {
		int minPostId = postMapper.selectPostIdByUserIdAsSort(userId, "ASC");
		return minPostId == nextId;
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
	
	// 글 수정
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
	
	// 글 삭제
	// input: postId, userId
	// output: X
	public void deletePostByPostIdUserId(int postId, int userId) {
		// 기존 글 가져오기(이미지 파일 존재 시 삭제해야 함)
		Post post = postMapper.selectPostByUserIdPostId(userId, postId);
		if (post == null) {
			log.info("[글 삭제] post is null postId:{} userId:{}", postId, userId);
			return;
		}
		
		// post 먼저 db에서 제거
		int rowCount = postMapper.deletePostByPostId(postId);
		
		// 이미지 존재 시 삭제 + 삭제된 행도 1개 일 때
		if (rowCount > 0 && post.getImagePath() != null) {
			fileManagerService.deleteFile(post.getImagePath());
		}
	}
}

package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/post")
@Controller
public class PostController {

	@Autowired
	private PostBO postBO;
	
	@GetMapping("/post-list-view")
	public String postListView(
			@RequestParam(value = "prevId", required = false) Integer prevIdParam, 
			@RequestParam(value = "nextId", required = false) Integer nextIdParam, 
			HttpSession session, Model model) {
		// 로그인 여부 확인
		Integer userId = (Integer)session.getAttribute("userId");
		if (userId == null) { // 비로그인 상태
			// 로그인 페이지로 이동시킴
			return "redirect:/user/sign-in-view";
		}
		
		// db 글목록 조회
		List<Post> postList = postBO.getPostListByUserId(userId, prevIdParam, nextIdParam);
		int prevId = 0;
		int nextId = 0;
		if (postList.isEmpty() == false) { // 글목록이 비어있지 않을 때 페이징 정보 세팅
			prevId = postList.get(0).getId();
			nextId = postList.get(postList.size() - 1).getId();
			
			// 이전 방향의 끝이면 prevId를 0으로 세팅
			// prevId와 테이블의 가장 큰 숫자와 같으면 끝 페이지인 것
			if (postBO.isPrevLastPageByUserId(userId, prevId)) {
				prevId = 0;
			}
			
			// 다음 방향의 끝이면 nextId를 0으로 세팅
			// nextId와 테이블의 가장 작은 숫자와 같으면 끝 페이지인 것
			if (postBO.isNextLastPageByUserId(userId, nextId)) {
				nextId = 0;
			}
		}
		
		// model에 담기
		model.addAttribute("prevId", prevId);	
		model.addAttribute("nextId", nextId);	
		model.addAttribute("postList", postList);
		
		return "post/postList";
	}
	
	@GetMapping("/post-create-view")
	public String postCreateView() {
		return "post/postCreate";
	}
	
//	@GetMapping("/post-detail-view")
//	public String postDetailView(
//			@RequestParam("postId") int postId,
//			Model model, HttpSession session) {
//		
//		// db 조회 - userId, postId
//		int userId = (int)session.getAttribute("userId");
//		Post post = postBO.getPostByPostIdUserId(userId, postId);
//		
//		// model에 담기
//		model.addAttribute("post", post);
//		
//		// 화면 이동
//		return "post/postDetail";
//	}
	
	@GetMapping("/post-detail-view")
	public String postDetailView(
			@RequestParam("postId") int postId, 
			Model model, 
			HttpSession session) {
		
		// db 조회(userId, postId 조회)
		int userId = (int)session.getAttribute("userId");
		Post post = postBO.getPostByUserIdPostId(userId, postId);
		
		// model에 담기
		model.addAttribute("post", post);
		
		// 화면 이동
		return "post/postDetail";
	}
}

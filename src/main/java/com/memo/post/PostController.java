package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/post")
@Controller
public class PostController {

	@Autowired
	private PostBO postBO;
	
	@GetMapping("/post-list-view")
	public String postListView(HttpSession session, Model model) {
		// 로그인 여부 확인
		Integer userId = (Integer)session.getAttribute("userId");
		if (userId == null) { // 비로그인 상태
			// 로그인 페이지로 이동시킴
			return "redirect:/user/sign-in-view";
		}
		
		// db 글목록 조회
		List<Post> postList = postBO.getPostListByUserId(userId);
		
		// model에 담기
		model.addAttribute("postList", postList);
		
		return "post/postList";
	}
}
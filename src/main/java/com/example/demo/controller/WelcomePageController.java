package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Users;
import com.example.demo.model.Account;
import com.example.demo.repository.UsersRepository;

/**
 * アプリケーションのルートURL ("/") へのアクセスを処理するコントローラー。
 * ユーザーのログイン状態に応じて、タスクページまたはログインページにリダイレクトします。
 */
@Controller
public class WelcomePageController {
	// セッションスコープで管理されるAccountオブジェクトを自動的に注入
	@Autowired
	Account account;
	
	// UsersRepositoryを自動的に注入して、データベースのUsersテーブルと対話
	@Autowired
	UsersRepository usersRepository;
	
	/**
	 * GETリクエストを処理し、ユーザーのログイン状態を確認してリダイレクトする。
	 * @return リダイレクト先のビュー名（ログインページまたはタスクページ）
	 */
	@GetMapping("/")
	public String index() {
		// セッションにアカウントIDまたは名前が存在するか確認
		if(account.getId() != null || account.getName() != null) {
			// IDを使ってデータベースからユーザー情報を取得
			Optional<Users> user = usersRepository.findById(account.getId());
			// ユーザーが存在する場合、タスクページにリダイレクト
			if(user.isPresent()) {
				return "redirect:/tasks";
			}
		}
		
		// ログイン情報が無効な場合、または存在しない場合、ログインページにリダイレクト
		return "redirect:/login";
	};
	
	/**
	 * ルートURLへのPOSTリクエストを処理する。
	 * このメソッドは、POSTリクエストをGETリクエストに転送するために使用されます。
	 * @return ルートURLへのリダイレクト
	 */
	@PostMapping("/")
	public String doPost(){
		// POSTメソッドをGETメソッドに転送し、/ へリダイレクトする
		return "redirect:/";
	}
}

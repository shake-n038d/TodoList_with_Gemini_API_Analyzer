package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Users;
import com.example.demo.model.Account;
import com.example.demo.repository.UsersRepository;

/**
 * ログイン、アカウント作成、ユーザー設定の更新を処理するコントローラー。
 * このクラスは、ユーザー認証と情報管理に関連するエンドポイントを定義します。
 */
@Controller
public class UsersController {
    // HttpSessionを自動的に注入して、セッションを管理
    @Autowired
    HttpSession session;

    // セッションスコープで管理されるAccountオブジェクトを自動的に注入
    @Autowired
    Account account;

    // UsersRepositoryを自動的に注入して、データベースのUsersテーブルと対話
    @Autowired
    UsersRepository usersRepository;
    
    /**
     * ログインまたはログアウトページを表示する。
     * ログインフォームへのアクセスと、ログアウト時のセッション無効化を処理します。
     * @param error URLパラメータの"error"
     * @param model ビューにデータを渡すためのModel
     * @return ログインページのビュー名
     */
    @GetMapping({"/login", "/logout" })
    public String index(@RequestParam(name = "error", defaultValue = "") String error,
            Model model) {
        // ログアウト時にセッションを無効化
        session.invalidate();

        if (error.equals("notLoggedIn")) {
            // ログインが必要な場合にエラーメッセージをモデルに追加
            model.addAttribute("message", "ログインしてください");
        }

        return "login";
    };

    /**
     * ログインフォームからのPOSTリクエストを処理する。
     * 入力されたメールアドレスとパスワードを検証し、ログインを試みます。
     * @param email ユーザーが入力したメールアドレス
     * @param password ユーザーが入力したパスワード
     * @param ra リダイレクト時に属性を渡すためのRedirectAttributes
     * @return リダイレクト先のビュー名
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes ra) {
        List<String> errList = new ArrayList<>();
        Users user = null;
        
        // メールアドレスでユーザーを検索
        Optional<Users> userOp = usersRepository.findByEmail(email);
        if(userOp.isPresent()) {
            user = userOp.get();
        }else {
            errList.add("ユーザーが登録されていません");    
        }
                
        // メールアドレスが空の場合にエラーメッセージを追加
        if (email == null || email.length() == 0) {
            errList.add("メールアドレスを入力してください");
        }
        
        // パスワードが空の場合にエラーメッセージを追加
        if (password == null || password.length() == 0) {
            errList.add("パスワードを入力してください");
        }
        
        // エラーがあればリダイレクトしてエラーメッセージを表示
        if(errList.size() > 0) {
            ra.addFlashAttribute("errList", errList);
            return "redirect:/login";
        }
        // メールアドレスまたはパスワードが一致しない場合にエラーメッセージを追加
        else if(user != null && (!email.equals(user.getEmail()) || !password.equals(user.getPassword()))) {
            errList.add("メールアドレスかパスワードが一致していません"); 
            ra.addFlashAttribute("errList", errList);
            return "redirect:/login";
        }
        // ログイン成功
        else {
             // セッション管理されたアカウント情報にユーザー情報をセット
            account.setName(user.getName());
            account.setId(user.getId());
            account.setEmail(user.getEmail());
            account.setNeedGraph(user.isNeedGraph());

            return "redirect:/tasks";   
        }
    }
    
    /**
     * アカウント作成フォームを表示する。
     * @return アカウント作成ページのビュー名
     */
    @GetMapping("/account")
    public String create() {
        return "accountForm";
    }
    
    /**
     * アカウント作成フォームからのPOSTリクエストを処理する。
     * 入力された情報に基づいて新しいユーザーを作成し、データベースに保存します。
     * @param name ユーザー名
     * @param email メールアドレス
     * @param password パスワード
     * @param ra リダイレクト時に属性を渡すためのRedirectAttributes
     * @return リダイレクト先のビュー名
     */
    @PostMapping("/account")
    public String store(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes ra) {

        List<String> errList = new ArrayList<>(); 
        
        // 名前のバリデーション
        if (name == null || name.length() == 0) {
            errList.add("名前を入力してください");
        }else if(name.length() > 20){
            errList.add("名前は20文字以内で設定してください");            
        }
        
        // メールアドレスのバリデーション
        if (email == null || email.length() == 0) {
            errList.add("Emailを入力してください");
        }else if(usersRepository.findByEmail(email).isPresent()) {
            errList.add("入力されたEmailは既に利用されています");            
        }
        
        // パスワードのバリデーション
        if (password == null || password.length() == 0) {
            errList.add("パスワードを入力してください");
        }
        
        if(errList.size() > 0) {
            ra.addFlashAttribute("errList", errList);
            return "redirect:/account";
        }
        
        // 新しいユーザーをデータベースに保存
        usersRepository.save(new Users(email, name, password));
        
        return "redirect:/login";
    }
    
    /**
     * ユーザー設定を更新するAPIエンドポイント。
     * @ResponseBodyアノテーションにより、JSONレスポンスを返します。
     * @param id パス変数として渡されるユーザーID
     * @param username 更新後のユーザー名
     * @param email 更新後のメールアドレス
     * @param isNeedGraph グラフ表示の必要性を示すフラグ
     * @param ra リダイレクト時に属性を渡すためのRedirectAttributes（このメソッドでは使用されない）
     * @return JSONレスポンスを含むResponseEntity
     */
    @ResponseBody
    @PostMapping("/users/settings/{id}")
    public ResponseEntity<Map<String, Object>> settings(
            @PathVariable(name="id") Integer id,
            @RequestParam(name="username", defaultValue="") String username,
            @RequestParam(name="email", defaultValue="") String email,
            @RequestParam(name="needGraph", defaultValue="false") boolean isNeedGraph,
            RedirectAttributes ra) {
        List<String> errList = new ArrayList<>();
        Users user = null;
        
        // JSONレスポンスを構築するためのマップ
        Map<String, Object> response = new HashMap<>();
        
        // セッションが利用可能か確認
        if(session == null) {
            response.put("success", false);
            response.put("errList", List.of("変更が許可されていません"));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        // データベースから現在のユーザー情報を取得
        Optional<Users> userOp = usersRepository.findById(account.getId());
        if(userOp.isPresent()) {
            user = userOp.get();
        }else {
            response.put("success", false);
            response.put("errList", List.of("現在、ユーザー変更機能をご利用できません"));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            
        }
        
        // 名前のバリデーション
        if (username == null || username.length() == 0) {
            errList.add("名前を入力してください");
        }else if(username.length() > 20){
            errList.add("名前は20文字以内で設定してください");            
        }
        
        // メールアドレスのバリデーション（既に利用されているか確認）
        if (email == null || email.length() == 0) {
            errList.add("Emailを入力してください");
        }else if(usersRepository.findByEmail(email).isPresent() && !account.getEmail().equals(email)) {
            errList.add("入力されたEmailは既に利用されています");            
        }
        
        if(errList.size() > 0) {
            response.put("success", false);
            response.put("errList", errList);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }else {
            // Usersテーブルの既存情報を更新
            user.setName(username);
            user.setEmail(email);
            user.setNeedGraph(isNeedGraph);
            
            // セッションスコープ下のAccountオブジェクトも更新
            account.setName(user.getName());
            account.setEmail(user.getEmail());
            account.setNeedGraph(user.isNeedGraph());
            
            usersRepository.save(user);

            // 成功レスポンスを返す
            response.put("success", true);
            response.put("message", "ユーザー情報が適切に変更されました");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}

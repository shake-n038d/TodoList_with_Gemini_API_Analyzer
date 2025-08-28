package com.example.demo.aop;

import jakarta.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.demo.model.Account;

/**
 * ログイン状態をチェックするためのAOPクラス。
 * TaskControllerのメソッドが実行される前に、ユーザーがログインしているか確認する。
 */
@Aspect // このクラスがアスペクト（横断的な関心を扱うクラス）であることを示す
@Component // Springのコンポーネントとして、DIコンテナに登録する
public class LoginCheckAspect {

	// ログイン情報を保持するセッションスコープのBeanを自動注入する
	@Autowired
	Account account;
	
    /**
     * @Aroundアノテーションは、指定されたメソッドの実行前後に処理を行うことを示す。
     * ここでは、`com.example.demo.controller.TaskController`クラス内の
     * 全てのメソッド(`*`)、引数(`(..)`)を対象とする。
     * これを「ポイントカット」と呼ぶ。
     */
    @Around("execution(* com.example.demo.controller.TaskController.*(..))")
    public Object checkLogin(ProceedingJoinPoint jp) throws Throwable {
        // 現在のリクエスト情報を取得する
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // レスポンスオブジェクトを取得する
        HttpServletResponse response = attributes.getResponse();

        // Accountオブジェクトのnameがnullまたは空の場合（ログインしていない状態）
        if (account.getName() == null || account.getName().length() <= 0) {
            // ログインページにリダイレクトする
            response.sendRedirect("/login?error=notLoggedIn");
            // 元の処理（TaskControllerのメソッド）は実行せずにnullを返す
            return null;
        }

        // ログインしている場合は、元の処理を続行する
        return jp.proceed();
    }
}
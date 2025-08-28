package com.example.demo.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

/**
 * ユーザーのセッション情報を保持するためのモデルクラス。
 * @Component によりSpringのコンポーネントとして管理されます。
 * @SessionScope により、このクラスのインスタンスはユーザーのセッションごとに1つ作成されます。
 */
@Data
@Component
@SessionScope
public class Account {
    // ユーザーIDを格納するフィールド。
    private Integer id;
    // ユーザー名を格納するフィールド。
    private String name;
    // ユーザーのメールアドレスを格納するフィールド。
    private String email;
    // グラフ表示設定を保持するフラグ。
    private boolean needGraph;
}

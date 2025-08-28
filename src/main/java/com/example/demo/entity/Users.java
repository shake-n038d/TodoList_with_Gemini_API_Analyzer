package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * データベースの"users"テーブルに対応するエンティティクラス。
 * ユーザーの認証情報や設定情報を表現します。
 */
@Data
@Entity
@Table(name = "users")
public class Users {
    // 主キー（Primary Key）として、idフィールドを定義
    @Id
    // データベースがIDを自動生成するように設定（AUTO_INCREMENTに相当）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // ユーザーのメールアドレスを格納するフィールド。ログインIDとして使用されます。
    private String email;
    // ユーザー名を格納するフィールド。
    private String name;
    // パスワードを格納するフィールド。
    private String password;
    
    // グラフ表示が必要かどうかを示すフラグ。`need_graph`カラムに対応します。
    @Column(name = "need_graph")
    private boolean needGraph;
    
    /**
     * JPAがエンティティをインスタンス化するために必要なデフォルトコンストラクタ。
     */
    public Users() {
        // JPA仕様によりデフォルトコンストラクタが必要です
    }
    
    /**
     * ユーザーを作成するためのコンストラクタ。
     * @param email ユーザーのメールアドレス
     * @param name ユーザー名
     * @param password ユーザーのパスワード
     */
    public Users(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        // 初期値として、グラフ表示は不要（false）に設定
        this.needGraph = false;    
    }
}

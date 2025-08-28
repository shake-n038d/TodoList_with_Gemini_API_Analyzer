package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * データベースの"tasks"テーブルに対応するエンティティクラス。
 * タスクの詳細情報（タイトル、期限、進捗など）を表現します。
 */
@Data
@Entity
@Table(name = "tasks")
public class Tasks {
    // 主キー（Primary Key）として、idフィールドを定義
    @Id
    // データベースがIDを自動生成するように設定（AUTO_INCREMENTに相当）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // カテゴリIDを格納するフィールド。`categories`テーブルの主キーと関連付けられます。
    @Column(name = "category_id")
    private Integer categoryId;
    
    // ユーザーIDを格納するフィールド。`users`テーブルの主キーと関連付けられます。
    @Column(name = "user_id")
    private Integer userId;
    
    // タスクのタイトルを格納するフィールド。
    private String title;
    
    // タスクの締め切り日を格納するフィールド。
    @Column(name = "closing_date")
    private LocalDate closingDate;
    
    // タスクの進捗状況を格納するフィールド。
    private Integer progress;
    // タスクに関するメモを格納するフィールド。
    private String memo;
    
    // タスクが有効であるか（削除されていないか）を示すフラグ。
    @Column(name = "is_active")
    private boolean isActive;

    /**
     * JPAがエンティティをインスタンス化するために必要なデフォルトコンストラクタ。
     */
    public Tasks() {
        super();
    }

    /**
     * タスクを作成するためのコンストラクタ。
     * @param categoryId カテゴリID
     * @param userId ユーザーID
     * @param title タスクのタイトル
     * @param memo タスクのメモ
     * @param closingDate タスクの締め切り日
     */
    public Tasks(Integer categoryId, Integer userId, String title, String memo, LocalDate closingDate) {
        super();
        this.categoryId = categoryId;
        this.userId = userId;
        this.closingDate = closingDate;
        this.title = title;
        
        // 初期値は未着手（0）
        this.progress = 0;
        this.memo = memo;
        // 初期値は有効（true）
        this.isActive = true;
    }
}

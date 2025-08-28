package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * データベースの"categories"テーブルに対応するエンティティクラス。
 * カテゴリ情報を表現するためのPOJO（Plain Old Java Object）です。
 */
@Data
@Entity
@Table(name = "categories")
public class Category {
    // 主キー（Primary Key）として、idフィールドを定義
    @Id
    // データベースがIDを自動生成するように設定（AUTO_INCREMENTに相当）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // カテゴリ名（例: "仕事", "プライベート"）を格納するフィールド
    private String name;
}

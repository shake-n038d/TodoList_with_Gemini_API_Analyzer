package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Category;

// DB内のcategriesテーブル操作を担うクラス
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}

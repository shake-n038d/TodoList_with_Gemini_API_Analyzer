package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Users;

//DB内のUsersテーブル操作を担うクラス
//独自のselect文を書きたい場合は以下のように記述することで実現できる
public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByName(String name);

    Optional<Users> findByEmail(String email);
}

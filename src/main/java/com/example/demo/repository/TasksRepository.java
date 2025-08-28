package com.example.demo.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Tasks;

// DB内のtasksテーブル操作を担うクラス
// 独自のselect文を書きたい場合は以下のように記述することで実現できる

public interface TasksRepository extends JpaRepository<Tasks, Integer>{
    @Query(value = "select * from tasks where category_id = :categoryId and user_id = :id order by id" , nativeQuery = true)
    List<Tasks> findByIdAndCategoryId(@Param("categoryId") Integer categoryId, @Param("id") Integer id);

    @Query(value = "select * from tasks where title like %:keyword% and user_id = :id order by id" , nativeQuery = true)
	List<Tasks> findByIdWithKeyword(@Param("keyword") String keyword, @Param("id") Integer id);

    @Query(value = "select * from tasks where user_id = :id order by id" , nativeQuery = true)
	List<Tasks> findAllById(@Param("id") Integer id);

    @Query(value = "select * from tasks where user_id = :id and is_active = 'f' order by id" , nativeQuery = true)
    List<Tasks> findAllDeleted(@Param("id")Integer id);

    @Query(value = "select * from tasks where user_id = :id order by closing_date" , nativeQuery = true)
    List<Tasks> findAllByIdAscClosingDate(@Param("id")Integer id);

    @Query(value = "select * from tasks where progress = :progress and user_id = :id" , nativeQuery = true)
    List<Tasks> findByProgress(@Param("progress") Integer progress, @Param("id") Integer id);

    
    // グラフ用メソッド群
    
    
    @Query(value = "select * from tasks where closing_date between :firstDate and :lastDate and user_id = :id and is_active = 't'" , nativeQuery = true)
    List<Tasks> findByDateAreaNoneProgress(
            @Param("firstDate")LocalDate firstDate,
            @Param("lastDate")LocalDate lastDate, 
            @Param("id")Integer id);
    
    @Query(value = "select * from tasks where closing_date between :firstDate and :lastDate and user_id = :id and progress = :progress and is_active = 't'" , nativeQuery = true)
    ArrayList<Tasks> findByDateArea(
            @Param("firstDate")LocalDate firstDate,
            @Param("lastDate")LocalDate lastDate, 
            @Param("id")Integer id,
            @Param("progress")Integer progress);
    
    @Query(value = "select count(*) from tasks where closing_date = :closingDate and user_id = :id and is_active = 't' group by closing_date" , nativeQuery = true)
    Integer countByClosingDateNoneProgress(
            @Param("closingDate")LocalDate closingDate, 
            @Param("id")Integer id);
    
    @Query(value = "select count(*) from tasks where closing_date = :closingDate and user_id = :id and progress = :progress and is_active = 't' group by closing_date" , nativeQuery = true)
    Integer countByClosingDate(
            @Param("closingDate")LocalDate closingDate, 
            @Param("id")Integer id, 
            @Param("progress")Integer progress);
    
    
    // categoryidあり
    
    @Query(value = "select * from tasks where closing_date between :firstDate and :lastDate and user_id = :id and category_id = :categoryId and is_active = 't'" , nativeQuery = true)
      List<Tasks> findByDateAreaNoneProgressAndCategoryId(
            @Param("firstDate")LocalDate firstDate,
            @Param("lastDate")LocalDate lastDate, 
            @Param("id")Integer id,
            @Param("categoryId")Integer categoryId);
    
    @Query(value = "select * from tasks where closing_date between :firstDate and :lastDate and user_id = :id and progress = :progress and category_id = :categoryId and is_active = 't'" , nativeQuery = true)
    ArrayList<Tasks> findByDateAreaAndCategoryId(
            @Param("firstDate")LocalDate firstDate,
            @Param("lastDate")LocalDate lastDate, 
            @Param("id")Integer id,
            @Param("progress")Integer progress,
            @Param("categoryId")Integer categoryId);
    
    @Query(value = "select count(*) from tasks where closing_date = :closingDate and user_id = :id and category_id = :categoryId and is_active = 't' group by closing_date" , nativeQuery = true)
    Integer countByClosingDateNoneProgressAndCategoryId(
            @Param("closingDate")LocalDate closingDate, 
            @Param("id")Integer id,
            @Param("categoryId")Integer categoryId);
    
    @Query(value = "select count(*) from tasks where closing_date = :closingDate and user_id = :id and progress = :progress and category_id = :categoryId and is_active = 't' group by closing_date" , nativeQuery = true)
    Integer countByClosingDateAndCategoryId(
            @Param("closingDate")LocalDate closingDate, 
            @Param("id")Integer id, 
            @Param("progress")Integer progress,
            @Param("categoryId")Integer categoryId);
    
    // ページング用
    @Query(value = "select * from tasks where user_id = :id order by id" , nativeQuery = true)
    Page<Tasks> findAllById(Integer id, Pageable pageable);
    
    @Query(value = "select * from tasks where title like %:keyword% and user_id = :id order by id" , nativeQuery = true)
    Page<Tasks> findByIdWithKeyword(@Param("keyword") String keyword, @Param("id") Integer id, Pageable pageable);

    @Query(value = "select * from tasks where category_id = :categoryId and user_id = :id order by id" , nativeQuery = true)
    Page<Tasks> findByIdAndCategoryId(@Param("categoryId") Integer categoryId, @Param("id") Integer id, Pageable pageable);

    @Query(value = "select * from tasks where user_id = :id and is_active = 'f' order by id" , nativeQuery = true)
    Page<Tasks> findAllDeleted(@Param("id")Integer id, Pageable pageable);

    @Query(value = "select * from tasks where user_id = :id order by closing_date" , nativeQuery = true)
    Page<Tasks> findAllByIdAscClosingDate(@Param("id")Integer id, Pageable pageable);

    @Query(value = "select * from tasks where progress = :progress and user_id = :id" , nativeQuery = true)
    Page<Tasks> findByProgress(@Param("progress") Integer progress, @Param("id") Integer id, Pageable pageable);

}

package com.example.demo.controller;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.service.GeminiService;

import lombok.RequiredArgsConstructor;

// Gemini用のメソッド
// ConfigureからGeminiに接続するためのクライアントを呼び出し、
// 以下のデータを渡す
// 1. 分析の指示するプロンプト
// 2. 分析対象のデータ
//      1つだけの場合 -> data
//      3つある場合   -> unstartedData, inProgressData, completedData
// 3. 分析対象のラベル
// 4. 進捗状況のラベル(progress) -> データが1, 3つの場合の判定にもなる

@Controller
@RequiredArgsConstructor
public class ChatController {
    
    @Autowired
    private final GeminiService geminiService;
    
    @PostMapping("/ai/chat")
    @ResponseBody
    public ResponseEntity<String> getAnalysisAndAdvice(
        @RequestParam(name = "progress", required = false) Integer progress,
        @RequestParam(name = "labels", required = false) List<String> labels,
        @RequestParam(name = "data", required = false) List<Integer> data,
        @RequestParam(name = "unstartedData", required = false) List<Integer> unstartedData,
        @RequestParam(name = "inProgressData", required = false) List<Integer> inProgressData,
        @RequestParam(name = "completedData", required = false) List<Integer> completedData
    ) {
        // サービスメソッドに渡すためのデータセットを定義
        List<Map.Entry<String, Map<LocalDate, Integer>>> geminiDatasets = new ArrayList<>();
        
        // progressの値でデータを分岐
        if (progress != null && data != null && labels != null && !data.isEmpty()) {
            // 単一のデータセットを構築
            Map<LocalDate, Integer> singleDataMap = IntStream.range(0, labels.size()).boxed()
                .collect(Collectors.toMap(
                    i -> LocalDate.parse(labels.get(i)),
                    data::get
                ));
            
            // progressの判定をするための三項演算子
            String title = (progress == 0) ? "未着手タスク" : (progress == 1) ? "進行中タスク" : "完了済みタスク";
            // progressとデータセットを渡す
            geminiDatasets.add(new AbstractMap.SimpleEntry<>(title, singleDataMap));
            
        } else if (unstartedData != null && inProgressData != null && completedData != null && labels != null) {
            // 3つのデータセットを構築
            // progressがnullであるため、直接progressに応じたtitleをMapに渡している
            
            // 未着手タスクのデータ
            Map<LocalDate, Integer> unstartedMap = IntStream.range(0, labels.size()).boxed()
                .collect(Collectors.toMap(
                    i -> LocalDate.parse(labels.get(i)),
                    unstartedData::get
                ));
            geminiDatasets.add(new AbstractMap.SimpleEntry<>("未着手タスク", unstartedMap));

            // 進行中タスクのデータ
            Map<LocalDate, Integer> inProgressMap = IntStream.range(0, labels.size()).boxed()
                .collect(Collectors.toMap(
                    i -> LocalDate.parse(labels.get(i)),
                    inProgressData::get
                ));
            geminiDatasets.add(new AbstractMap.SimpleEntry<>("進行中タスク", inProgressMap));

            // 完了済みタスクのデータ
            Map<LocalDate, Integer> completedMap = IntStream.range(0, labels.size()).boxed()
                .collect(Collectors.toMap(
                    i -> LocalDate.parse(labels.get(i)),
                    completedData::get
                ));
            geminiDatasets.add(new AbstractMap.SimpleEntry<>("完了済みタスク", completedMap));
        }

        String response = null;
        if (!geminiDatasets.isEmpty()) {
            // データの格納が正常に行われた場合は、サービスメソッドにデータを渡す
            response = geminiService.getAnalysisAndAdvice(geminiDatasets, progress);
        } else {
            // データセットが特にない場合は、データがない旨を知らせる
            response = "分析するデータがありません。";
        }
        
        return ResponseEntity.ok(response);
    }
}
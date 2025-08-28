package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import lombok.RequiredArgsConstructor;

/**
 * Google Gemini APIと連携して、タスクデータに基づいた分析やアドバイスを提供するサービス。
 */
@Service
@RequiredArgsConstructor
public class GeminiService {
    // Google Gemini APIのクライアントを注入
    private final Client client;
    
    /**
     * 1つまたは複数のデータセットと進捗状況を受け取り、Geminiに分析を依頼して回答を返す。
     * @param geminiDatasets 日付とタスク数のマップを含むデータセットのリスト
     * @param progress 現在表示されているタスクの進捗状況（0:未着手, 1:進行中, 2:完了）
     * @return Geminiから返された分析結果とアドバイス
     */
    public String getAnalysisAndAdvice(List<Entry<String, Map<LocalDate, Integer>>> geminiDatasets, Integer progress) {
        // 各データセットをプロンプト用に整形し、1つの文字列に結合
        String allData = geminiDatasets.stream()
            .map(entry -> {
                String title = entry.getKey();
                String data = entry.getValue().entrySet().stream()
                    .map(e -> e.getKey().toString() + ": " + e.getValue())
                    .collect(Collectors.joining("\n"));
                return String.format("### %s\n%s", title, data);
            })
            .collect(Collectors.joining("\n\n"));

        // progressの値からコンテキストに応じたメッセージを生成
        String progressContext = "";
        if (progress != null) {
            switch(progress) {
                case 0: progressContext = "未着手タスクのグラフが表示されています。"; break;
                case 1: progressContext = "進行中タスクのグラフが表示されています。"; break;
                case 2: progressContext = "完了済みタスクのグラフが表示されています。"; break;
            }
        }
        
        // Geminiに送るプロンプト（指示文）を構築
        String systemPrompt = """
            あなたは、タスク管理の専門家です。以下のタスクデータ（日付ごとのタスク数）を分析し、ユーザーに役立つアドバイスを簡潔に提示してください。
            提供されたデータ以外の情報は使用しないでください。

            ### コンテキスト
            %s

            ### データ
            %s

            ### 指示
            1. 全体的な傾向を分析する。
            2. 各データセットから得られる重要な洞察を特定する。
            3. 分析結果に基づいて、具体的で実行可能な改善策を提案する。
            4. 回答はユーザーフレンドリーで、簡潔にまとめる。
            5. 文章の改行にはHTMLの<br>タグを使って改行を適応してレスポンスを提供してください。
            6. セクションを【】としてセクションを分ける際には文章中に空行が一行見えるようにしてください
            7. 項目を並べる際は1. 2. 3. としてください
            8. セクションを【】としてセクションを分ける際には必ず行の最初に記述し、その前の行に<br>を入れてください
            """.formatted(progressContext, allData);
        
        // Geminiクライアントにリクエストを送信し、応答を取得
        GenerateContentResponse response = client.models.generateContent(
            "gemini-2.5-flash",
            systemPrompt,
            null);
        
        // 応答のテキスト部分を返す
        return response.text();
    }
}

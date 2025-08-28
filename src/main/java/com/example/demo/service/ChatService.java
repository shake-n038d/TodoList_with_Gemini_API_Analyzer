package com.example.demo.service;

//openai用のメソッド
//APIへのアクセスが有料のため断念
//
//@Service
public class ChatService {

//    private final ChatClient chatClient;
//
//    @Autowired
//    public ChatService(ChatClient chatClient) {
//        this.chatClient = chatClient;
//    }
//
//    public String getChatCptResponse(String prompt) {
//        Prompt userPrompt = new Prompt(prompt);
//        ChatResponse response = chatClient.call(userPrompt);
//
//        return response.getResult().getOutput().getContent();
//    }
//    
//    public String getAnalysisAndAdvice(List<Map.Entry<String, Map<LocalDate, Integer>>> datasets, Integer progress) {
//        String allData = datasets.stream()
//            .map(entry -> {
//                String title = entry.getKey();
//                String data = entry.getValue().entrySet().stream()
//                    .map(e -> e.getKey().toString() + ": " + e.getValue())
//                    .collect(Collectors.joining("\n"));
//                return String.format("### %s\n%s", title, data);
//            })
//            .collect(Collectors.joining("\n\n"));
//
//        String progressContext = "";
//        if (progress != null) {
//            switch(progress) {
//                case 0: progressContext = "未着手タスクのグラフが表示されています。"; break;
//                case 1: progressContext = "進行中タスクのグラフが表示されています。"; break;
//                case 2: progressContext = "完了済みタスクのグラフが表示されています。"; break;
//            }
//        }
//        
//        String systemPrompt = """
//            あなたは、タスク管理の専門家です。以下のタスクデータ（日付ごとのタスク数）を分析し、ユーザーに役立つアドバイスを簡潔に提示してください。
//            提供されたデータ以外の情報は使用しないでください。
//
//            ### コンテキスト
//            %s
//
//            ### データ
//            %s
//
//            ### 指示
//            1. 全体的な傾向を分析する。
//            2. 各データセットから得られる重要な洞察を特定する。
//            3. 分析結果に基づいて、具体的で実行可能な改善策を提案する。
//            4. 回答はユーザーフレンドリーで、簡潔にまとめる。
//            """.formatted(progressContext, allData);
//
//        Prompt prompt = new Prompt(List.of(
//            new SystemMessage(systemPrompt),
//            new UserMessage("上記のデータに基づいて、分析とアドバイスをお願いします。")
//        ));
//
//        return chatClient.call(prompt).getResult().getOutput().getContent();
//    }
}

package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.genai.Client;

/**
 * Gemini AIのクライアント設定を行うためのConfigurationクラス。
 * このクラスは、application.propertiesに設定されたAPIキーを使用して、
 * Geminiへの接続クライアント（ClientクラスのBean）を生成し、SpringのDIコンテナで管理する。
 */
@Configuration
public class GeminiConfig {

    /**
     * application.propertiesから'google.genai.api.key'の値を読み込み、
     * private変数apiKeyに自動でインジェクションする。
     * このAPIキーは、Gemini APIへの認証に必要となる。
     */
    @Value("${google.genai.api.key}")
    private String apiKey;
    
    /**
     * SpringのDIコンテナにClientクラスのインスタンスをBeanとして登録するメソッド。
     * メソッド名は任意だが、生成するBeanの役割を示す名前にすることが推奨される。
     * @Beanアノテーションを付与することで、Springはアプリケーション起動時にこのメソッドを実行し、
     * 戻り値をDIコンテナに登録する。
     * * @return Gemini APIとの通信に使用するClientオブジェクト
     */
    @Bean
    public Client geminiClient() {
        // Client.builder()を使ってClientオブジェクトを構築する
        // 読み込んだapiKeyをセットすることで、認証情報を設定する
        return Client.builder().apiKey(apiKey).build();
    }
}

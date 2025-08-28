【TodoList_with_Gemini_API_Analyzer】
　
Gemini API, Chart.js を組み合わせて作成したタスクの進捗度状況を可視化したTodoリストアプリケーションです。
動作環境はEclipseのみに限定していますが、応用次第では独自のTomCatサーバーにデプロイが可能です。
　

【EclipseでのMavenプロジェクトのインポートとPostgreSQLの設定
このドキュメントでは、Spring Bootアプリケーションのローカル開発環境をセットアップする手順を説明します。

1. MavenプロジェクトをEclipseに取り込む   

i. Eclipseを開き、メニューバーからFile -> Importを選択します。
ii. ダイアログで、Maven -> Existing Maven Projectsを選択し、Nextをクリックします。
iii. Browseをクリックして、プロジェクトのpom.xmlファイルが含まれているフォルダを選択します。
iv. プロジェクトがリストに表示されることを確認し、Finishをクリックします。
v. インポートしたプロジェクトを右クリックし、**Maven -> Update Project...**を選択します。
vi. ダイアログでプロジェクトが選択されていることを確認し、OKをクリックします。
　　これにより、依存関係が自動的にダウンロードされます。

2. PostgreSQLの設定
i. データベースへの接続 (PowerShellで入力)
psql -U postgres

ii. データベースの作成 (psqlターミナルで操作)
CREATE DATABASE todolist;

iii. PostgreSQLユーザーの作成
※ユーザー名・パスワードは任意で構いませんが、ここでは名前を「student」、パスワードを「himitu」とします。
CREATE USER student WITH PASSWORD 'himitu';

iv. データベース権限の付与
todolistデータベースに切り替えます。
\c todolist

todolistデータベースとpublicスキーマの全権限をstudentユーザーに付与します。

GRANT ALL PRIVILEGES ON DATABASE todolist TO student;
GRANT ALL ON SCHEMA public TO student;

3. Spring Bootアプリケーションの起動
i. TodolistApplication.javaの実行
　 Eclipseのパッケージエクスプローラーで、src/main/java/com/example/comフォルダを展開します。

ii. **TodolistApplication.java**ファイルを右クリックします。

iii. コンテキストメニューからRun As -> Java Applicationを選択します。
　　 コンソールにアプリケーションが起動したことを示すログが表示されれば、起動完了です。
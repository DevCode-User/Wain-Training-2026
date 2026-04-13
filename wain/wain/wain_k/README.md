# ec-login-register-https

Spring Boot 3 / Thymeleaf / H2 を使った、ECシステムの **HTTPS対応ログイン＋新規登録サンプル** です。

## できること
- システム入口画面
- 利用者ログイン
- 利用者新規登録
- 管理者ログイン
- 利用者ホーム表示
- 管理者ホーム表示
- HTTPS通信（自己署名証明書）

## 主な対応内容
- `customers` テーブルに合わせた利用者登録
- `admins` テーブルに合わせた管理者ログイン
- パスワードは BCrypt でハッシュ化
- H2 メモリDBを使用

## 初期データ
- 利用者ID: `user001`
- 利用者PW: `cust1234`
- 管理者ID: `admin001`
- 管理者PW: `admin1234`

## 実行
```bash
mvn clean package
mvn org.springframework.boot:spring-boot-maven-plugin:run
```

## アクセスURL
- 入口: `https://localhost:8443/`
- 利用者ログイン: `https://localhost:8443/login`
- 新規登録: `https://localhost:8443/register`
- 管理者ログイン: `https://localhost:8443/admin/login`

## 証明書について
自己署名証明書を使っているため、ブラウザによっては警告が表示されます。
ローカル確認用として詳細から続行してください。

## LANケーブルで2台接続する場合
- サーバーPCでアプリを起動
- 実行PCのブラウザから `https://サーバーPCのIP:8443/` にアクセス
- IPでアクセスする場合は、証明書警告が出ることがあります

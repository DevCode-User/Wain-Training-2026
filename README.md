# EC在庫管理システム（設計書対応版）

Excel の基本設計書に寄せて、既存プログラムを組み直した版です。  
主に次の内容を追加しています。

- 管理者ダッシュボード
- 商品管理（登録・編集・非表示）
- 在庫管理（一括更新・CSV取込）
- 注文管理（一覧・詳細・状態更新）
- categories / carts / cart_items テーブル対応
- JSON API
  - `POST /api/login`
  - `GET /api/products`
  - `POST /api/orders`

## 1. 前提

- Java 17 以上
- Maven
- MySQL 8 系

## 2. データベース作成

`sql/create_database.sql` を実行してください。

```sql
CREATE DATABASE IF NOT EXISTS ecdb
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

## 3. Docker で MySQL を起動する場合

```powershell
docker compose up -d
```

## 4. PowerShell で環境変数を設定

```powershell
$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_DATABASE="ecdb"
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="root"
```

## 5. 起動

```powershell
mvn spring-boot:run
```

## 6. 画面URL

- https://localhost:8444/
- https://localhost:8444/login
- https://localhost:8444/register
- https://localhost:8444/admin/login

## 7. 初期アカウント

### 利用者
- ID: `user001`
- PW: `cust1234`

### 管理者
- ID: `admin001`
- PW: `admin1234`

## 8. API 例

### ログインAPI

```powershell
curl -k -X POST https://localhost:8444/api/login `
  -H "Content-Type: application/json" `
  -d "{\"userId\":\"user001\",\"password\":\"cust1234\"}"
```

### 商品一覧API

```powershell
curl -k "https://localhost:8444/api/products?keyword=ゲーム&page=1&limit=20"
```

### 注文登録API

```powershell
curl -k -X POST https://localhost:8444/api/orders `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer {token}" `
  -d "{\"userId\":\"user001\",\"totalAmount\":7200,\"paymentMethod\":\"credit_card\",\"shippingAddress\":\"東京都千代田区1-1-1\",\"items\":[{\"productId\":3,\"quantity\":1,\"unitPrice\":7200}]}"
```

## 9. CSV一括更新フォーマット

`sample/stock_update_sample.csv` を参考にしてください。

## 10. 注意

この環境では Maven が入っていなかったため、**ソース一式としてZIP化**しています。  
コードは設計書に寄せて整理済みですが、手元で `mvn spring-boot:run` による最終起動確認をしてください。


## 2026-04-14 修正
- カート画面の「レジに進む」ボタンを /checkout へ遷移するよう修正
- カート画面に成功/エラーメッセージ表示を追加
- 管理者の商品登録/更新で予期しない例外メッセージも画面表示
- 初期カテゴリを不足分だけ補完するよう調整

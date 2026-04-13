# ec-login-sample (MySQL版)

## 起動方法

### 1. MySQL を用意
- Host: localhost
- Port: 3306
- Database: ecdb
- Username: root
- Password: root

必要なら先に以下を実行してください。

```sql
CREATE DATABASE IF NOT EXISTS ecdb
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

### 2. PowerShell で環境変数を設定

```powershell
$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_DATABASE="ecdb"
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="root"
```

### 3. アプリ起動

```powershell
mvn spring-boot:run
```

### 4. アクセスURL
- https://localhost:8444/
- https://localhost:8444/login
- https://localhost:8444/register
- https://localhost:8444/admin/login

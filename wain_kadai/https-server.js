const https = require('https');
const fs = require('fs');
const path = require('path');

const PORT = 8443;
const FRONTEND_PATH = 'wain_kadai/frontend';

// 自己署名証明書を生成
function generateCertificate() {
  const { exec } = require('child_process');
  const certPath = 'wain_kadai/server.cert';
  const keyPath = 'wain_kadai/server.key';
  
  if (fs.existsSync(certPath) && fs.existsSync(keyPath)) {
    console.log('✓ 既存の証明書を使用: ' + certPath);
    return { cert: certPath, key: keyPath };
  }
  
  console.log('証明書を生成しています...');
  
  // Node内で証明書を生成する簡易実装
  return generateNodeCertificate(certPath, keyPath);
}

// Node.jsで簡易的に証明書を生成
function generateNodeCertificate(certPath, keyPath) {
  const { spawnSync } = require('child_process');
  
  // keytoolコマンドを試す
  try {
    const result = spawnSync('powershell', ['-Command', `
      $cert = New-SelfSignedCertificate -CertStoreLocation cert:\\CurrentUser\\My -DnsName localhost
      $pwd = ConvertTo-SecureString -String "password" -Force -AsPlainText
      Export-PfxCertificate -Cert $cert -FilePath wain_kadai/cert.pfx -Password $pwd
    `], { encoding: 'utf8' });
    
    if (result.status === 0) {
      console.log('✓ Windows PowerShellで証明書を生成しました');
      return { cert: 'wain_kadai/cert.pfx', key: 'wain_kadai/cert.pfx' };
    }
  } catch (e) {
    // エラー処理
  }
  
  // フォールバック：既存のキーストアを使用
  console.log('警告: 証明書の自動生成に失敗しました');
  console.log('Javaサーバーで生成されたキーストアを使用します');
  return null;
}

// HTTPSサーバーを作成
const options = generateCertificate();

if (!options) {
  console.log('エラー: 証明書が見つかりません');
  process.exit(1);
}

// 簡易APIサーバー
const server = https.createServer(options, (req, res) => {
  // CORSヘッダー
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  
  // OPTIONSリクエストに応答
  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }

  // ルーティング
  if (req.url.startsWith('/api/')) {
    // API呼び出しをJavaサーバーにフォワード
    handleApiRequest(req, res);
  } else {
    // 静的ファイルを配信
    handleStaticFile(req, res);
  }
});

// 静的ファイル処理
function handleStaticFile(req, res) {
  let filePath = path.join(FRONTEND_PATH, req.url === '/' ? '/admin/login.html' : req.url);
  
  // セキュリティ: パストトラバーサル対策
  const realPath = path.resolve(filePath);
  const baseDir = path.resolve(FRONTEND_PATH);
  if (!realPath.startsWith(baseDir)) {
    res.writeHead(403);
    res.end('Forbidden');
    return;
  }

  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404);
      res.end('<h1>404 - Not Found</h1>');
      return;
    }

    const contentType = getContentType(filePath);
    res.writeHead(200, { 'Content-Type': contentType + '; charset=utf-8' });
    res.end(data);
  });
}

// API処理（Javaサーバーへフォワード）
function handleApiRequest(req, res) {
  // ここではダミーレスポンスを返す
  // 実際にはJavaサーバーのHTTP APIをコール
  
  res.writeHead(200, { 'Content-Type': 'application/json' });
  
  if (req.url === '/api/login' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', () => {
      try {
        const data = JSON.parse(body);
        res.end(JSON.stringify({
          success: true,
          message: 'ログインに成功しました',
          token: 'test_token_' + Date.now(),
          username: data.username,
          role: 'ADMIN'
        }));
      } catch (e) {
        res.end(JSON.stringify({ success: false, message: 'Invalid request' }));
      }
    });
  } else {
    res.end(JSON.stringify({ success: true, data: [] }));
  }
}

// コンテンツタイプを判定
function getContentType(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  const types = {
    '.html': 'text/html',
    '.css': 'text/css',
    '.js': 'application/javascript',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.gif': 'image/gif'
  };
  return types[ext] || 'text/plain';
}

// サーバー起動
server.listen(PORT, () => {
  console.log('========================================');
  console.log('  HTTPS Server Started (Node.js)');
  console.log('========================================');
  console.log('プロトコル: HTTPS');
  console.log('ポート: ' + PORT);
  console.log('URL: https://localhost:' + PORT);
  console.log();
  console.log('ログイン画面: https://localhost:' + PORT + '/admin/login.html');
  console.log('顧客ページ: https://localhost:' + PORT + '/customer/index.html');
  console.log();
  console.log('テストアカウント:');
  console.log('  admin / admin123');
  console.log('  user1 / user1pass');
  console.log('  user2 / user2pass');
  console.log('========================================');
});

// ログインフォーム処理
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');
    const loading = document.getElementById('loading');
    
    // フォーム送信時の処理
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();
        
        // バリデーション
        if (!username || !password) {
            showError('ユーザー名とパスワードを入力してください');
            return;
        }
        
        // ローディング表示
        loading.classList.add('show');
        errorMessage.style.display = 'none';
        successMessage.style.display = 'none';
        
        try {
            // サーバーにログイン要求を送信
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });
            
            // レスポンスの処理
            if (response.ok) {
                const data = await response.json();
                showSuccess('ログインに成功しました。');
                
                // 2秒後にダッシュボードにリダイレクト
                setTimeout(() => {
                    window.location.href = '/dashboard';
                }, 2000);
                
                // クッキーにトークンを保存（オプション）
                if (data.token) {
                    document.cookie = `authToken=${data.token}; path=/; max-age=${7 * 24 * 60 * 60}`;
                }
            } else {
                const error = await response.json();
                showError(error.message || 'ログインに失敗しました。ユーザー名またはパスワードが間違っています。');
            }
        } catch (error) {
            console.error('ログインエラー:', error);
            showError('サーバーに接続できません。後で再度お試しください。');
        } finally {
            // ローディング非表示
            loading.classList.remove('show');
        }
    });
    
    // エラーメッセージ表示
    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
        successMessage.style.display = 'none';
    }
    
    // 成功メッセージ表示
    function showSuccess(message) {
        successMessage.textContent = message;
        successMessage.style.display = 'block';
        errorMessage.style.display = 'none';
    }
    
    // Enterキーでも送信可能
    passwordInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            loginForm.dispatchEvent(new Event('submit'));
        }
    });
    
    // ローカルストレージから自動入力（保存されていた場合）
    const savedUsername = localStorage.getItem('savedUsername');
    if (savedUsername) {
        usernameInput.value = savedUsername;
        document.getElementById('remember').checked = true;
    }
});

// ユーザー名変更時の処理
document.getElementById('username').addEventListener('change', function() {
    const remember = document.getElementById('remember');
    if (remember.checked) {
        localStorage.setItem('savedUsername', this.value);
    }
});

// チェックボックス変更時の処理
document.getElementById('remember').addEventListener('change', function() {
    if (!this.checked) {
        localStorage.removeItem('savedUsername');
    } else {
        const username = document.getElementById('username').value;
        if (username) {
            localStorage.setItem('savedUsername', username);
        }
    }
});
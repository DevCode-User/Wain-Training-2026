// ダッシュボード処理
document.addEventListener('DOMContentLoaded', function() {
    // ログイン確認
    checkAuthentication();
    
    // グリーティング表示
    displayGreeting();
    
    // データ読み込み
    loadDashboardData();
});

// ログイン確認
function checkAuthentication() {
    const authToken = getCookie('authToken');
    const username = localStorage.getItem('currentUser');
    
    if (!authToken && !username) {
        // ログインしていない場合はログインページにリダイレクト
        window.location.href = '/';
    }
}

// グリーティング表示
function displayGreeting() {
    const username = localStorage.getItem('currentUser') || '管理者';
    const greeting = document.getElementById('greeting');
    if (greeting) {
        greeting.textContent = `ようこそ、${username}さん`;
    }
}

// ダッシュボードデータの読み込み
function loadDashboardData() {
    // APIからデータを取得（本来はサーバーから取得）
    const mockOrders = [
        {
            id: 'ORD-001',
            customer: '田中太郎',
            amount: '¥15,000',
            date: '2026-04-03 14:30',
            status: '配送中'
        },
        {
            id: 'ORD-002',
            customer: '鈴木花子',
            amount: '¥8,500',
            date: '2026-04-03 13:15',
            status: '確認待ち'
        },
        {
            id: 'ORD-003',
            customer: '佐藤次郎',
            amount: '¥25,300',
            date: '2026-04-03 12:00',
            status: '完了'
        },
        {
            id: 'ORD-004',
            customer: '伊藤美咲',
            amount: '¥6,200',
            date: '2026-04-03 11:45',
            status: '確認待ち'
        },
        {
            id: 'ORD-005',
            customer: '西村健太',
            amount: '¥18,900',
            date: '2026-04-03 10:30',
            status: '配送中'
        }
    ];
    
    // テーブルに注文データを表示
    displayOrders(mockOrders);
}

// 注文テーブルの表示
function displayOrders(orders) {
    const tbody = document.getElementById('ordersTable');
    if (!tbody) return;
    
    tbody.innerHTML = orders.map(order => `
        <tr>
            <td>${order.id}</td>
            <td>${order.customer}</td>
            <td>${order.amount}</td>
            <td>${order.date}</td>
            <td><span style="padding: 5px 10px; border-radius: 4px; background: ${getStatusColor(order.status)}; color: white; font-size: 12px;">${order.status}</span></td>
        </tr>
    `).join('');
}

// ステータスの色を取得
function getStatusColor(status) {
    const colors = {
        '配送中': '#ff9800',
        '確認待ち': '#f44336',
        '完了': '#4caf50',
        'キャンセル': '#999'
    };
    return colors[status] || '#999';
}

// クッキーから値を取得
function getCookie(name) {
    const nameEQ = name + "=";
    const cookies = document.cookie.split(';');
    for (let c of cookies) {
        c = c.trim();
        if (c.indexOf(nameEQ) === 0) {
            return c.substring(nameEQ.length);
        }
    }
    return '';
}

// ログアウト処理
function logout() {
    // クッキーをクリア
    document.cookie = 'authToken=; path=/; max-age=0';
    localStorage.removeItem('currentUser');
    
    // ログインページへリダイレクト
    window.location.href = '/';
}
// 注文ページ処理
document.addEventListener('DOMContentLoaded', function() {
    // カート内容を表示
    displayCart();
});

// カート内容の表示
function displayCart() {
    const cartTable = document.getElementById('cartTable');
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    
    if (!cartTable) return;
    
    if (cart.length === 0) {
        cartTable.innerHTML = `
            <tr>
                <td colspan="4" style="text-align: center; padding: 20px; color: #999;">
                    カートは空です
                </td>
            </tr>
        `;
        return;
    }
    
    let totalAmount = 0;
    cartTable.innerHTML = cart.map(item => {
        const priceNum = parseInt(item.price.replace(/¥|,/g, ''));
        const itemTotal = priceNum * item.quantity;
        totalAmount += itemTotal;
        
        return `
            <tr>
                <td>${item.name}</td>
                <td>${item.quantity}</td>
                <td>¥${itemTotal.toLocaleString()}</td>
                <td>
                    <button class="btn btn-secondary" style="padding: 5px 10px; font-size: 12px;" onclick="removeFromCart(${item.id})">削除</button>
                </td>
            </tr>
        `;
    }).join('');
    
    // 合計を追加
    const totalRow = document.createElement('tr');
    totalRow.style.background = '#f5f5f5';
    totalRow.innerHTML = `
        <td colspan="2" style="font-weight: bold; text-align: right;">合計:</td>
        <td style="font-weight: bold; color: #667eea;">¥${totalAmount.toLocaleString()}</td>
        <td></td>
    `;
    cartTable.appendChild(totalRow);
}

// カート内容をクリア
function clearCart() {
    if (confirm('カートを空にしますか？')) {
        localStorage.removeItem('cart');
        displayCart();
        alert('カートをクリアしました');
    }
}

// カートから削除
function removeFromCart(productId) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    cart = cart.filter(item => item.id !== productId);
    localStorage.setItem('cart', JSON.stringify(cart));
    displayCart();
    alert('アイテムを削除しました');
}

// チェックアウト
function checkout() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    
    if (cart.length === 0) {
        alert('カートが空です');
        return;
    }
    
    // ユーザーがログインしているか確認
    const currentUser = localStorage.getItem('currentUser');
    if (!currentUser) {
        alert('ログインしてからチェックアウトしてください');
        window.location.href = '/login';
        return;
    }
    
    // 注文確認
    const totalAmount = cart.reduce((sum, item) => {
        const priceNum = parseInt(item.price.replace(/¥|,/g, ''));
        return sum + (priceNum * item.quantity);
    }, 0);
    
    if (confirm(`合計金額：¥${totalAmount.toLocaleString()}\n\n注文を確定しますか？`)) {
        // 注文を送信（実際にはサーバーに送信）
        const order = {
            id: 'ORD-' + Date.now(),
            items: cart,
            total: totalAmount,
            date: new Date().toLocaleString('ja-JP'),
            status: '確認待ち',
            customer: currentUser
        };
        
        // ローカルストレージに注文を保存
        let orders = JSON.parse(localStorage.getItem('userOrders')) || [];
        orders.push(order);
        localStorage.setItem('userOrders', JSON.stringify(orders));
        
        // カートをクリア
        localStorage.removeItem('cart');
        
        alert('注文が確定しました。\nご利用ありがとうございました');
        window.location.href = '/';
    }
}
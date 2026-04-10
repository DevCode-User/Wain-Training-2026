// ホームページ処理
document.addEventListener('DOMContentLoaded', function() {
    // 商品データを読み込み
    loadProducts();
});

// 商品データの読み込み
function loadProducts() {
    // モック商品データ（本来はサーバーから取得）
    const mockProducts = [
        {
            id: 1,
            name: '商品A',
            description: '高品質な商品Aです',
            price: '¥1,000',
            image: '画像A'
        },
        {
            id: 2,
            name: '商品B',
            description: 'おすすめの商品Bです',
            price: '¥2,500',
            image: '画像B'
        },
        {
            id: 3,
            name: '商品C',
            description: '人気の商品Cです',
            price: '¥1,800',
            image: '画像C'
        },
        {
            id: 4,
            name: '商品D',
            description: '限定商品Dです',
            price: '¥3,200',
            image: '画像D'
        },
        {
            id: 5,
            name: '商品E',
            description: '新商品Eです',
            price: '¥2,100',
            image: '画像E'
        },
        {
            id: 6,
            name: '商品F',
            description: 'セール中の商品Fです',
            price: '¥900',
            image: '画像F'
        }
    ];
    
    // 商品グリッドに表示
    displayProducts(mockProducts);
}

// 商品の表示
function displayProducts(products) {
    const grid = document.getElementById('productsGrid');
    if (!grid) return;
    
    grid.innerHTML = products.map(product => `
        <div class="product-card">
            <div class="product-image">${product.image}</div>
            <div class="product-info">
                <h3>${product.name}</h3>
                <p>${product.description}</p>
                <div class="product-price">${product.price}</div>
                <button class="btn btn-primary" onclick="addToCart(${product.id}, '${product.name}', '${product.price}')">
                    カートに追加
                </button>
            </div>
        </div>
    `).join('');
}

// カートに追加
function addToCart(productId, productName, productPrice) {
    // 確認ダイアログ
    if (confirm(`${productName}をカートに追加しますか？`)) {
        // ローカルストレージに保存
        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        
        const item = {
            id: productId,
            name: productName,
            price: productPrice,
            quantity: 1
        };
        
        // 既存アイテムか確認
        const existingItem = cart.find(c => c.id === productId);
        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            cart.push(item);
        }
        
        localStorage.setItem('cart', JSON.stringify(cart));
        alert(`${productName}をカートに追加しました！`);
    }
}
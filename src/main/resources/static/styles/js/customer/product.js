document.addEventListener("DOMContentLoaded", function() {
	const minus = document.getElementById("minus");
	const plus = document.getElementById("plus");
	const qty = document.getElementById("qty");
	const addCart = document.getElementById("addCart");
	const buyNow = document.getElementById("buyNow");
	const cartIconCount = document.getElementById("cart-count");

	// ====== Tăng / Giảm số lượng ======
	minus.addEventListener("click", () => {
		let value = parseInt(qty.value) || 1;
		if (value > 1) qty.value = value - 1;
	});

	plus.addEventListener("click", () => {
		let value = parseInt(qty.value) || 1;
		qty.value = value + 1;
	});

	// ====== 🛒 Thêm vào giỏ hàng ======
	addCart.addEventListener("click", () => {
		const productId = addCart.dataset.id;
		const quantity = qty.value;

		fetch("/product/add-to-cart", {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: `productId=${productId}&quantity=${quantity}`,
			credentials: "same-origin" 
		})
			.then(async res => {
				const msg = await res.text();

				if (res.status === 401) {
					alert(msg); // ⚠️ Chưa đăng nhập
					setTimeout(() => window.location.href = "/login", 200);
				} else if (res.status === 403) {
					alert("Tài khoản của bạn không được phép mua hàng hoặc thêm vào giỏ!");
				} else if (res.ok) {
					alert(msg); // ✅ Thành công
					updateCartCount();
				} else {
					alert("Lỗi máy chủ!");
				}
			})
			.catch(() => alert("Không thể kết nối đến máy chủ."));
	});

	// ====== ⚡ Mua ngay ======
	buyNow.addEventListener("click", () => {
	    const productId = buyNow.dataset.id;
	    const quantity = qty.value;

	    // ✅ Gọi trước để kiểm tra quyền
	    fetch("/product/check-buy")
	        .then(async res => {
	            const msg = await res.text();

	            if (res.status === 401) {
	                alert(msg);
	                setTimeout(() => window.location.href = "/login", 300);
	            } else if (res.status === 403) {
	                alert(msg);
	            } else if (res.ok) {
	                // ✅ Nếu hợp lệ thì mới chuyển sang trang mua
	                window.location.href = `/product/buy-now/${productId}?quantity=${quantity}`;
	            }
	        })
	        .catch(() => alert("Không thể kết nối đến máy chủ!"));
	});


	// ====== 🧮 Cập nhật số lượng giỏ hàng ======
	function updateCartCount() {
		fetch("/cart/count")
			.then(res => res.json())
			.then(data => {
				if (cartIconCount) {
					cartIconCount.textContent = data.count > 99 ? "99+" : data.count;
					cartIconCount.style.display = data.count > 0 ? "inline-block" : "none";
				}
			});
	}

	updateCartCount();
});

document.addEventListener("DOMContentLoaded", () => {
	window.removeFavorite = function(btn) {
		const productId = btn.getAttribute("data-prod-id");

		if (!confirm("Bạn có chắc muốn xoá sản phẩm này khỏi danh sách yêu thích không?")) return;

		fetch(`/api/favorite-products/remove/${productId}`, {
			method: "DELETE",
			credentials: "same-origin"
		})
			.then(res => {
				if (res.status === 401) {
					alert("Vui lòng đăng nhập để tiếp tục!");
					setTimeout(() => window.location.href = "/login", 300);
					return;
				}
				if (res.ok) {
					btn.closest(".product-card").remove();

					if (document.querySelectorAll(".product-card").length === 0) {
						document.querySelector(".product-grid").outerHTML =
							`<div class="no-favorite" style="text-align:center; padding:50px 0;">
								<p style="font-size:1.1rem; color:#777;">Bạn chưa có sản phẩm yêu thích nào.</p>
								<a href="/" class="btn-back-home"
									style="display:inline-block; margin-top:16px; padding:10px 20px; background:#b53e50; color:#fff; border-radius:8px; text-decoration:none;">
									Tiếp tục mua sắm
								</a>
							</div>`;
					}
				} else {
					alert("Không thể xoá sản phẩm yêu thích!");
				}
			})
			.catch(() => alert("Lỗi kết nối đến máy chủ!"));
	};
});

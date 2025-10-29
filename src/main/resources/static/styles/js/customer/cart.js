document.addEventListener("DOMContentLoaded", () => {
  const cartContainer = document.querySelector(".cart-container");
  const emptyCart = document.querySelector(".empty-cart");

  // === Hàm cập nhật tổng tiền ===
  const updateSummary = () => {
    let subtotal = 0;
    document.querySelectorAll(".cart-item").forEach(item => {
      const checkbox = item.querySelector("input[type='checkbox']");
      const priceText = item.querySelector(".info p").innerText.replace(/[^\d]/g, '');
      const quantity = parseInt(item.querySelector(".quantity input").value);
      if (checkbox && checkbox.checked) subtotal += parseFloat(priceText) * quantity;
    });

    const total = subtotal;
    const summary = document.querySelector(".summary");
    if (summary) {
      summary.querySelector(".subtotal").innerText = subtotal.toLocaleString("vi-VN") + " ₫";
      summary.querySelector(".total strong").innerText = total.toLocaleString("vi-VN") + " ₫";
    }
  };

  // === Hàm cập nhật DB ===
  const updateQuantityDB = (id, quantity) => {
    fetch(`/cart/update?id=${id}&quantity=${quantity}`, { method: "POST" })
      .catch(err => console.error("Cập nhật giỏ hàng lỗi:", err));
  };

  const removeItemDB = (id) => {
    fetch(`/cart/remove?id=${id}`, { method: "POST" })
      .catch(err => console.error("Xóa sản phẩm lỗi:", err));
  };

  // === Hàm kiểm tra giỏ hàng trống ===
  const checkEmptyCart = () => {
    const items = document.querySelectorAll(".cart-item");
    if (items.length === 0) {
      cartContainer.style.display = "none";
      emptyCart.style.display = "block";
    }
  };

  // === Tăng/giảm số lượng ===
  document.querySelectorAll(".plus, .minus").forEach(btn => {
    btn.addEventListener("click", e => {
      const input = e.target.closest(".quantity").querySelector("input");
      let qty = parseInt(input.value);
      if (e.target.classList.contains("plus")) qty++;
      else if (qty > 1) qty--;

      input.value = qty;
      updateSummary();
      updateQuantityDB(input.dataset.id, qty);
    });
  });

  // === Khi gõ số lượng trực tiếp ===
  document.querySelectorAll(".quantity input").forEach(inp => {
    inp.addEventListener("change", e => {
      let qty = parseInt(e.target.value);
      if (isNaN(qty) || qty < 1) qty = 1;
      e.target.value = qty;
      updateSummary();
      updateQuantityDB(inp.dataset.id, qty);
    });
  });

  // === Xóa sản phẩm ===
  document.querySelectorAll(".remove").forEach(btn => {
    btn.addEventListener("click", e => {
      const id = btn.dataset.id;
      e.target.closest(".cart-item").remove();
      updateSummary();
      removeItemDB(id);
      checkEmptyCart(); // 🔹 thêm dòng này
    });
  });

  // === Khi tích / bỏ chọn sản phẩm ===
  document.querySelectorAll(".cart-item input[type='checkbox']").forEach(cb => {
    cb.addEventListener("change", updateSummary);
  });

  // Cập nhật tổng ban đầu
  updateSummary();
});

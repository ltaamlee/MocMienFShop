(function() {
  const modal = document.getElementById('reviewModal');
  const form = document.getElementById('reviewForm');
  const orderIdInput = document.getElementById('orderId');

  window.openReviewPopup = function(btn) {
    const orderId = btn?.getAttribute('data-order-id');
    if (!orderId) return;
    orderIdInput.value = orderId;
    // set form action
    form.setAttribute('action', `/customer/orders/${orderId}/review`);
    modal.classList.remove('hidden');
  };

  window.closeReviewPopup = function() {
    modal.classList.add('hidden');
    form.reset();
  };

  if (form) {
    form.addEventListener('submit', async function(e) {
      e.preventDefault();
      const orderId = orderIdInput.value;
      const rating = (form.querySelector('input[name="rating"]:checked') || {}).value;
      const comment = form.querySelector('textarea[name="comment"]').value || '';
      if (!rating) { alert('Vui lòng chọn số sao đánh giá'); return; }
      try {
        const res = await fetch(`/customer/orders/${orderId}/review`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
          body: new URLSearchParams({ rating, comment })
        });
        const text = await res.text();
        if (!res.ok || text !== 'success') throw new Error(text || 'Lỗi gửi đánh giá');
        alert('Cảm ơn bạn đã đánh giá!');
        closeReviewPopup();
        // optional refresh to update counts
        setTimeout(() => window.location.reload(), 300);
      } catch (err) {
        alert(err.message || 'Không thể gửi đánh giá');
      }
    });
  }
})();



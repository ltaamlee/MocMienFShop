let currentPage = 0;
const pageSize = 10;

function fmtDate(dt) {
	if (!dt) return '—';
	const d = new Date(dt);
	return d.toLocaleString('vi-VN');
}
function fmtMoney(v) {
	if (v == null) return '0 ₫';
	try { return Number(v).toLocaleString('vi-VN') + ' ₫'; } catch { return v + ' ₫'; }
}
function badge(status) {
	const map = {
		NEW: ['badge-pending', 'Đơn hàng mới'],
		PENDING: ['badge-inactive', 'Chờ xử lý'],
		CONFIRMED: ['badge-active', 'Đã xác nhận']
	};
	const [cls, text] = map[status] || ['badge', status || '—'];
	return `<span class="badge ${cls}">${text}</span>`;
}

async function loadOrders(page = 0) {
	currentPage = page;
	const keyword = document.getElementById('keyword')?.value.trim() || '';
	const status = document.getElementById('status')?.value || '';

	const url = new URL('/api/vendor/orders', window.location.origin);
	url.searchParams.set('page', page);
	url.searchParams.set('size', pageSize);
	if (keyword) url.searchParams.set('keyword', keyword);
	if (status) url.searchParams.set('status', status);

	const tbody = document.getElementById('ordersTbody');
	tbody.innerHTML = `<tr><td colspan="8" class="text-center">Đang tải…</td></tr>`;

	try {
		const res = await fetch(url);
		const data = await res.json();

		if (!data.content || data.content.length === 0) {
			tbody.innerHTML = `<tr><td colspan="8" class="text-center">Không có đơn hàng</td></tr>`;
			renderPagination(0);
			return;
		}

		tbody.innerHTML = '';
		data.content.forEach((o, idx) => {
			const actions = renderActions(o);
			tbody.insertAdjacentHTML('beforeend', `
        <tr>
          <td>${idx + 1 + page * pageSize}</td>
          <td>${o.id}</td>
          <td>${o.customerName || '—'}</td>
          <td>${fmtDate(o.createdAt)}</td>
          <td>${fmtMoney(o.total)}</td>
          <td>${o.paymentMethodDisplay || '—'}</td>
          <td>${badge(o.status)}</td>
          <td class="text-right">
            <div class="action-buttons">
              <button class="btn-view" title="Xem chi tiết" onclick="openDetail('${o.id}')">
                <i class="fas fa-eye"></i> Xem
              </button>
              ${actions}
            </div>
          </td>
        </tr>
      `);
		});

		renderPagination(data.totalPages || 0);
	} catch (e) {
		console.error(e);
		tbody.innerHTML = `<tr><td colspan="8" class="text-center" style="color:#f44336">Lỗi tải dữ liệu</td></tr>`;
	}
}

function renderActions(o) {
	// Flow: NEW -> PENDING -> CONFIRMED
	if (o.status === 'NEW') {
		return `
      <button class="btn-edit" onclick="changeStatus('${o.id}','PENDING')">
        <i class="fas fa-box"></i> Chuyển PENDING
      </button>`;
	}
	if (o.status === 'PENDING') {
		return `
      <button class="btn-approve" onclick="changeStatus('${o.id}','CONFIRMED')">
        <i class="fas fa-check"></i> Xác nhận
      </button>`;
	}
	return '';
}

async function changeStatus(orderId, to) {
	try {
		const res = await fetch(`/api/vendor/orders/${orderId}/status?to=${to}`, { method: 'PUT' });
		if (!res.ok) {
			const msg = await res.text();
			alert(msg || 'Không thể cập nhật trạng thái');
			return;
		}
		await loadOrders(currentPage);
	} catch (e) {
		alert('Lỗi kết nối');
	}
}

async function openDetail(orderId) {
	try {
		const res = await fetch(`/api/vendor/orders/${orderId}`);
		if (!res.ok) throw new Error('Không tải được chi tiết');
		const d = await res.json();

		const itemsHtml = (d.items && d.items.length)
			? d.items.map((it, i) => `
          <tr>
            <td>${i + 1}</td>
            <td>${it.productName || ('#' + it.productId)}</td>
            <td>${it.quantity}</td>
            <td>${fmtMoney(it.price)}</td>
            <td>${fmtMoney(it.amount)}</td>
          </tr>`).join('')
			: `<tr><td colspan="5" class="text-center">Không có sản phẩm</td></tr>`;

		document.getElementById('detailBody').innerHTML = `
      <table class="table-detail">
        <tr><th>Mã đơn</th><td>${d.id}</td></tr>
        <tr><th>Khách hàng</th><td>${d.customerName || '—'}</td></tr>
        <tr><th>SĐT</th><td>${d.customerPhone || '—'}</td></tr>
        <tr><th>Thanh toán</th><td>${d.paymentMethodDisplay || '—'}</td></tr>
        <tr><th>Trạng thái</th><td>${badge(d.status)}</td></tr>
        <tr><th>Ngày đặt</th><td>${fmtDate(d.createdAt)}</td></tr>
        <tr><th>Cập nhật</th><td>${fmtDate(d.updatedAt)}</td></tr>
      </table>

      <h3 class="mt-2 mb-1" style="font-size:18px;">Chi tiết sản phẩm</h3>
      <table class="table-detail">
        <thead>
          <tr><th>#</th><th>Sản phẩm</th><th>SL</th><th>Giá</th><th>Thành tiền</th></tr>
        </thead>
        <tbody>${itemsHtml}</tbody>
      </table>

      <table class="table-detail mt-2">
        <tr><th>Tổng tiền hàng</th><td>${fmtMoney(d.itemsTotal)}</td></tr>
        <tr><th>Phí vận chuyển</th><td>${fmtMoney(d.shippingFee)}</td></tr>
        <tr><th><b>Tổng thanh toán</b></th><td><b>${fmtMoney(d.grandTotal)}</b></td></tr>
      </table>
    `;
		openModal('orderDetailModal');
	} catch (e) {
		console.error(e);
		alert('Không tải được chi tiết đơn hàng');
	}
}

function renderPagination(totalPages) {
	const pag = document.getElementById('pagination');
	pag.innerHTML = '';
	if (!totalPages || totalPages <= 1) return;
	for (let i = 0; i < totalPages; i++) {
		const btn = document.createElement('button');
		btn.textContent = i + 1;
		btn.disabled = i === currentPage;
		btn.onclick = () => loadOrders(i);
		pag.appendChild(btn);
	}
}

function openModal(id) { document.getElementById(id)?.classList.add('show'); document.body.style.overflow = 'hidden'; }
function closeModal(id) { document.getElementById(id)?.classList.remove('show'); document.body.style.overflow = ''; }

document.addEventListener('DOMContentLoaded', () => {
	loadOrders();
	document.getElementById('status')?.addEventListener('change', () => loadOrders(0));
});

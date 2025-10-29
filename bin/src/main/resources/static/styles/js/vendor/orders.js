let currentPage = 0;
const pageSize = 10;

// ================= THỐNG KÊ ĐƠN HÀNG =================
async function loadOrderStats() {
	try {
		const res = await fetch('/api/manager/orders/stats');
		if (!res.ok) throw new Error('Không thể tải thống kê');

		const data = await res.json();
		document.getElementById('totalOrdersStat').textContent = data.totalOrders ?? 0;
		document.getElementById('closedOrdersStat').textContent = data.closedOrders ?? 0;
		document.getElementById('deliveringOrdersStat').textContent = data.deliveringOrders ?? 0;
		document.getElementById('cancelledOrdersStat').textContent = data.cancelledOrders ?? 0;
	} catch (err) {
		console.error('Lỗi tải thống kê:', err);
		showNotification('❌ Lỗi tải thống kê', true);
	}
}

// ================= LOAD ĐƠN HÀNG (AJAX + FILTER + SEARCH + PAGE) =================
async function loadOrders(page = 0) {
	currentPage = page;

	const status = document.querySelector('select[name="status"]')?.value || '';
	const keyword = document.querySelector('input[name="keyword"]')?.value.trim() || '';

	const url = new URL('/api/manager/orders', window.location.origin);
	url.searchParams.append('page', currentPage);
	url.searchParams.append('size', pageSize);
	if (status) url.searchParams.append('trangThai', status); // nếu rỗng -> tất cả
	if (keyword) url.searchParams.append('keyword', keyword);

	try {
		const res = await fetch(url);
		if (!res.ok) throw new Error('Không thể tải danh sách đơn hàng');

		const data = await res.json();
		renderOrderTable(data.content);
		renderPagination(data.totalPages);

	} catch (err) {
		console.error('❌ Lỗi tải đơn hàng:', err);
		const tableBody = document.getElementById('orderTableBody');
		tableBody.innerHTML = `<tr><td colspan="8" style="text-align:center; color:red;">Không thể tải dữ liệu</td></tr>`;
	}
}

// ================= HIỂN THỊ BẢNG =================
function renderOrderTable(orders) {
	const tableBody = document.getElementById('orderTableBody');
	tableBody.innerHTML = '';

	if (!orders || orders.length === 0) {
		tableBody.innerHTML = `<tr><td colspan="8" style="text-align:center;">Không có đơn hàng nào!</td></tr>`;
		return;
	}

	orders.forEach((order, index) => {
		const row = `
      <tr>
        <td>${index + 1 + currentPage * pageSize}</td>
        <td>${order.hoTenKH || ('Mã KH: ' + order.maKH)}</td>
        <td>${formatDate(order.ngayDat)}</td>
        <td>${formatCurrency(order.tongTien)}</td>
        <td>${order.phuongThucThanhToan}</td>
        <td>${formatDate(order.ngayDat)}</td>
        <td>${order.trangThai}</td>
        <td>
          <button class="btn-view" onclick="viewOrderModal(${order.maDH})" title="Xem chi tiết">
            <i class="fas fa-eye"></i>
          </button>
          <button class="btn-approve" onclick="openAssignShipperModal(${order.maDH})" title="Phân công shipper">
            <i class="fas fa-truck"></i>
          </button>
        </td>
      </tr>
    `;
		tableBody.insertAdjacentHTML('beforeend', row);
	});
}


// ================= PHÂN TRANG =================
function renderPagination(totalPages) {
	const paginationDiv = document.getElementById('orderPagination');
	paginationDiv.innerHTML = '';

	if (totalPages <= 1) return;

	for (let i = 0; i < totalPages; i++) {
		const btn = document.createElement('button');
		btn.textContent = i + 1;
		btn.disabled = i === currentPage;
		btn.onclick = () => loadOrders(i);
		paginationDiv.appendChild(btn);
	}
}

// ================= HÀM HỖ TRỢ =================
function formatDate(dateStr) {
	if (!dateStr) return '';
	const date = new Date(dateStr);
	return date.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function formatCurrency(amount) {
	if (!amount) return '0 ₫';
	return amount.toLocaleString('vi-VN') + ' ₫';
}

function showNotification(message, isError = false, duration = 3000) {
	const notification = document.getElementById('notification');
	notification.textContent = message;
	notification.classList.toggle('error', isError);
	notification.style.display = 'block';
	setTimeout(() => notification.style.display = 'none', duration);
}

// ================= MODAL =================
function openModal(modalId) {
	document.getElementById(modalId)?.classList.add('show');
	document.body.style.overflow = 'hidden';
}
function closeModal(modalId) {
	document.getElementById(modalId)?.classList.remove('show');
	document.body.style.overflow = '';
}

// ================= XEM CHI TIẾT ĐƠN HÀNG =================
async function viewOrderModal(orderId) {
	try {
		const res = await fetch(`/api/manager/orders/view/${orderId}`);
		if (!res.ok) throw new Error('Không thể tải chi tiết đơn hàng');
		const order = await res.json();

		const modal = document.getElementById('viewOrderModal');
		modal.dataset.orderId = orderId;

		document.getElementById('detailOrderId').innerText = order.maDH ?? '';
		document.getElementById('detailCustomer').innerText = order.hoTenKH ?? '';
		document.getElementById('detailOrderDate').innerText = order.ngayDat ? formatDate(order.ngayDat) : '';
		document.getElementById('detailPayment').innerText = order.phuongThucThanhToan ?? '';
		document.getElementById('detailStatus').innerText = order.trangThai ?? '';
		document.getElementById('detailAddress').innerText = order.diaChiGiao ?? '';
		document.getElementById('detailPhone').innerText = order.sdtNguoiNhan ?? '';

		const employeeLabel = [
			order.tenNhanVienDuyet ? `Duyệt: ${order.tenNhanVienDuyet}` : null,
			order.tenNhanVienDongGoi ? `Đóng gói: ${order.tenNhanVienDongGoi}` : null,
			order.tenNhanVienGiaoHang ? `Giao hàng: ${order.tenNhanVienGiaoHang}` : null,
		].filter(Boolean).join(' | ');
		document.getElementById('detailEmployee').innerText = employeeLabel || '—';

		document.getElementById('detailUpdatedAt').innerText = order.ngayCapNhat ? formatDate(order.ngayCapNhat) : '';

		const tbody = document.getElementById('detailProductsBody');
		tbody.innerHTML = '';
		let totalItemsAmount = 0;
		if (order.chiTietDonHang?.length) {
			order.chiTietDonHang.forEach((item, index) => {
				const amount = (item.soLuong && item.giaBan) ? item.soLuong * item.giaBan : 0;
				totalItemsAmount += amount;
				tbody.insertAdjacentHTML('beforeend', `
          <tr>
            <td>${index + 1}</td>
            <td>${item.tenSP ?? ''}</td>
            <td>${item.soLuong ?? ''}</td>
            <td>${item.giaBan ? Number(item.giaBan).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) : ''}</td>
            <td>${amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</td>
          </tr>
        `);
			});
			tbody.insertAdjacentHTML('beforeend', `
        <tr>
          <td colspan="4" style="text-align:right;font-weight:bold;">Tổng tiền hàng</td>
          <td style="font-weight:bold;">${totalItemsAmount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</td>
        </tr>
      `);
		} else {
			tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Không có sản phẩm</td></tr>';
		}

		const shippingFee = Number(order.phiVanChuyen ?? 0);
		document.getElementById('detailShippingFee').innerText = shippingFee.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
		document.getElementById('detailTotal').innerText = (totalItemsAmount + shippingFee).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });

		openModal('viewOrderModal');
	} catch (err) {
		console.error(err);
		showNotification('❌ ' + err.message, true);
	}
}


// ================= DUYỆT ĐƠN HÀNG =================
let currentApproveOrderId = null;
let currentApproveOrderStatus = null;

function approveOrder(orderId, orderStatus) {
	currentApproveOrderId = orderId;
	currentApproveOrderStatus = orderStatus?.trim().toLowerCase();

	const modal = document.getElementById('confirmApproveModal');
	const body = modal.querySelector('.modal-body');
	const btnSubmit = modal.querySelector('.btn-submit');
	const btnCancel = modal.querySelector('.btn-cancel');

	if (currentApproveOrderStatus !== 'chờ xử lý') {
		body.innerHTML = `<p>Đơn hàng đã được duyệt!</p>`;
		btnSubmit.style.display = 'none';  // ẩn nút "Có"
		btnCancel.textContent = 'Đóng';     // đổi nút "Hủy" thành "Đóng"
		btnCancel.style.display = 'inline-block';
	} else {
		body.innerHTML = `<p>Bạn có chắc chắn muốn duyệt đơn hàng này không?</p>`;
		btnSubmit.style.display = 'inline-block';
		btnCancel.textContent = 'Hủy';      // giữ lại chữ Hủy
		btnCancel.style.display = 'inline-block';
	}

	openModal('confirmApproveModal');
}


async function confirmApproveOrder() {
	if (!currentApproveOrderId) return;

	try {
		const res = await fetch(`/api/manager/orders/approve/${currentApproveOrderId}`, {
			method: 'PUT',
			headers: { 'Content-Type': 'application/json' }
		});
		if (!res.ok) throw new Error(await res.text() || 'Không thể duyệt đơn hàng');

		showNotification('✅ Đơn hàng đã được duyệt!');
		closeModal('confirmApproveModal');
		currentApproveOrderId = null;
		loadOrderStats();
		loadOrders(currentPage);

	} catch (err) {
		console.error(err);
		showNotification('❌ ' + err.message, true);
	}
}


let cancelRequestOrderId = null;
let cancelRequestOrderStatus = null;
let cancelRequestStatus = null; // NONE, PENDING, APPROVED, REJECTED

function openRequestCancelModal(orderId, orderStatus, orderCancelStatus) {
	cancelRequestOrderId = orderId;
	cancelRequestOrderStatus = orderStatus?.trim().toLowerCase();
	cancelRequestStatus = orderCancelStatus || 'NONE'; // NONE = chưa gửi

	const modalMsg = document.getElementById('cancelModalMsg');
	const reasonInput = document.getElementById('requestCancelReason');
	const submitBtn = document.querySelector('#requestCancelModal .btn-submit');
	const cancelBtn = document.querySelector('#requestCancelModal .btn-cancel');

	reasonInput.value = '';
	reasonInput.style.display = 'block';
	submitBtn.style.display = 'inline-block';
	cancelBtn.textContent = 'Hủy';
	cancelBtn.style.display = 'inline-block';

	// Nếu đã gửi yêu cầu → ẩn textarea + nút gửi
	if (cancelRequestStatus !== 'NONE') {
		modalMsg.textContent = "⚠️ Đơn hàng đã có yêu cầu hủy trước đó. Vui lòng chờ quản lý xử lý.";
		reasonInput.style.display = 'none';
		submitBtn.style.display = 'none';
		cancelBtn.textContent = 'Đóng';
	}
	// Nếu đơn chưa duyệt → vẫn có thể gửi
	else if (cancelRequestOrderStatus === 'chờ xử lý') {
		modalMsg.textContent = "Đơn chưa duyệt, bạn có thể gửi yêu cầu hủy để quản lý xử lý.";
	}
	// Nếu đơn đã duyệt → yêu cầu sẽ được quản lý phê duyệt
	else {
		modalMsg.textContent = "Đơn đã duyệt, yêu cầu hủy sẽ được quản lý phê duyệt.";
	}

	modalMsg.classList.remove('error', 'success');
	openModal('requestCancelModal');
}



async function sendCancelRequest() {
	const reason = document.getElementById('requestCancelReason').value.trim();
	const modalMsg = document.getElementById('cancelModalMsg');
	modalMsg.classList.remove('error', 'success');

	if (!reason) {
		modalMsg.textContent = "Vui lòng nhập lý do hủy!";
		modalMsg.classList.add('error');
		return;
	}
	if (!cancelRequestOrderId) {
		modalMsg.textContent = "Không xác định được đơn hàng!";
		modalMsg.classList.add('error');
		return;
	}

	try {
		const res = await fetch(`/api/manager/orders/${cancelRequestOrderId}/request-cancel`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ reason })
		});

		const data = await res.json().catch(() => ({}));

		if (!res.ok) {
			const msg = data.message || "Gửi yêu cầu hủy thất bại!";

			if (msg.includes("đã có yêu cầu hủy trước đó")) {
				modalMsg.textContent = "⚠️ Đơn hàng đã có yêu cầu hủy trước đó. Vui lòng chờ quản lý xử lý.";
				document.getElementById('requestCancelReason').style.display = 'none';
				document.querySelector('#requestCancelModal .btn-submit').style.display = 'none';
				document.querySelector('#requestCancelModal .btn-cancel').textContent = 'Đóng';
			} else {
				modalMsg.textContent = msg;
			}

			modalMsg.classList.add('error');
			setTimeout(() => loadOrders(currentPage), 2000);
			return;
		}

		modalMsg.textContent = "✅ Yêu cầu hủy đã gửi đến quản lý!";
		modalMsg.classList.add('success');

		setTimeout(() => {
			closeModal('requestCancelModal');
			loadOrders(currentPage);
		}, 1500);

	} catch (err) {
		console.error("Cancel request error:", err);
		modalMsg.textContent = '❌ ' + (err.message || "Lỗi kết nối!");
		modalMsg.classList.add('error');
	}
}








let assignTargetOrderId = null;

async function openAssignShipperModal(orderId) {
	assignTargetOrderId = orderId;

	// load danh sách shipper
	await loadShipperOptions();

	// gợi ý
	const hint = document.getElementById('assignHint');
	hint.textContent = 'Chỉ phân công cho đơn đang ở trạng thái "ĐÃ ĐÓNG ĐƠN".';

	openModal('assignShipperModal');
}

async function loadShipperOptions() {
	const sel = document.getElementById('shipperSelect');
	sel.innerHTML = '<option value="">— Đang tải danh sách shipper… —</option>';

	try {
		const res = await fetch('/api/manager/orders/shippers');
		if (!res.ok) throw new Error('Không tải được danh sách shipper');

		const data = await res.json(); // [{id, name}]
		if (!data || data.length === 0) {
			sel.innerHTML = '<option value="">(Chưa có nhân viên giao hàng)</option>';
			return;
		}

		sel.innerHTML = '<option value="">— Chọn shipper —</option>';
		data.forEach(opt => {
			const o = document.createElement('option');
			o.value = opt.id;
			o.textContent = `#${opt.id} — ${opt.name}`;
			sel.appendChild(o);
		});
	} catch (e) {
		console.error(e);
		sel.innerHTML = '<option value="">Lỗi tải shipper</option>';
	}
} async function assignShipperToOrder() {
	const sel = document.getElementById('shipperSelect');
	const shipperId = sel.value ? Number(sel.value) : null;
	if (!assignTargetOrderId) return showNotification('Không xác định được đơn hàng!', true);
	if (!shipperId) return showNotification('Vui lòng chọn shipper!', true);

	const btn = document.querySelector('#assignShipperModal .btn-submit');
	const oldText = btn.textContent;
	btn.disabled = true;
	btn.textContent = 'Đang phân công…';

	try {
		const csrf = getCsrf();
		const headers = {};
		if (csrf) headers[csrf.header] = csrf.token;

		const res = await fetch(`/api/manager/orders/${assignTargetOrderId}/assign-shipper?employeeId=${shipperId}`, {
			method: 'PUT',
			headers
		});

		// Debug nhẹ nếu cần
		console.debug('assign-shipper status:', res.status);

		let data = null;
		const hasBody = res.status !== 204; // nhiều server không set content-length
		if (hasBody) {
			const text = await res.text();
			if (text) { try { data = JSON.parse(text); } catch { } }
		}

		if (res.status === 401 || res.status === 403) {
			showNotification('❌ Phiên đăng nhập hết hạn hoặc thiếu CSRF. Vui lòng tải lại trang và thử lại.', true, 5000);
			return;
		}

		if (!res.ok) {
			const msg = (data && (data.message || data.error)) || `Phân công thất bại (HTTP ${res.status})`;
			showNotification('❌ ' + msg, true);
			return;
		}

		// Thành công
		showNotification('✅ Đã phân công shipper và chuyển trạng thái sang ĐANG GIAO!');
		closeModal('assignShipperModal');
		loadOrders(currentPage);
		if (typeof loadOrderStats === 'function') loadOrderStats();

	} catch (err) {
		console.error(err);
		showNotification('❌ ' + (err.message || 'Lỗi kết nối!'), true);
	} finally {
		btn.disabled = false;
		btn.textContent = oldText;
	}
}

async function openAssignShipperModal(orderId) {
	assignTargetOrderId = orderId;
	const sel = document.getElementById('shipperSelect');
	sel.innerHTML = '<option value="">— Đang tải danh sách shipper… —</option>';
	await loadShipperOptions();
	document.getElementById('assignHint').textContent = 'Chỉ phân công cho đơn đang ở trạng thái "ĐÃ ĐÓNG ĐƠN".';
	openModal('assignShipperModal');
}


function getCookie(name) {
	return document.cookie.split('; ').find(row => row.startsWith(name + '='))?.split('=')[1];
}

function getCsrf() {
	// Ưu tiên meta (chuẩn Thymeleaf)
	const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
	const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
	if (token && header) return { header, token };

	// Fallback nếu dùng Cookie XSRF-TOKEN (Spring Security với CookieCsrfTokenRepository)
	const xsrf = getCookie('XSRF-TOKEN');
	if (xsrf) return { header: 'X-XSRF-TOKEN', token: decodeURIComponent(xsrf) };

	return null; // không có CSRF
}



// ================= INIT =================
document.addEventListener('DOMContentLoaded', () => {
	loadOrderStats();
	loadOrders();

	// Search form
	document.getElementById('searchFilterForm')?.addEventListener('submit', e => {
		e.preventDefault();
		loadOrders(0);
	});

	// Filter trạng thái
	document.querySelector('select[name="status"]')?.addEventListener('change', () => loadOrders(0));

	// Đóng modal khi click ra ngoài hoặc nhấn ESC
	document.addEventListener('click', e => { if (e.target.classList.contains('modal')) closeModal(e.target.id); });
	document.addEventListener('keydown', e => { if (e.key === 'Escape') document.querySelectorAll('.modal.show').forEach(m => closeModal(m.id)); });
});
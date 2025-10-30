let map, marker;

// 🧩 Lấy token CSRF để gửi API bảo mật
function getCsrf() {
	const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
	const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
	return token && header ? { header, token } : null;
}

// 🗺️ Khởi tạo bản đồ và cập nhật vị trí shipper
async function initMap() {
	const VN_BOUNDS = L.latLngBounds(L.latLng(8.179, 102.144), L.latLng(23.393, 109.469));

	map = L.map('map', { maxBounds: VN_BOUNDS, maxBoundsViscosity: 1.0 });
	L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
		maxZoom: 19,
		attribution: '&copy; OpenStreetMap & CARTO'
	}).addTo(map);

	map.fitBounds(VN_BOUNDS);
	marker = L.marker([16.047, 108.206]).addTo(map); // trung tâm VN

	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(pos => {
			const { latitude, longitude } = pos.coords;
			const latlng = [latitude, longitude];
			marker.setLatLng(latlng);
			map.setView(latlng, 15);
			updateLocation(latitude, longitude);
		}, () => { });

		navigator.geolocation.watchPosition(pos => {
			const { latitude, longitude } = pos.coords;
			marker.setLatLng([latitude, longitude]);
			updateLocation(latitude, longitude);
		}, () => { });
	}
}

// 📡 Gửi vị trí lên server
async function updateLocation(lat, lng) {
	try {
		const csrf = getCsrf();
		const headers = { 'Content-Type': 'application/json' };
		if (csrf) headers[csrf.header] = csrf.token;
		await fetch('/api/shipper/location', {
			method: 'POST',
			headers,
			body: JSON.stringify({ lat, lng })
		});
	} catch (e) {
		console.error('Lỗi cập nhật vị trí:', e);
	}
}

// 🔄 Tải trạng thái hoạt động của shipper
async function loadAvailability() {
	try {
		const res = await fetch('/api/shipper/me');
		if (!res.ok) return;

		const me = await res.json();
		const tg = document.getElementById('availabilityToggle');
		const tx = document.getElementById('availabilityText');

		if (tg && tx) {
			tg.checked = !!me.available;
			tx.textContent = me.available ? '🟢 Trực tuyến' : '⚫ Ngoại tuyến';
			tx.style.color = me.available ? '#10b981' : '#6b7280';
		}
	} catch (e) {
		console.error('Error loading availability:', e);
	}
}

// ⚙️ Chuyển đổi trạng thái trực tuyến/ngoại tuyến
async function toggleAvailability() {
	const tg = document.getElementById('availabilityToggle');
	if (!tg) return;

	try {
		const csrf = getCsrf();
		const headers = { 'Content-Type': 'application/json' };
		if (csrf) headers[csrf.header] = csrf.token;

		const res = await fetch('/api/shipper/availability', {
			method: 'PATCH',
			headers,
			body: JSON.stringify({ available: tg.checked })
		});

		if (!res.ok) {
			tg.checked = !tg.checked;
			alert('Không thể thay đổi trạng thái. Vui lòng thử lại.');
			return;
		}
		await loadAvailability();
	} catch (e) {
		tg.checked = !tg.checked;
		alert('Lỗi kết nối. Vui lòng thử lại.');
	}
}

// 📦 Lấy danh sách đơn hàng
async function loadOrders() {
	const [availRes, assignedRes] = await Promise.all([
		fetch('/api/shipper/orders/available'),
		fetch('/api/shipper/orders/assigned')
	]);
	const avail = availRes.ok ? await availRes.json() : [];
	const assigned = assignedRes.ok ? await assignedRes.json() : [];

	renderOrderList('availableOrders', avail, true);
	renderOrderList('assignedOrders', assigned, false);
}

// 🧾 Render danh sách đơn hàng
function renderOrderList(containerId, orders, canAccept) {
	const c = document.getElementById(containerId);
	if (!c) return;
	c.innerHTML = '';

	if (!orders || orders.length === 0) {
		c.innerHTML = `
			<div class="empty-state">
				<i class="fa fa-inbox"></i>
				<p>Không có đơn hàng nào</p>
			</div>`;
		return;
	}

	orders.forEach(o => {
		const div = document.createElement('div');
		div.className = canAccept ? 'order-card available' : 'order-card shipping';

		const statusClass = {
			'CONFIRMED': 'confirmed',
			'SHIPPING': 'shipping',
			'DELIVERED': 'delivered'
		}[(o.status || '').toUpperCase()] || '';

		const statusDisplay = {
			'CONFIRMED': 'Chờ lấy hàng',
			'SHIPPING': 'Đang giao',
			'DELIVERED': 'Đã giao'
		}[(o.status || '').toUpperCase()] || o.status;

		const actions = [];
		if (canAccept) {
			actions.push(`
				<button class="btn-accept" data-accept="${o.id}">
					<i class="fa fa-check-circle"></i> Nhận đơn ngay
				</button>`);
		} else {
			const st = (o.status || '').toUpperCase();
			if (st === 'CONFIRMED') {
				actions.push(`
					<button class="btn-start" data-status="SHIPPING" data-id="${o.id}">
						<i class="fa fa-shipping-fast"></i> Bắt đầu giao
					</button>`);
			}
			if (st === 'SHIPPING') {
				actions.push(`
					<button class="btn-complete" data-status="DELIVERED" data-id="${o.id}">
						<i class="fa fa-check-double"></i> Giao thành công
					</button>
					<button class="btn-return" data-status="RETURNED_REFUNDED" data-id="${o.id}">
						<i class="fa fa-undo"></i> Hoàn hàng
					</button>`);
			}
		}

		div.innerHTML = `
			<div class="order-info">
				<div class="order-info-row"><span class="label">Mã đơn:</span><span class="value">${o.id}</span></div>
				<div class="order-info-row"><span class="label">Cửa hàng:</span><span class="value">${o.storeName || 'N/A'}</span></div>
				<div class="order-info-row"><span class="label">Khách hàng:</span><span class="value">${o.customerName || 'N/A'}</span></div>
				<div class="order-info-row"><span class="label">Trạng thái:</span><span class="status-badge ${statusClass}">${statusDisplay}</span></div>
			</div>
			<div class="action-buttons">${actions.join('')}</div>`;
		c.appendChild(div);
	});
}

// ✅ Nhận đơn hàng
async function acceptOrder(id) {
	if (!confirm('Bạn có chắc muốn nhận đơn hàng này?')) return;

	try {
		const csrf = getCsrf();
		const headers = csrf ? { [csrf.header]: csrf.token } : {};

		const res = await fetch(`/api/shipper/orders/${id}/accept`, { method: 'PATCH', headers });
		if (!res.ok) {
			if (res.status === 409) alert('Đơn hàng đã được shipper khác nhận!');
			else alert('Nhận đơn thất bại. Vui lòng thử lại!');
			return;
		}
		alert('✓ Đã nhận đơn thành công!');
		loadOrders();
	} catch (err) {
		alert('Lỗi kết nối. Vui lòng thử lại!');
	}
}

// 🚀 Khi trang tải xong
document.addEventListener('DOMContentLoaded', () => {
	initMap();
	loadAvailability();
	loadOrders();

	document.getElementById('availabilityToggle')?.addEventListener('change', toggleAvailability);

	document.getElementById('availableOrders')?.addEventListener('click', e => {
		const btn = e.target.closest('button[data-accept]');
		if (btn) acceptOrder(btn.getAttribute('data-accept'));
	});

	document.getElementById('assignedOrders')?.addEventListener('click', async e => {
		const btn = e.target.closest('button[data-status]');
		if (!btn) return;

		const id = btn.getAttribute('data-id');
		const status = btn.getAttribute('data-status');

		let confirmMsg = '';
		if (status === 'SHIPPING') confirmMsg = 'Xác nhận bắt đầu giao đơn hàng này?';
		else if (status === 'DELIVERED') confirmMsg = 'Xác nhận đã giao hàng thành công?';
		else if (status === 'RETURNED_REFUNDED') confirmMsg = 'Xác nhận hoàn hàng về shop?';
		if (!confirm(confirmMsg)) return;

		const csrf = getCsrf();
		const headers = { 'Content-Type': 'application/json' };
		if (csrf) headers[csrf.header] = csrf.token;

		try {
			const res = await fetch(`/api/shipper/orders/${id}/status`, {
				method: 'PATCH',
				headers,
				body: JSON.stringify({ status })
			});
			if (!res.ok) {
				alert(res.status === 409 ? 'Không thể chuyển trạng thái này!' : 'Cập nhật thất bại!');
				return;
			}
			alert('✓ Cập nhật trạng thái thành công!');
			loadOrders();
		} catch (err) {
			alert('Lỗi kết nối. Vui lòng kiểm tra mạng!');
		}
	});
});

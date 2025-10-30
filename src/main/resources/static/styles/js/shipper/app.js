let map, marker;

function getCsrf() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    return token && header ? { header, token } : null;
}

async function initMap() {
    map = L.map('map');
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);
    marker = L.marker([0, 0]).addTo(map);
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(pos => {
            const { latitude, longitude } = pos.coords;
            map.setView([latitude, longitude], 15);
            marker.setLatLng([latitude, longitude]);
            updateLocation(latitude, longitude);
        });
        navigator.geolocation.watchPosition(pos => {
            const { latitude, longitude } = pos.coords;
            marker.setLatLng([latitude, longitude]);
            updateLocation(latitude, longitude);
        });
    }
}

async function updateLocation(lat, lng) {
    try {
        const csrf = getCsrf();
        const headers = { 'Content-Type': 'application/json' };
        if (csrf) headers[csrf.header] = csrf.token;
        await fetch('/api/shipper/location', { method: 'POST', headers, body: JSON.stringify({ lat, lng }) });
    } catch {}
}

async function loadAvailability() {
    const res = await fetch('/api/shipper/me');
    if (!res.ok) return;
    const me = await res.json();
    const tg = document.getElementById('availabilityToggle');
    const tx = document.getElementById('availabilityText');
    tg.checked = !!me.available;
    tx.textContent = me.available ? '🟢 Trực tuyến' : '⚫ Ngoại tuyến';
    tx.style.color = me.available ? '#10b981' : '#6b7280';
}

async function toggleAvailability() {
    const tg = document.getElementById('availabilityToggle');
    const csrf = getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf) headers[csrf.header] = csrf.token;
    const res = await fetch('/api/shipper/availability', { method: 'PATCH', headers, body: JSON.stringify({ available: tg.checked }) });
    if (!res.ok) tg.checked = !tg.checked;
    loadAvailability();
}

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

function renderOrderList(containerId, orders, canAccept) {
    const c = document.getElementById(containerId);
    c.innerHTML = '';
    
    if (!orders || orders.length === 0) {
        c.innerHTML = `
            <div class="empty-state">
                <i class="fa fa-inbox"></i>
                <p>Không có đơn hàng nào</p>
            </div>
        `;
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
            actions.push(`<button class="btn-accept" data-accept="${o.id}">
                <i class="fa fa-check-circle"></i> Nhận đơn ngay
            </button>`);
        } else {
            const st = (o.status || '').toUpperCase();
            if (st === 'CONFIRMED') {
                actions.push(`<button class="btn-start" data-status="SHIPPING" data-id="${o.id}">
                    <i class="fa fa-shipping-fast"></i> Bắt đầu giao
                </button>`);
            }
            if (st === 'SHIPPING') {
                actions.push(`<button class="btn-complete" data-status="DELIVERED" data-id="${o.id}">
                    <i class="fa fa-check-double"></i> Giao thành công
                </button>`);
                actions.push(`<button class="btn-return" data-status="RETURNED_REFUNDED" data-id="${o.id}">
                    <i class="fa fa-undo"></i> Hoàn hàng
                </button>`);
            }
        }
        
        div.innerHTML = `
            <div class="order-info">
                <div class="order-info-row">
                    <span class="label">Mã đơn:</span>
                    <span class="value">${o.id}</span>
                </div>
                <div class="order-info-row">
                    <span class="label">Cửa hàng:</span>
                    <span class="value">${o.storeName || 'N/A'}</span>
                </div>
                <div class="order-info-row">
                    <span class="label">Khách hàng:</span>
                    <span class="value">${o.customerName || 'N/A'}</span>
                </div>
                <div class="order-info-row">
                    <span class="label">Trạng thái:</span>
                    <span class="status-badge ${statusClass}">${statusDisplay}</span>
                </div>
            </div>
            <div class="action-buttons">${actions.join('')}</div>
        `;
        c.appendChild(div);
    });
}

async function acceptOrder(id) {
    if (!confirm('Bạn có chắc muốn nhận đơn hàng này?')) return;
    
    const csrf = getCsrf();
    const headers = {};
    if (csrf) headers[csrf.header] = csrf.token;
    
    try {
        const res = await fetch(`/api/shipper/orders/${id}/accept`, { method: 'PATCH', headers });
        if (!res.ok) {
            if (res.status === 409) {
                alert('Đơn hàng đã được shipper khác nhận!');
            } else {
                alert('Nhận đơn thất bại. Vui lòng thử lại!');
            }
            return;
        }
        alert('✓ Đã nhận đơn thành công!');
        loadOrders();
    } catch (err) {
        alert('Lỗi kết nối. Vui lòng kiểm tra mạng!');
    }
}

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
        if (btn) {
            const id = btn.getAttribute('data-id');
            const status = btn.getAttribute('data-status');
            
            let confirmMsg = '';
            if (status === 'SHIPPING') {
                confirmMsg = 'Xác nhận bắt đầu giao đơn hàng này?';
            } else if (status === 'DELIVERED') {
                confirmMsg = 'Xác nhận đã giao hàng thành công?';
            } else if (status === 'RETURNED_REFUNDED') {
                confirmMsg = 'Xác nhận hoàn hàng về shop?';
            }
            
            if (!confirm(confirmMsg)) return;
            
            const csrf = getCsrf();
            const headers = { 'Content-Type':'application/json' };
            if (csrf) headers[csrf.header] = csrf.token;
            
            try {
                const res = await fetch(`/api/shipper/orders/${id}/status`, { method: 'PATCH', headers, body: JSON.stringify({ status }) });
                if (!res.ok) {
                    if (res.status === 409) {
                        alert('Không thể chuyển trạng thái này!');
                    } else {
                        alert('Cập nhật trạng thái thất bại. Vui lòng thử lại!');
                    }
                    return;
                }
                
                let successMsg = '';
                if (status === 'SHIPPING') {
                    successMsg = '✓ Đã bắt đầu giao hàng!';
                } else if (status === 'DELIVERED') {
                    successMsg = '✓ Đã xác nhận giao hàng thành công!';
                } else if (status === 'RETURNED_REFUNDED') {
                    successMsg = '✓ Đã xác nhận hoàn hàng!';
                }
                alert(successMsg);
                loadOrders();
            } catch (err) {
                alert('Lỗi kết nối. Vui lòng kiểm tra mạng!');
            }
        }
    });
});



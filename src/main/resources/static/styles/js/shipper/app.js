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
    tx.textContent = me.available ? 'Trực tuyến' : 'Ngoại tuyến';
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
    if (!orders || orders.length === 0) { c.textContent = 'Không có dữ liệu'; return; }
    orders.forEach(o => {
        const div = document.createElement('div');
        div.className = 'mobile-card';
        div.style.margin = '6px 0';
        const actions = [];
        if (canAccept) {
            actions.push(`<button class="btn" data-accept="${o.id}">Nhận đơn</button>`);
        } else {
            const st = (o.status || '').toUpperCase();
            if (st === 'CONFIRMED') {
                actions.push(`<button class="btn" data-status="SHIPPING" data-id="${o.id}">Bắt đầu giao</button>`);
            }
            if (st === 'SHIPPING') {
                actions.push(`<button class="btn" data-status="DELIVERED" data-id="${o.id}">Đã giao</button>`);
                actions.push(`<button class="btn secondary" data-status="RETURNED_REFUNDED" data-id="${o.id}">Hoàn hàng</button>`);
            }
        }
        div.innerHTML = `
            <div><strong>Đơn:</strong> ${o.id}</div>
            <div><strong>Shop:</strong> ${o.storeName || ''}</div>
            <div><strong>Khách:</strong> ${o.customerName || ''}</div>
            <div><strong>Trạng thái:</strong> ${o.status}</div>
            <div class="row" style="gap:8px;margin-top:6px;">${actions.join(' ')}</div>
        `;
        c.appendChild(div);
    });
}

async function acceptOrder(id) {
    const csrf = getCsrf();
    const headers = {};
    if (csrf) headers[csrf.header] = csrf.token;
    const res = await fetch(`/api/shipper/orders/${id}/accept`, { method: 'PATCH', headers });
    if (!res.ok) { alert('Nhận đơn thất bại'); return; }
    loadOrders();
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
            const csrf = getCsrf();
            const headers = { 'Content-Type':'application/json' };
            if (csrf) headers[csrf.header] = csrf.token;
            const res = await fetch(`/api/shipper/orders/${id}/status`, { method: 'PATCH', headers, body: JSON.stringify({ status }) });
            if (!res.ok) { alert('Cập nhật trạng thái thất bại'); return; }
            loadOrders();
        }
    });
});



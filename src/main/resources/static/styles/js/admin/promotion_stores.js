let spCurrentPage = 0;
const spPageSize = 10;

const spTableBody = document.getElementById("storePromotionTableBody");
const spPagination = document.getElementById("storePromotionPagination");

async function loadStorePromotions(page = 0) {
    spCurrentPage = page;
    const keyword = document.querySelector('#storePromotionFilter input[name="keyword"]').value || '';
    const params = new URLSearchParams({ page, size: spPageSize });
    if (keyword) params.append('keyword', keyword);
    const res = await fetch(`/api/admin/promotion/stores?${params.toString()}`);
    if (!res.ok) {
        if (spTableBody) spTableBody.innerHTML = `<tr><td colspan="8" style="text-align:center;">LỖI TẢI DỮ LIỆU</td></tr>`;
        return;
    }
    const data = await res.json();
    renderStorePromotionTable(data.content);
    renderStorePagination(data);
}

function renderStorePromotionTable(items) {
    if (!spTableBody) return;
    spTableBody.innerHTML = '';
    if (!items || items.length === 0) {
        spTableBody.innerHTML = `<tr><td colspan="8" style="text-align:center;">KHÔNG CÓ DỮ LIỆU</td></tr>`;
        return;
    }
    items.forEach((p, idx) => {
        const tr = document.createElement('tr');
        const stt = idx + 1 + (spCurrentPage * spPageSize);
        const statusText = p.status || '';
        const statusUpper = statusText.toUpperCase();
        const isBanned = statusUpper === 'BANNED';
        const isActive = statusUpper === 'ACTIVE';
        
        // Xác định status class và text
        let statusClass = 'status-badge ';
        let displayStatus = statusText;
        if (isBanned) {
            statusClass += 'status-banned';
            displayStatus = 'Đã cấm';
        } else if (isActive) {
            statusClass += 'status-active';
            displayStatus = 'Đang hoạt động';
        } else if (statusUpper === 'EXPIRED') {
            statusClass += 'status-expired';
            displayStatus = 'Đã hết hạn';
        } else if (statusUpper === 'SCHEDULED') {
            statusClass += 'status-upcoming';
            displayStatus = 'Sắp bắt đầu';
        } else {
            statusClass += 'status-inactive';
            displayStatus = 'Chưa kích hoạt';
        }
        
        // Render action buttons
        let actionButtons = `<div class="action-buttons">
            <label class="switch" title="Kích hoạt/Vô hiệu hóa">
                <input type="checkbox" class="store-promo-toggle" data-id="${p.id}" ${isActive ? 'checked' : ''} ${isBanned ? 'disabled' : ''} />
                <span class="slider round"></span>
            </label>`;
        
        if (isBanned) {
            actionButtons += `
                <button class="btn-unban" data-id="${p.id}" title="Bỏ cấm khuyến mãi">
                    <i class="fas fa-check-circle"></i> Bỏ cấm
                </button>`;
        } else {
            actionButtons += `
                <button class="btn-ban" data-id="${p.id}" title="Cấm khuyến mãi">
                    <i class="fas fa-ban"></i> Cấm
                </button>`;
        }
        
        actionButtons += `</div>`;
        
        tr.innerHTML = `
            <td>${stt}</td>
            <td>${escapeHTML(p.storeName || '')}</td>
            <td>${escapeHTML(p.name || '')}</td>
            <td>${escapeHTML(p.type || '')}</td>
            <td><span class="${statusClass}">${displayStatus}</span></td>
            <td>${formatDateTime(p.startDate)}</td>
            <td>${formatDateTime(p.endDate)}</td>
            <td>${actionButtons}</td>
        `;
        spTableBody.appendChild(tr);
    });
}

function renderStorePagination(data) {
    if (!spPagination) return;
    spPagination.innerHTML = '';
    const totalPages = Math.max(1, data.totalPages);
    const current = data.number;
    for (let i = 0; i < totalPages; i++) {
        const a = document.createElement('a');
        a.href = '#';
        a.textContent = i + 1;
        if (i === current) a.classList.add('active');
        a.addEventListener('click', e => { e.preventDefault(); loadStorePromotions(i); });
        spPagination.appendChild(a);
    }
}

async function ban(id) {
    if (!confirm('Bạn có chắc muốn CẤM khuyến mãi này của cửa hàng?')) return;
    
    try {
        const res = await fetch(`/api/admin/promotion/${id}/ban`, { method: 'PATCH' });
        if (res.ok) {
            alert('✅ Đã cấm khuyến mãi thành công!');
            loadStorePromotions(spCurrentPage);
        } else {
            const error = await res.text();
            alert('❌ Lỗi: ' + error);
        }
    } catch (error) {
        console.error('Error banning store promotion:', error);
        alert('❌ Lỗi kết nối');
    }
}

async function unban(id) {
    if (!confirm('Bạn có chắc muốn BỎ CẤM khuyến mãi này của cửa hàng?')) return;
    
    try {
        const res = await fetch(`/api/admin/promotion/${id}/unban`, { method: 'PATCH' });
        if (res.ok) {
            alert('✅ Đã bỏ cấm khuyến mãi thành công!');
            loadStorePromotions(spCurrentPage);
        } else {
            const error = await res.text();
            alert('❌ Lỗi: ' + error);
        }
    } catch (error) {
        console.error('Error unbanning store promotion:', error);
        alert('❌ Lỗi kết nối');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadStorePromotions(0);
    document.getElementById('storePromotionFilter')?.addEventListener('submit', e => {
        e.preventDefault();
        loadStorePromotions(0);
    });
    spTableBody?.addEventListener('click', e => {
        const banBtn = e.target.closest('.btn-ban');
        const unbanBtn = e.target.closest('.btn-unban');
        if (banBtn) { e.preventDefault(); ban(banBtn.dataset.id); }
        if (unbanBtn) { e.preventDefault(); unban(unbanBtn.dataset.id); }
    });

    spTableBody?.addEventListener('change', async e => {
        const toggle = e.target.closest('.store-promo-toggle');
        if (toggle) {
            const id = toggle.dataset.id;
            const endpoint = toggle.checked ? `/api/admin/promotion/${id}/activate` : `/api/admin/promotion/${id}/deactivate`;
            const res = await fetch(endpoint, { method: 'PATCH' });
            if (!res.ok) alert(await res.text());
            loadStorePromotions(spCurrentPage);
        }
    });
});

function formatDateTime(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleString('vi-VN');
}

function escapeHTML(str) {
    if (!str) return '';
    return String(str).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[m]));
}



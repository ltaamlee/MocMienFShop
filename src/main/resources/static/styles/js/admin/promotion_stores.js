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
        const isBanned = statusText.toUpperCase() === 'BANNED';
        const isActive = statusText.toUpperCase() === 'ACTIVE';
        tr.innerHTML = `
            <td>${stt}</td>
            <td>${escapeHTML(p.storeName || '')}</td>
            <td>${escapeHTML(p.name || '')}</td>
            <td>${escapeHTML(p.type || '')}</td>
            <td>${escapeHTML(statusText)}</td>
            <td>${formatDateTime(p.startDate)}</td>
            <td>${formatDateTime(p.endDate)}</td>
            <td>
                <label class="switch" title="Kích hoạt/Nhấc kích hoạt">
                    <input type="checkbox" class="store-promo-toggle" data-id="${p.id}" ${isActive ? 'checked' : ''} ${isBanned ? 'disabled' : ''} />
                    <span class="slider round"></span>
                </label>
                <button class="btn-ban" data-id="${p.id}" ${isBanned ? 'disabled' : ''}>Cấm</button>
                <button class="btn-unban" data-id="${p.id}" ${isBanned ? '' : 'disabled'}>Bỏ cấm</button>
            </td>
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
    await fetch(`/api/admin/promotion/${id}/ban`, { method: 'PATCH' });
    loadStorePromotions(spCurrentPage);
}

async function unban(id) {
    await fetch(`/api/admin/promotion/${id}/unban`, { method: 'PATCH' });
    loadStorePromotions(spCurrentPage);
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



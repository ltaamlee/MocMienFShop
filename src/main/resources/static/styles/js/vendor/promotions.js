let currentPage = 0;
const pageSize = 10;
let pendingToggle = null;

// ============ Utils ============
function fmtDate(dt) { return dt ? new Date(dt).toLocaleString('vi-VN') : '—'; }
function typeDisplay(t) {
  const map = { PERCENT: 'Giảm %', AMOUNT: 'Giảm tiền', FREESHIP: 'Freeship', GIFT: 'Quà tặng' };
  return map[t] || t || '—';
}
function statusDisplay(s) {
  const map = { INACTIVE: 'Chưa kích hoạt', ACTIVE: 'Đang hoạt động', EXPIRED: 'Đã hết hạn', SCHEDULED: 'Hẹn giờ' };
  return map[s] || s || '—';
}
function isExpired(endDate) { return endDate ? (new Date(endDate) < new Date()) : false; }

function openModal(id) { const m = document.getElementById(id); if (!m) return; m.classList.add('show'); document.body.style.overflow = 'hidden'; }
function closeModal(id) { const m = document.getElementById(id); if (!m) return; m.classList.remove('show'); document.body.style.overflow = ''; }

document.addEventListener('click', e => {
  if (e.target.classList.contains('modal')) closeModal(e.target.id);
});
document.addEventListener('keydown', e => {
  if (e.key === 'Escape') document.querySelectorAll('.modal.show').forEach(m => closeModal(m.id));
});

// --- Helper an toàn: chỉ gán text nếu phần tử tồn tại ---
function setTextSafe(id, value) {
  const el = document.getElementById(id);
  if (el) el.textContent = value;
}

// ============ Stats ============
async function loadPromotionStats() {
  const r = await fetch('/api/vendor/promotions/stats');
  if (!r.ok) return;
  const s = await r.json();

  // Dùng setTextSafe để không lỗi khi card bị xoá/ẩn trong HTML
  setTextSafe('totalPromotionsStat',    s.totalPromotions || 0);
  setTextSafe('inactivePromotionsStat', s.inactivePromotions || 0);
  setTextSafe('activePromotionsStat',   s.activePromotions || 0);
  setTextSafe('expiringPromotionsStat', s.expiringSoonPromotions || 0);
  setTextSafe('expiredPromotionsStat',  s.expiredPromotions || 0);
}

// ============ Product options (dropdown) ============
let PRODUCT_OPTIONS = []; // [{id, productName}, ...]

async function loadProductOptions() {
  const baseSel = document.querySelector('#relatedProductsTable select');
  if (!baseSel) return;

  // show loading
  baseSel.innerHTML = `<option value="">Đang tải...</option>`;

  const r = await fetch('/api/vendor/promotions/options/products');
  if (!r.ok) {
    baseSel.innerHTML = `<option value="">Không tải được danh sách</option>`;
    return;
  }
  PRODUCT_OPTIONS = await r.json();

  const optsHtml = ['<option value="">-- Chọn sản phẩm --</option>']
    .concat(PRODUCT_OPTIONS.map(o => `<option value="${o.id}">${escapeHtml(o.productName)} (#${o.id})</option>`))
    .join('');

  // fill all selects currently in DOM (add & edit)
  document.querySelectorAll('#relatedProductsTable select, #editRelatedProductsTable select')
    .forEach(sel => sel.innerHTML = optsHtml);
}

function escapeHtml(s) {
  return (s ?? '').toString()
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function buildSelectHtml(selectedId = '') {
  const options = ['<option value="">-- Chọn sản phẩm --</option>']
    .concat(PRODUCT_OPTIONS.map(o =>
      `<option value="${o.id}" ${String(selectedId) === String(o.id) ? 'selected' : ''}>
        ${escapeHtml(o.productName)} (#${o.id})
      </option>`));
  return `<select class="w-full">${options.join('')}</select>`;
}

// ============ List ============
async function loadPromotions(page = 0) {
  currentPage = page;
  const form = document.getElementById('searchFilterForm');
  const keyword = form?.elements['keyword']?.value || '';
  const status = form?.elements['status']?.value || '';
  const type = form?.elements['type']?.value || '';

  let url = `/api/vendor/promotions?page=${page}&size=${pageSize}`;
  if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
  if (status) url += `&status=${status}`;
  if (type) url += `&type=${type}`;

  const tb = document.getElementById('promotionTableBody');
  tb.innerHTML = `<tr><td colspan="8" class="text-center">Đang tải...</td></tr>`;

  const r = await fetch(url);
  if (!r.ok) { tb.innerHTML = `<tr><td colspan="8" class="text-center text-danger">Lỗi tải dữ liệu</td></tr>`; return; }
  const data = await r.json();

  renderPromotionTable(data.content || []);
  renderPromotionPagination(data.number || 0, data.totalPages || 1);
}

function renderPromotionTable(items) {
  const tb = document.getElementById('promotionTableBody');
  tb.innerHTML = '';

  if (!items.length) {
    tb.innerHTML = `<tr><td colspan="8" class="text-center">Không có khuyến mãi</td></tr>`;
    return;
  }

  items.forEach(p => {
    const expired = isExpired(p.endDate) || p.status === 'EXPIRED';
    const tr = document.createElement('tr');

    tr.innerHTML = `
      <td>${p.id}</td>
      <td>${p.name || '—'}</td>
      <td>${typeDisplay(p.type)}</td>
      <td>${p.value != null ? p.value : '—'}</td>
      <td>${fmtDate(p.startDate)}</td>
      <td>${fmtDate(p.endDate)}</td>
      <td class="toggle-cell">
        <label class="switch">
          <input type="checkbox" class="promo-toggle"
                 ${p.status === 'ACTIVE' && !expired ? 'checked' : ''}
                 ${expired ? 'disabled' : ''}
                 data-id="${p.id}" data-name="${escapeHtml(p.name || '')}" data-expired="${expired}">
          <span class="slider round"></span>
        </label>
      </td>
      <td>
        <div class="action-buttons">
          <button class="btn-view" onclick="openPromotionDetail(${p.id})"><i class="fas fa-eye"></i></button>
          <button class="btn-edit" onclick="openPromotionEdit(${p.id})"><i class="fas fa-edit"></i></button>
          <button class="btn-delete" onclick="deletePromotion(${p.id})"><i class="fas fa-trash"></i></button>
        </div>
      </td>
    `;

    const checkbox = tr.querySelector('.promo-toggle');
    checkbox?.addEventListener('click', (e) => {
      e.preventDefault();
      if (checkbox.dataset.expired === 'true') {
        alert('❌ Khuyến mãi đã hết hạn');
        return;
      }
      pendingToggle = checkbox;
      const willActivate = !checkbox.checked;
      document.getElementById('activatePromotionActionText').innerText = willActivate ? 'vô hiệu hóa' : 'kích hoạt';
      document.getElementById('activatePromotionName').innerText = checkbox.dataset.name || '';
      openModal('activatePromotionModal');
    });

    tb.appendChild(tr);
  });
}

function renderPromotionPagination(cur, total) {
  const pg = document.getElementById('promotionPagination');
  pg.innerHTML = '';
  if (total <= 1) return;

  const mk = (i, txt, disabled = false, active = false) => {
    if (disabled) return `<span class="disabled">${txt}</span>`;
    const cls = active ? 'active' : '';
    return `<a href="#" data-page="${i}" class="${cls}">${txt}</a>`;
  };

  let html = '';
  html += mk(cur - 1, `<i class="fas fa-chevron-left"></i> Trước`, cur === 0);
  for (let i = 0; i < total; i++) html += mk(i, (i + 1), false, i === cur);
  html += mk(cur + 1, `Sau <i class="fas fa-chevron-right"></i>`, cur === total - 1);
  pg.innerHTML = html;

  pg.querySelectorAll('a[data-page]').forEach(a => {
    a.addEventListener('click', e => { e.preventDefault(); loadPromotions(parseInt(a.dataset.page)); });
  });
}

// ============ Toggle status confirm ============
async function confirmConfirmation() {
  if (!pendingToggle) return;
  const id = pendingToggle.dataset.id;
  const newStatus = pendingToggle.checked ? 'INACTIVE' : 'ACTIVE';

  const r = await fetch(`/api/vendor/promotions/${id}/status`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status: newStatus })
  });

  closeModal('activatePromotionModal');
  pendingToggle = null;

  if (!r.ok) { alert('Cập nhật trạng thái thất bại'); return; }
  await loadPromotionStats();
  await loadPromotions(currentPage);
}
function cancelConfirmation() { pendingToggle = null; closeModal('activatePromotionModal'); }

// ============ Detail ============
async function openPromotionDetail(id) {
  const r = await fetch(`/api/vendor/promotions/${id}`);
  if (!r.ok) { alert('Không tải được chi tiết'); return; }
  const d = await r.json();

  document.getElementById('viewName').textContent = d.name || '—';
  document.getElementById('viewType').textContent = typeDisplay(d.type);
  document.getElementById('viewValue').textContent = (d.value != null ? d.value : '—');
  document.getElementById('viewDescription').textContent = d.description || '—';
  document.getElementById('viewStartDate').textContent = fmtDate(d.startDate);
  document.getElementById('viewEndDate').textContent = fmtDate(d.endDate);
  document.getElementById('viewStatus').textContent = statusDisplay(d.status);

  const body = document.getElementById('viewProductsBody');
  body.innerHTML = '';
  if (d.productIds && d.productIds.length) {
    d.productIds.forEach((pid, i) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${i + 1}</td><td>${pid}</td><td>${escapeHtml(d.productNames?.[i] || '—')}</td>`;
      body.appendChild(tr);
    });
  } else {
    body.innerHTML = `<tr><td colspan="3" class="text-center">Áp dụng toàn bộ sản phẩm</td></tr>`;
  }

  openModal('viewPromotionModal');
}

// ============ Add / Edit form helpers ============
/// Bật/Tắt và cấu hình input value theo type
function setupValueField(typeSelId, valueInputId) {
  const typeEl = document.getElementById(typeSelId);
  const valEl = document.getElementById(valueInputId);
  if (!typeEl || !valEl) return;

  const apply = () => {
    const t = typeEl.value;
    valEl.removeAttribute('disabled');
    valEl.value = valEl.value; // keep
    valEl.placeholder = '';
    valEl.min = ''; valEl.max = ''; valEl.step = 'any';

    if (t === 'PERCENT') {
      valEl.type = 'number';
      valEl.min = '0'; valEl.max = '100'; valEl.step = '0.01';
      valEl.placeholder = '0 – 100 (%)';
    } else if (t === 'AMOUNT') {
      valEl.type = 'number';
      valEl.min = '0.01'; valEl.step = '0.01';
      valEl.placeholder = 'Số tiền giảm (> 0)';
    } else { // FREESHIP | GIFT
      valEl.value = '';
      valEl.type = 'text';
      valEl.placeholder = 'Không cần nhập';
      valEl.setAttribute('disabled', 'disabled');
    }
  };

  typeEl.addEventListener('change', apply);
  apply();
}

/// Chuẩn hóa & kiểm tra payload value theo type
function validateValueByType(type, rawValue) {
  if (type === 'FREESHIP' || type === 'GIFT') return { ok: true, value: null };

  const v = Number(rawValue);
  if (isNaN(v)) return { ok: false, msg: 'Giá trị không hợp lệ' };

  if (type === 'PERCENT') {
    if (v < 0 || v > 100) return { ok: false, msg: 'Phần trăm phải trong khoảng 0–100' };
    // giới hạn 2 chữ số thập phân
    const rounded = Math.round(v * 100) / 100;
    return { ok: true, value: rounded };
  }
  if (type === 'AMOUNT') {
    if (v <= 0) return { ok: false, msg: 'Số tiền phải > 0' };
    return { ok: true, value: Math.round(v * 100) / 100 };
  }
  return { ok: false, msg: 'Loại khuyến mãi không hợp lệ' };
}

function validateDateRange(startStr, endStr) {
  if (!startStr || !endStr) return { ok: true };
  const s = new Date(startStr), e = new Date(endStr);
  if (isNaN(s.getTime()) || isNaN(e.getTime())) return { ok: true };
  if (e < s) return { ok: false, msg: 'Thời gian kết thúc phải sau thời gian bắt đầu' };
  return { ok: true };
}

// ============ Edit ============
async function openPromotionEdit(id) {
  // đảm bảo options đã có trước khi dựng bảng
  if (PRODUCT_OPTIONS.length === 0) await loadProductOptions();

  const r = await fetch(`/api/vendor/promotions/${id}`);
  if (!r.ok) { alert('Không tải được dữ liệu'); return; }
  const d = await r.json();

  const setV = (sel, val) => { const el = document.getElementById(sel); if (el) el.value = val ?? ''; };
  setV('editId', d.id);
  setV('editName', d.name);
  setV('editType', d.type);
  setV('editValue', d.value);
  setV('editDescription', d.description);
  setV('editStartDate', d.startDate ? d.startDate.slice(0, 16) : '');
  setV('editEndDate', d.endDate ? d.endDate.slice(0, 16) : '');

  const tbody = document.querySelector('#editRelatedProductsTable tbody');
  tbody.innerHTML = '';
  if (d.productIds && d.productIds.length) {
    d.productIds.forEach(pid => tbody.appendChild(createProductRow(pid)));
  } else {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td colspan="2" class="text-center">Áp dụng toàn shop</td>`;
    tbody.appendChild(tr);
  }

  // cấu hình input value theo type (edit)
  setupValueField('editType', 'editValue');

  openModal('editPromotionModal');
}

function createProductRow(selectedId = "") {
  const row = document.createElement('tr'); row.classList.add('product-row');

  const td1 = document.createElement('td');
  td1.innerHTML = buildSelectHtml(selectedId);

  const td2 = document.createElement('td');
  const btn = document.createElement('button');
  btn.type = 'button'; btn.className = 'btn-delete'; btn.textContent = 'Xóa';
  btn.onclick = () => row.remove();
  td2.appendChild(btn);

  row.appendChild(td1); row.appendChild(td2);
  return row;
}
function addRelatedProductRow() {
  const tbody = document.querySelector('#relatedProductsTable tbody');
  tbody.appendChild(createProductRow());
}
function addEditProductRow() {
  const tbody = document.querySelector('#editRelatedProductsTable tbody');
  tbody.appendChild(createProductRow());
}

// ============ Create ============
async function createPromotion() {
  const type = document.getElementById('type').value;
  const rawVal = document.getElementById('value').value;

  const valueChk = validateValueByType(type, rawVal);
  if (!valueChk.ok) { alert('❌ ' + valueChk.msg); return; }

  const start = document.getElementById('startDate').value;
  const end = document.getElementById('endDate').value;
  const dtChk = validateDateRange(start, end);
  if (!dtChk.ok) { alert('❌ ' + dtChk.msg); return; }

  const payload = {
    name: document.getElementById('name').value.trim(),
    type,
    value: valueChk.value, // đã chuẩn hóa theo type
    description: document.getElementById('description').value.trim(),
    startDate: start,
    endDate: end,
    productIds: Array.from(document.querySelectorAll('#relatedProductsTable tbody select'))
      .map(s => parseInt(s.value)).filter(v => !isNaN(v))
  };

  const r = await fetch('/api/vendor/promotions/add', {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!r.ok) { alert('Thêm thất bại'); return; }
  alert('✅ Thêm thành công');
  closeModal('addPromotionModal'); document.getElementById('promotionForm').reset();
  // reset value field state về mặc định
  setupValueField('type', 'value');
  await loadPromotionStats(); await loadPromotions(currentPage);
}

// ============ Update ============
document.getElementById('editPromotionForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('editId').value;

  const type = document.getElementById('editType').value;
  const rawVal = document.getElementById('editValue').value;
  const valueChk = validateValueByType(type, rawVal);
  if (!valueChk.ok) { alert('❌ ' + valueChk.msg); return; }

  const start = document.getElementById('editStartDate').value;
  const end = document.getElementById('editEndDate').value;
  const dtChk = validateDateRange(start, end);
  if (!dtChk.ok) { alert('❌ ' + dtChk.msg); return; }

  const payload = {
    name: document.getElementById('editName').value.trim(),
    type,
    value: valueChk.value,
    description: document.getElementById('editDescription').value.trim(),
    startDate: start,
    endDate: end,
    productIds: Array.from(document.querySelectorAll('#editRelatedProductsTable tbody select'))
      .map(s => parseInt(s.value)).filter(v => !isNaN(v))
  };

  const r = await fetch(`/api/vendor/promotions/edit/${id}`, {
    method: 'PUT', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!r.ok) { alert('Cập nhật thất bại'); return; }
  alert('✅ Cập nhật thành công');
  closeModal('editPromotionModal');
  await loadPromotionStats(); await loadPromotions(currentPage);
});

// ============ Delete ============
async function deletePromotion(id) {
  if (!confirm('Xóa khuyến mãi này?')) return;
  const r = await fetch(`/api/vendor/promotions/${id}`, { method: 'DELETE' });
  if (!r.ok) { alert('Xóa thất bại'); return; }
  await loadPromotionStats(); await loadPromotions(currentPage);
}

// ============ Init ============
document.addEventListener('DOMContentLoaded', () => {
  // cấu hình value field state (add)
  setupValueField('type', 'value');

  loadProductOptions().then(() => {
    // sau khi options sẵn sàng mới load list để tránh nhấp nút thêm hàng bị option trống
    loadPromotionStats();
    loadPromotions(0);
  });

  document.getElementById('searchFilterForm')?.addEventListener('submit', e => {
    e.preventDefault(); loadPromotions(0);
  });
  document.getElementById('statusFilter')?.addEventListener('change', () => loadPromotions(0));
  document.getElementById('typeFilter')?.addEventListener('change', () => loadPromotions(0));

  document.getElementById('promotionForm')?.addEventListener('submit', e => {
    e.preventDefault(); createPromotion();
  });
});

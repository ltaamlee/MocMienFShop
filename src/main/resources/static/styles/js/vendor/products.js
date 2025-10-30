const apiBase = '/api/vendor/products';
let currentPage = 0;
const pageSize = 10;

const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
function authHeaders() {
	const h = { 'Content-Type': 'application/json' };
	if (csrfToken && csrfHeader) h[csrfHeader] = csrfToken;
	return h;
}

document.addEventListener('DOMContentLoaded', () => {
	bindSearch();
	//loadCategories();
	loadProducts();
});

function bindSearch() {
	const form = document.getElementById('searchFilterForm');
	form.addEventListener('submit', (e) => {
		e.preventDefault();
		currentPage = 0;
		loadProducts();
	});
}

function resetFilters() {
	const form = document.getElementById('searchFilterForm');
	form.keyword.value = '';
	form.categoryId.value = '';
	form.status.value = '';
	currentPage = 0;
	loadProducts();
}

// ======== CATEGORIES from VendorProductsController
async function loadCategories() {
	try {
		const res = await fetch(`${apiBase}/categories?active=true`);
		if (!res.ok) throw new Error('Load categories failed');
		const cats = await res.json();
		const catFilter = document.getElementById('categoryFilter');
		const catSelect = document.getElementById('categorySelect');

		if (catFilter) {
			catFilter.innerHTML = '<option value="">Tất cả</option>';
			cats.forEach(c => {
				const op = document.createElement('option');
				op.value = c.id; op.textContent = c.categoryName;
				catFilter.appendChild(op);
			});
		}
		if (catSelect) {
			catSelect.innerHTML = '<option value="">-- Chọn danh mục --</option>';
			cats.forEach(c => {
				const op = document.createElement('option');
				op.value = c.id; op.textContent = c.categoryName;
				catSelect.appendChild(op);
			});
		}
	} catch (e) { console.error(e); }
}

// ======== LIST
async function loadProducts(page = currentPage) {
	currentPage = page;
	const form = document.getElementById('searchFilterForm');
	const keyword = encodeURIComponent(form.keyword.value || '');
	const categoryId = form.categoryId.value ? `&categoryId=${form.categoryId.value}` : '';
	const status = form.status.value ? `&status=${form.status.value}` : '';
	const url = `${apiBase}?keyword=${keyword}${categoryId}${status}&page=${currentPage}&size=${pageSize}`;

	const tbody = document.getElementById('productTableBody');
	tbody.innerHTML = `<tr><td colspan="7" class="text-center">Đang tải...</td></tr>`;

	const res = await fetch(url);
	const data = await res.json();
	renderTable(data.content);
	renderPagination(data);
}

function renderTable(items) {
	const tbody = document.getElementById('productTableBody');
	if (!items || items.length === 0) {
		tbody.innerHTML = `<tr><td colspan="7" class="text-center">Không có dữ liệu</td></tr>`;
		return;
	}
	tbody.innerHTML = items.map(p => `
    <tr>
      <td>
        ${p.defaultImage ? `<img src="${p.defaultImage}" style="width:128px;height:128px;object-fit:cover;border-radius:8px">` : ''}
      </td>
      <td><strong>${p.productName}</strong></td>
      <td>${p.categoryName || ''}</td>
      <td>${fmtMoney(p.promotionalPrice || p.price)}</td>
      <td>
        <div style="display: flex; flex-direction: column; gap: 5px;">
          <div><i class="fa-solid fa-box" style="color: #3b82f6;"></i> Tồn kho: <strong>${p.stock != null ? p.stock : 0}</strong></div>
          <div><i class="fa-solid fa-shopping-bag" style="color: #059669;"></i> Đã bán: <strong>${p.soldCount != null ? p.soldCount : 0}</strong></div>
        </div>
      </td>
      <td>${renderStatusBadge(p.status)}</td>
      <td class="text-right">
        <div class="action-buttons">
          <button class="btn-view" onclick="openEditModal(${p.id})"><i class="fas fa-edit"></i> Sửa</button>
          <button class="btn-delete" onclick="confirmDelete(${p.id})"><i class="fas fa-trash"></i> Xoá</button>
        </div>
      </td>
    </tr>
  `).join('');
}

function renderStatusBadge(st) {
	if (st === 'SELLING') return `<span class="badge badge-active"><i class="fas fa-dot-circle"></i> Đang bán</span>`;
	if (st === 'STOPPED') return `<span class="badge badge-inactive"><i class="fas fa-pause-circle"></i> Ngừng bán</span>`;
	return `<span class="badge badge-blocked"><i class="fas fa-times-circle"></i> Hết hàng</span>`;
}

function renderPagination({ number, totalPages }) {
	const el = document.getElementById('productPagination');
	if (totalPages <= 1) { el.innerHTML = ''; return; }
	let html = '';
	const prevDisabled = number === 0 ? 'disabled' : '';
	const nextDisabled = number >= totalPages - 1 ? 'disabled' : '';
	html += `<button ${prevDisabled} onclick="loadProducts(${number - 1})">&laquo; Trước</button>`;
	for (let i = 0; i < totalPages; i++) {
		const active = i === number ? 'class="active"' : '';
		html += `<a href="javascript:void(0)" ${active} onclick="loadProducts(${i})">${i + 1}</a>`;
	}
	html += `<button ${nextDisabled} onclick="loadProducts(${number + 1})">Sau &raquo;</button>`;
	el.innerHTML = html;
}

function fmtMoney(v) {
	if (v == null) return '0';
	return Number(v).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
}

// ======== Modal & Upload
function openAddModal() {
	document.getElementById('modalTitle').textContent = 'Thêm sản phẩm';
	const f = document.getElementById('productForm');
	f.reset();
	f.id.value = '';

	const preview = document.getElementById('imagePreview');
	const input = document.getElementById('imageFiles');
	if (input) input.value = '';
	preview.innerHTML = '';
	document.querySelector('#productModal').classList.add('show');
}

function closeProductModal() { document.querySelector('#productModal').classList.remove('show'); }

document.addEventListener('change', e => {
	if (e.target && e.target.id === 'imageFiles') {
		const preview = document.getElementById('imagePreview');
		preview.innerHTML = '';
		const files = e.target.files || [];
		Array.from(files).forEach(file => {
			const url = URL.createObjectURL(file);
			const img = document.createElement('img');
			img.src = url;
			img.style.width = '72px';
			img.style.height = '72px';
			img.style.objectFit = 'cover';
			img.style.borderRadius = '8px';
			preview.appendChild(img);
		});
	}
});

async function openEditModal(id) {
	const res = await fetch(`${apiBase}/${id}`);
	const p = await res.json();

	document.getElementById('modalTitle').textContent = 'Sửa sản phẩm';
	const f = document.getElementById('productForm');
	f.id.value = p.id;
	f.productName.value = p.productName;
	document.getElementById('categorySelect').value = p.categoryId || '';
	f.size.value = p.size;
	f.price.value = p.price;
	f.stock.value = p.stock;
	document.getElementById('statusSelect').value = p.status || 'SELLING';
	f.isActive.checked = !!p.isActive;

	const input = document.getElementById('imageFiles');
	const preview = document.getElementById('imagePreview');
	if (input) input.value = '';
	preview.innerHTML = '';
	(p.imageUrls || []).forEach(u => {
		const img = document.createElement('img');
		img.src = u;
		img.style.width = '72px'; img.style.height = '72px';
		img.style.objectFit = 'cover'; img.style.borderRadius = '8px';
		preview.appendChild(img);
	});

	document.querySelector('#productModal').classList.add('show');
}

async function uploadOne(file) {
	const form = new FormData();
	form.append('file', file);
	form.append('folder', 'products');
	const res = await fetch('/api/upload', { method: 'POST', body: form });
	const data = await res.json();
	if (data.error) throw new Error(data.error);
	return data.url;
}

async function uploadAll(files) {
	const list = Array.from(files || []);
	const urls = [];
	for (const f of list) urls.push(await uploadOne(f));
	return urls;
}

async function submitProduct() {
	const f = document.getElementById('productForm');
	const files = document.getElementById('imageFiles').files;

	// Upload ảnh nếu có
	let imageUrls = [];
	if (files && files.length) {
		try {
			imageUrls = await uploadAll(files);
		} catch (e) {
			alert('Upload ảnh thất bại: ' + e.message);
			return;
		}
	} else if (f.id.value) {
		// đang sửa mà không chọn ảnh mới -> giữ ảnh cũ
		imageUrls = null;
	}

	const payload = {
		productName: f.productName.value.trim(),
		categoryId: Number(document.getElementById('categorySelect').value),
		price: Number(f.price.value),
		size: f.size.value.trim(),
		stock: Number(f.stock.value),
		status: document.getElementById('statusSelect').value,
		isActive: f.isActive.checked
	};
	if (imageUrls !== null) payload.imageUrls = imageUrls;

	const isEdit = !!f.id.value;
	const method = isEdit ? 'PUT' : 'POST';
	const url = isEdit ? `${apiBase}/${f.id.value}` : apiBase;

	const res = await fetch(url, { method, headers: authHeaders(), body: JSON.stringify(payload) });
	const text = await res.text();
	if (!res.ok) {
		console.error('Save failed', res.status, text);
		alert(`Lưu thất bại (${res.status})\n${text}`);
		return;
	}
	closeProductModal();
	loadProducts();
}

async function confirmDelete(id) {
	if (!confirm('Xoá sản phẩm này?')) return;
	const res = await fetch(`${apiBase}/${id}`, { method: 'DELETE', headers: authHeaders() });
	if (res.ok || res.status === 204) loadProducts();
	else alert('Xoá không thành công');
}

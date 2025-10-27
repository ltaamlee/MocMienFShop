(() => {
	let materialIndex = 1;
	let materialsData = {};
	let STATUS_CACHE = [];

	function escapeHtml(str) {
		if (str == null) return '';
		return String(str)
			.replace(/&/g, '&amp;')
			.replace(/</g, '&lt;')
			.replace(/>/g, '&gt;')
			.replace(/"/g, '&quot;')
			.replace(/'/g, '&#39;');
	}

	function initMaterialsDataFromWindow() {
		try {
			const arr = window.__MATERIALS__ || [];
			materialsData = {};
			arr.forEach(m => { materialsData[m.maNL] = m; });
		} catch (e) { materialsData = {}; }
	}

	async function loadStatuses() {
		if (STATUS_CACHE.length) return STATUS_CACHE;
		const r = await fetch('/api/vendor/products/statuses');
		STATUS_CACHE = await r.json(); // [{value, text}]
		return STATUS_CACHE;
	}

	function populateStatusSelect(selectEl, statuses, defaultValue = null) {
		selectEl.innerHTML = '';
		statuses.forEach(s => {
			const opt = document.createElement('option');
			opt.value = s.value;
			opt.textContent = s.text;
			if (defaultValue !== null && String(defaultValue) === String(s.value)) opt.selected = true;
			selectEl.appendChild(opt);
		});
	}

	// --- Mở & Đóng modal ---
	window.openModal = function(modalId) {
		const modal = document.getElementById(modalId);
		if (!modal) return;
		modal.classList.add('show');
		document.body.classList.add('modal-open');
	};

	window.closeModal = function(modalId) {
		const modal = document.getElementById(modalId);
		if (!modal) return;
		modal.classList.remove('show');
		document.body.classList.remove('modal-open');
		modal.dataset.editingId = '';
		// reset form
		const form = modal.querySelector('form');
		form?.reset();
		const tbody = modal.querySelector('#materialsTable tbody');
		if (tbody) {
			tbody.innerHTML = '';
			addMaterialRow(); // giữ lại 1 dòng trống
		}
		const title = modal.querySelector('.modal-header h2');
		if (title) title.textContent = 'Thêm Sản Phẩm';
		const btnSubmit = modal.querySelector('.btn-submit');
		if (btnSubmit) btnSubmit.textContent = 'Thêm';
	};

	// Click ngoài modal để đóng
	document.addEventListener('click', (e) => {
		if (e.target.classList.contains('modal')) closeModal(e.target.id);
	});

	// ESC để đóng
	document.addEventListener('keydown', (e) => {
		if (e.key === 'Escape') {
			document.querySelectorAll('.modal.show').forEach(m => closeModal(m.id));
		}
	});

	// Review ảnh sản phẩm
	window.previewProduct = function(event) {
		const file = event.target.files[0];
		if (!file) return;
		const reader = new FileReader();
		reader.onload = () => (document.getElementById('productPreview').src = reader.result);
		reader.readAsDataURL(file);
	};

	// Cập nhật đơn vị tính theo nguyên liệu chọn
	function updateUnitDisplay(selectElement) {
		const row = selectElement.closest("tr");
		const materialId = selectElement.value;
		const unitCell = row.querySelector(".unit-display");
		unitCell.textContent = materialsData[materialId]?.donViTinh || "";
	}

	// Thêm dòng nguyên liệu
	window.addMaterialRow = function() {
		const tableBody = document.querySelector("#materialsTable tbody");
		if (!tableBody) return;

		let template = tableBody.querySelector(".material-row");
		if (!template) {
			// clone từ mẫu trong HTML (tr đầu tiên)
			template = document.createElement('tr');
			template.className = 'material-row';
			template.innerHTML = `
	<td>
		<select name="materials[0].materialId" required>
			<option value="">Chọn nguyên liệu</option>
		</select>
	</td>
	<td><input type="number" name="materials[0].soLuong" min="1" value="1" required></td>
	<td><span class="unit-display"></span></td>
	<td><button type="button" class="btn-remove">Xóa</button></td>
	`;
		}

		const newRow = template.cloneNode(true);

		// set name index
		newRow.querySelectorAll("select, input").forEach(el => {
			if (el.name?.includes("materials[0]")) {
				el.name = el.name.replace("materials[0]", `materials[${materialIndex}]`);
			}
			if (el.tagName === "INPUT") el.value = 1;
		});

		// build options từ materialsData
		const sel = newRow.querySelector("select[name*='materialId']");
		if (sel) {
			sel.innerHTML = `<option value="">Chọn nguyên liệu</option>`;
			Object.values(materialsData).forEach(m => {
				const opt = document.createElement('option');
				opt.value = m.maNL;
				opt.textContent = m.tenNL;
				opt.dataset.unit = m.donViTinh || '';
				sel.appendChild(opt);
			});
			sel.addEventListener('change', () => updateUnitDisplay(sel));
		}

		newRow.querySelector('.btn-remove')?.addEventListener('click', () => {
			const rows = document.querySelectorAll("#materialsTable .material-row");
			if (rows.length > 1) newRow.remove();
		});

		tableBody.appendChild(newRow);
		materialIndex++;
	};

	// ==============================
	// ⚙️ 2) API CRUD SẢN PHẨM
	// ==============================
	const API = '/api/vendor/products';

	async function loadProducts() {
		// dùng endpoint no-paging
		const form = document.getElementById('searchFilterForm');
		const keyword = form?.querySelector('[name="keyword"]')?.value ?? '';
		const status = form?.querySelector('#statusFilter')?.value || '';
		const categoryId = form?.querySelector('#categoryFilter')?.value || '';

		const q = new URLSearchParams({
			keyword: keyword.trim(),
			status: status || '',
			categoryId: categoryId || ''
		});

		try {
			const r = await fetch(`${API}/no-paging?` + q.toString());
			if (!r.ok) throw new Error('Không tải được danh sách');
			const data = await r.json(); // List<ProductRowVM>

			const tbody = document.getElementById('productTableBody');
			if (!tbody) return;
			tbody.innerHTML = '';

			if (!data.length) {
				tbody.innerHTML = `<tr><td colspan="7" style="text-align:center">Không có dữ liệu</td></tr>`;
				return;
			}

			data.forEach(p => {
				const tr = document.createElement('tr');
				tr.innerHTML = `
					<td>${p.maSP}</td>
					<td>${escapeHtml(p.tenSP ?? '')}</td>
					<td>${p.hinhAnh ? `<img src="${p.hinhAnh}" alt="" style="height:128px;border-radius:6px">` : ''}</td>
					<td class="text-right">${p.giaGoc ?? p.gia ?? ''}</td>
					<td>${escapeHtml(p.trangThaiText ?? '')}</td>
					<td class="text-right">
						<a href="javascript:void(0)" class="btn-edit" data-id="${p.maSP}">
							<i class="fas fa-edit"></i>
						</a>
						<button type="button" class="btn-delete" data-id="${p.maSP}">
							<i class="fas fa-trash"></i>
						</button>
					</td>`;
				tbody.appendChild(tr);
			});

			tbody.querySelectorAll('.btn-edit').forEach(btn => btn.addEventListener('click', () => openEdit(btn.dataset.id)));
			tbody.querySelectorAll('.btn-delete').forEach(btn => btn.addEventListener('click', () => del(btn.dataset.id)));
		} catch (e) {
			const tbody = document.getElementById('productTableBody');
			if (tbody) tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:red">${e.message || e}</td></tr>`;
		}
	}
	document.addEventListener('click', (e) => {
		const editBtn = e.target.closest('.btn-edit');
		if (editBtn) {
			const id = editBtn.dataset.id;
			if (id) openEdit(id);
			return;
		}

		const delBtn = e.target.closest('.btn-del');
		if (delBtn) {
			const id = delBtn.dataset.id;
			if (id) del(id);
			return;
		}
	});

	// mở modal Sửa
	async function openEdit(id) {
		try {
			const [detailRes, statuses] = await Promise.all([
				fetch(`${API}/${id}`),
				loadStatuses()
			]);
			if (!detailRes.ok) throw new Error(`Không tải được sản phẩm #${id}`);
			const p = await detailRes.json();

			openModal('addProductModal');
			const modal = document.getElementById('addProductModal');
			modal.dataset.editingId = id;

			// đổi tiêu đề + nút
			modal.querySelector('.modal-header h2').textContent = 'Sửa Sản Phẩm';
			modal.querySelector('.btn-submit').textContent = 'Cập nhật';

			// fill form
			document.getElementById('tenSP').value = p.tenSP || '';
			document.getElementById('donViTinh').value = p.donViTinh || '';
			document.getElementById('gia').value = p.gia || 0;
			document.getElementById('moTa').value = p.moTa || '';
			document.getElementById('danhMuc').value = p.maDM || '';

			// trạng thái
			const selStatus = document.getElementById('trangThai');
			populateStatusSelect(selStatus, statuses, p.trangThai);

			const preview = document.getElementById('productPreview');
			if (p.hinhAnh) {
				preview.src = p.hinhAnh.startsWith('http') ? p.hinhAnh : (window.location.origin + p.hinhAnh);
			} else {
				preview.src = '/styles/image/logo.png';
			}

			// materials
			const tbody = document.querySelector('#materialsTable tbody');
			tbody.innerHTML = '';
			(p.materials || []).forEach(line => {
				addMaterialRow();
				const last = tbody.querySelector('tr.material-row:last-child');
				const sel = last.querySelector('select');
				const qty = last.querySelector('input[type="number"]');
				sel.value = String(line.maNL);
				qty.value = line.soLuongCan || 1;
				updateUnitDisplay(sel);
			});
			if (!(p.materials || []).length) addMaterialRow();
		} catch (e) {
			alert(e.message || e);
		}
	}


	// xóa
	function del(id) {
		if (!confirm('Xóa sản phẩm này?')) return;
		fetch(`${API}/${id}`, { method: 'DELETE' })
			.then(r => {
				if (!r.ok) throw new Error('Xóa thất bại');
				loadProducts();
			})
			.catch(err => alert(err.message || err));
	}

	// submit form (JSON)
	function bindFormSubmit() {
		const modal = document.getElementById('addProductModal');
		const form = modal?.querySelector('form');
		if (!form) return;

		form.addEventListener('submit', async (e) => {
			e.preventDefault();

			const tenSP = form.querySelector('#tenSP').value.trim();
			const donViTinh = form.querySelector('#donViTinh').value.trim();
			const gia = Number(form.querySelector('#gia').value || 0);
			const moTa = form.querySelector('#moTa').value || '';
			const danhMucId = Number(form.querySelector('#danhMuc').value);
			const trangThai = Number(form.querySelector('#trangThai').value); // ⭐ lấy từ select trạng thái

			const rows = form.querySelectorAll('#materialsTable tbody tr.material-row');
			const materials = Array.from(rows).map(tr => {
				const sel = tr.querySelector('select');
				const qty = tr.querySelector('input[type="number"]');
				return { maNL: Number(sel.value), soLuongCan: Number(qty.value || 1) };
			}).filter(x => x.maNL);

			const payload = { tenSP, donViTinh, gia, moTa, danhMucId, trangThai, materials };

			const editingId = modal.dataset.editingId;
			const method = editingId ? 'PUT' : 'POST';
			const url = editingId ? `${API}/${editingId}` : API;

			fetch(url, {
				method,
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload)
			})
				.then(async r => {
					if (!r.ok) throw new Error(await r.text());

					// lấy id sản phẩm để upload ảnh
					const modal = document.getElementById('addProductModal');
					const editingId = modal.dataset.editingId;
					let productId = editingId;

					// nếu là tạo mới, BE của bạn trả 201 + body là ProductDetailResponse.
					// Nếu controller hiện trả 200, cũng OK: đọc body để lấy id
					const resBody = await r.json().catch(() => null);
					if (!productId && resBody && resBody.maSP) {
						productId = resBody.maSP;
					}

					const locationHeader = r.headers.get('Location');
					if (!productId && locationHeader) {
						const m = locationHeader.match(/\/api\/vendor\/products\/(\d+)/);
						if (m) productId = m[1];
					}


					// nếu có file thì upload ảnh
					const fileInput = document.querySelector('#file');
					const file = fileInput?.files?.[0];
					if (productId && file) {
						const fd = new FormData();
						fd.append('file', file);
						const up = await fetch(`/api/vendor/products/${productId}/image`, {
							method: 'POST',
							body: fd
						});
						if (!up.ok) throw new Error('Upload ảnh thất bại');
						// clear file sau khi upload ok (tuỳ chọn)
						fileInput.value = '';
					}

					closeModal('addProductModal');
					loadProducts();
				})
				.catch(err => alert(err.message || err));
		});
	}

	// Init
	document.addEventListener('DOMContentLoaded', async () => {
		initMaterialsDataFromWindow();
		addMaterialRow(); // có sẵn 1 dòng nguyên liệu
		// load mặc định danh sách trạng thái cho modal Thêm
		const statuses = await loadStatuses();
		const selStatus = document.getElementById('trangThai');
		if (selStatus) populateStatusSelect(selStatus, statuses, 1); // default: Đang bán (1)
		// bind filter submit
		const formFilter = document.getElementById('searchFilterForm');
		formFilter?.addEventListener('submit', (e) => {
			e.preventDefault();
			loadProducts();
		});
		loadProducts();
		bindFormSubmit();
	});
})();
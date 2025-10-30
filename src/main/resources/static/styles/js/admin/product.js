/* ==========================
   BIẾN TOÀN CỤC VÀ DOM ELEMENTS
========================== */
let prodCurrentPage = 0;
const prodPageSize = 10;

const prodTbody = document.getElementById('productTableBody');
const prodPagination = document.getElementById('productPagination');

/* ==========================
   TẢI DỮ LIỆU VÀ RENDER
========================== */

/**
 * Tải danh sách sản phẩm từ API dựa trên bộ lọc và phân trang
 */
async function loadProducts(page = 0) {
	prodCurrentPage = page;
	
	// Lấy giá trị từ các bộ lọc
	const keyword = document.querySelector('#searchFilterForm input[name="keyword"]')?.value || '';
	const categoryId = document.getElementById('categoryFilter')?.value || '';
	const storeId = document.getElementById('storeFilter')?.value || '';
	const status = document.getElementById('statusFilter')?.value || '';

	// Xây dựng parameters
	const params = new URLSearchParams({ page, size: prodPageSize });
	if (keyword) params.append('keyword', keyword);
	if (categoryId) params.append('categoryId', categoryId);
	if (storeId) params.append('storeId', storeId);
	if (status) params.append('status', status);

	try {
		const res = await fetch(`/api/admin/product?${params.toString()}`);
		if (!res.ok) {
			const errorText = await res.text();
			throw new Error(errorText || 'Lỗi tải danh sách');
		}
		
		const data = await res.json();
		renderProductTable(data.content);
		renderProductPagination(data); // <-- Sử dụng hàm phân trang nâng cao
		
	} catch (error) {
		console.error("Lỗi khi tải sản phẩm:", error.message);
		if (prodTbody) prodTbody.innerHTML = `<tr><td colspan="8" class="text-center">Lỗi: ${error.message}</td></tr>`;
	}
}

/**
 * Hiển thị dữ liệu sản phẩm lên bảng
 */
function renderProductTable(items) {
	if (!prodTbody) return;
	prodTbody.innerHTML = '';
	
	if (!items || items.length === 0) {
		prodTbody.innerHTML = `<tr><td colspan="8" class="text-center">Không có dữ liệu</td></tr>`;
		return;
	}
	
	items.forEach(p => {
		const tr = document.createElement('tr');
		
		// 🌟 SỬA LỖI LOGIC: Dùng p.status cho trạng thái, không phải p.isActive
		// (Backend của bạn có vẻ trả về p.status qua AdminProductServiceImpl)
		// Tuy nhiên, nếu bạn muốn hiển thị 'Active'/'Inactive' dựa trên p.isActive, hãy dùng nó.
		// Tạm thời dùng p.status như code gốc:
		const statusText = p.status ? escapeHTML(p.status) : (p.isActive ? 'Active' : 'Inactive');
		
		// 🌟 LOGIC ẢNH: Sử dụng ảnh mặc định (placeholder) nếu p.defaultImage là null
		const imageUrl = p.defaultImage || '/images/default-placeholder.png'; // <-- Đổi thành đường dẫn ảnh mặc định của bạn
		
		tr.innerHTML = `
            <td>
				<img src="${imageUrl}" style="width:128px;height:128px;object-fit:cover;border-radius:8px">
			</td>
            <td>${escapeHTML(p.productName || '')}</td>
            <td>${escapeHTML(p.storeName || '')}</td>
            <td>${escapeHTML(p.categoryName || '')}</td>
            <td>${p.price != null ? p.price.toLocaleString('vi-VN') + ' VNĐ' : ''}</td>
            <td>${p.stock != null ? p.stock : ''}</td>
            <td>${statusText}</td>
            <td class="text-right">
                <button class="btn-ban" data-id="${p.id}">Cấm</button>
                <button class="btn-unban" data-id="${p.id}">Bỏ cấm</button>
            </td>
        `;
		prodTbody.appendChild(tr);
	});
}

/* ==========================
   HÀM PHÂN TRANG NÂNG CAO
========================== */

/**
 * Hiển thị thanh phân trang (Logic giống file Khuyến mãi)
 */
function renderProductPagination(data) {
	if (!prodPagination) return;
	prodPagination.innerHTML = ""; // Xóa nội dung cũ

	const totalPages = Math.max(1, data.totalPages);
	const currentPage = data.number;

	// Không hiển thị nếu chỉ có 1 trang
	if (totalPages <= 1) return;

	// Nút Previous (Trước)
	const prevLink = document.createElement("a");
	prevLink.innerHTML = '&laquo; Trước';
	prevLink.href = "#";
	if (currentPage === 0) {
		prevLink.classList.add("disabled");
	} else {
		prevLink.addEventListener("click", (e) => {
			e.preventDefault();
			loadProducts(currentPage - 1);
		});
	}
	prodPagination.appendChild(prevLink);

	// Logic hiển thị các số trang
	const maxVisible = 7;
	if (totalPages <= maxVisible) {
		// Hiển thị tất cả các trang
		for (let i = 0; i < totalPages; i++) {
			addProdPageLink(prodPagination, i, currentPage);
		}
	} else {
		// Hiển thị logic có dấu "..."
		addProdPageLink(prodPagination, 0, currentPage); // Luôn hiển thị trang 1
		
		if (currentPage > 3) addProdDots(prodPagination);

		let start = Math.max(1, currentPage - 2);
		let end = Math.min(totalPages - 2, currentPage + 2);

		if (currentPage < 3) end = Math.min(totalPages - 2, 4);
		else if (currentPage > totalPages - 4) start = Math.max(1, totalPages - 5);

		for (let i = start; i <= end; i++) {
			addProdPageLink(prodPagination, i, currentPage);
		}

		if (currentPage < totalPages - 4) addProdDots(prodPagination);
		
		addProdPageLink(prodPagination, totalPages - 1, currentPage); // Luôn hiển thị trang cuối
	}

	// Nút Next (Sau)
	const nextLink = document.createElement("a");
	nextLink.innerHTML = 'Sau &raquo;';
	nextLink.href = "#";
	if (currentPage === totalPages - 1) {
		nextLink.classList.add("disabled");
	} else {
		nextLink.addEventListener("click", (e) => {
			e.preventDefault();
			loadProducts(currentPage + 1);
		});
	}
	prodPagination.appendChild(nextLink);
}

/**
 * Hàm tiện ích: Thêm một liên kết trang (số)
 */
function addProdPageLink(container, pageIndex, currentPage) {
	const link = document.createElement("a");
	link.textContent = pageIndex + 1;
	link.href = "#";
	if (pageIndex === currentPage) link.classList.add("active");
	
	link.addEventListener("click", (e) => {
		e.preventDefault();
		if (pageIndex !== currentPage) loadProducts(pageIndex);
	});
	container.appendChild(link);
}

/**
 * Hàm tiện ích: Thêm dấu "..."
 */
function addProdDots(container) {
	const dots = document.createElement("span");
	dots.textContent = "...";
	dots.classList.add("pagination-dots");
	container.appendChild(dots);
}


/* ==========================
   HÀNH ĐỘNG (BAN/UNBAN)
========================== */

/**
 * Cấm sản phẩm
 */
async function banProduct(id) {
	if (!confirm('Bạn có chắc muốn cấm sản phẩm này?')) return;
	try {
		const res = await fetch(`/api/admin/product/${id}/ban`, { method: 'PATCH' });
		if (!res.ok) {
			const errorText = await res.text();
			throw new Error(errorText || 'Hành động thất bại');
		}
		loadProducts(prodCurrentPage); // Tải lại trang hiện tại
	} catch (error) {
		alert(`Lỗi: ${error.message}`);
	}
}

/**
 * Bỏ cấm sản phẩm
 */
async function unbanProduct(id) {
	if (!confirm('Bạn có chắc muốn bỏ cấm sản phẩm này?')) return;
	try {
		const res = await fetch(`/api/admin/product/${id}/unban`, { method: 'PATCH' });
		if (!res.ok) {
			const errorText = await res.text();
			throw new Error(errorText || 'Hành động thất bại');
		}
		loadProducts(prodCurrentPage); // Tải lại trang hiện tại
	} catch (error) {
		alert(`Lỗi: ${error.message}`);
	}
}

/* ==========================
   HÀM TIỆN ÍCH (HELPERS)
========================== */

/**
 * Chống XSS: Thoát các ký tự HTML đặc biệt
 */
function escapeHTML(str) {
	if (!str) return '';
	return String(str).replace(/[&<>"']/g, m => ({ 
		'&': '&amp;', 
		'<': '&lt;', 
		'>': '&gt;', 
		'"': '&quot;', 
		'\'': '&#39;' 
	}[m]));
}

/* ==========================
   LẮNG NGHE SỰ KIỆN
========================== */

document.addEventListener('DOMContentLoaded', () => {
	
	// Tải sản phẩm lần đầu
	loadProducts(0);

	// Lắng nghe sự kiện submit form bộ lọc
	document.getElementById('searchFilterForm')?.addEventListener('submit', e => { 
		e.preventDefault(); 
		loadProducts(0); // Luôn về trang 0 khi tìm kiếm
	});

	// Lắng nghe sự kiện thay đổi các bộ lọc <select>
	['categoryFilter', 'storeFilter', 'statusFilter'].forEach(id => {
		document.getElementById(id)?.addEventListener('change', () => {
			loadProducts(0); // Luôn về trang 0 khi đổi bộ lọc
		});
	});

	// Lắng nghe sự kiện click trên bảng (Event Delegation)
	prodTbody?.addEventListener('click', e => {
		const banBtn = e.target.closest('.btn-ban');
		const unbanBtn = e.target.closest('.btn-unban');
		
		if (banBtn) { 
			e.preventDefault(); 
			banProduct(banBtn.dataset.id); 
		}
		
		if (unbanBtn) { 
			e.preventDefault(); 
			unbanProduct(unbanBtn.dataset.id); 
		}
	});
});
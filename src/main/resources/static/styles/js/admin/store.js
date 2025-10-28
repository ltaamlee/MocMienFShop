/*THỐNG KÊ CỬA HÀNG*/
function fetchStoreStats() {
	fetch("/api/admin/store/stats")
		.then(response => response.json())
		.then(data => {
			document.getElementById("totalStoresStat").textContent = data.totalStores;
			document.getElementById("activeStoresStat").textContent = data.activeStores;
			document.getElementById("inactiveStoresStat").textContent = data.inactiveStores;
			document.getElementById("blockedStoresStat").textContent = data.blockedStores;
		})
		.catch(error => console.error("Error fetching store stats:", error));
}


// ===========================
// LOAD DANH SÁCH CỬA HÀNG
// ===========================
function loadStores(page = 0) {
	const size = document.querySelector('select[name="size"]')?.value || 10;
	const keyword = document.querySelector('input[name="keyword"]')?.value || '';
	const status = document.querySelector('#statusFilter')?.value || '';

	let url = `/api/admin/store?page=${page}&size=${size}`;
	if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
	if (status) url += `&status=${status}`;

	fetch(url)
		.then(response => {
			if (!response.ok) throw new Error("Lỗi tải danh sách cửa hàng");
			return response.json();
		})
		.then(data => {
			renderStoreTable(data.content);
			renderPaginationControls(data); // vẽ phân trang dựa trên tổng số trang
		})
		.catch(error => console.error("Error fetching stores:", error));
}

// ===========================
// RENDER BẢNG CỬA HÀNG
// ===========================
function renderStoreTable(stores) {
	const tbody = document.getElementById("storeTableBody");
	if (!tbody) return;

	console.log(stores);

	tbody.innerHTML = "";

	if (!stores || stores.length === 0) {
		tbody.innerHTML = `<tr><td colspan="9" style="text-align:center;">KHÔNG CÓ DỮ LIỆU</td></tr>`;
		return;
	}

	stores.forEach((store, index) => {
		const tr = document.createElement("tr");

		const statusText = store.isOpen ? "Mở cửa" : "Đóng cửa";
		const lockIcon = store.isActive === false
		            ? '<i class="fas fa-lock"></i>'
		            : '<i class="fas fa-lock-open"></i>';

		tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${store.storeName || ''}</td>
			<td>${store.vendorName || ''}</td>
            <td>${store.point || 0}</td>
            <td>${store.rating || 0}</td>
            <td>${statusText}</td>
            <td>
			<button class="btn-block" data-id="${store.id}">${lockIcon}</button>

			</td>
            <td>
			<button class="btn btn-primary btn-sm btn-view" 
			           data-id="${store.id}" 
			           data-bs-toggle="modal" 
			           data-bs-target="#storeDetailModal">
			       <i class="fas fa-eye"></i>
			   </button>
                <button class="btn-delete" data-id="${store.id}"><i class="fas fa-trash"></i></button>
            </td>
        `;

		tbody.appendChild(tr);
	});


	// Gắn sự kiện cho nút khóa
	document.querySelectorAll('.btn-block').forEach(btn => {
		btn.addEventListener('click', function() {
			const storeId = this.dataset.id;
			toggleBlockStore(storeId);
		});
	});

	// Gắn sự kiện cho nút xóa
	document.querySelectorAll('.btn-delete').forEach(btn => {
		btn.addEventListener('click', function() {
			const storeId = this.dataset.id;
			if (confirm("Bạn có chắc muốn xóa cửa hàng này?")) {
				deleteStore(storeId);
			}
		});
	});
	
	// Gắn sự kiện cho nút xem chi tiết
	document.querySelectorAll('.btn-view').forEach(btn => {
	    btn.addEventListener('click', function() {
	        const storeId = this.dataset.id;
	        openStoreModal(storeId);
	    });
	});
}

// ===========================
// HÀM MỞ MODAL CHI TIẾT
// ===========================
function openStoreModal(storeId) {
    // Gọi API để lấy chi tiết store
    fetch(`/api/admin/store/${storeId}`)
        .then(res => res.json())
        .then(store => {
            // Gán dữ liệu vào modal
            document.querySelector('#storeDetailModalLabel').textContent = store.storeName || "Chi tiết cửa hàng";
            document.querySelector('#storeDetailModal .modal-body').innerHTML = `
                <p><strong>Chủ cửa hàng:</strong> ${store.vendorName || 'N/A'}</p>
                <p><strong>Hạng cửa hàng:</strong> ${store.levelName || 'N/A'}</p>
                <p><strong>Điểm:</strong> ${store.point || 0}</p>
                <p><strong>Rating:</strong> ${store.rating || 0}</p>
                <p><strong>Địa chỉ:</strong> ${store.address || 'Chưa cập nhật'}</p>
            `;
        })
        .catch(err => {
			console.error(err);
			document.querySelector('#storeDetailModal .modal-body').innerHTML = "<p>Không thể tải chi tiết cửa hàng.</p>";
		});
}


function toggleBlockStore(storeId) {
    fetch(`/api/admin/store/${storeId}/block`, {
        method: 'PATCH'
    })
    .then(response => {
        if (!response.ok) throw new Error("Không thể khóa cửa hàng");
        return response.text();
    })
    .then(() => {
        console.log("User blocked/unblocked:", storeId);
        loadStores(0); // reload danh sách để cập nhật trạng thái
        fetchStoreStats(); // cập nhật số liệu thống kê
    })
    .catch(error => console.error(error));
}


// ===========================
// HÀM XÓA CỬA HÀNG
// ===========================
function deleteStore(storeId) {
	fetch(`/api/admin/store/${storeId}`, {
		method: 'DELETE'
	})
		.then(response => {
			if (!response.ok) throw new Error("Không thể xóa cửa hàng");
			alert("Xóa cửa hàng thành công!");
			loadStores(0); // reload bảng
			fetchStoreStats(); // cập nhật số liệu thống kê
		})
		.catch(error => console.error(error));
}

// ===========================
// INIT
// ===========================
document.addEventListener("DOMContentLoaded", function() {
	fetchStoreStats();
	loadStores(0); // load trang đầu tiên

	document.getElementById("statusFilter")?.addEventListener("change", function(e) {
		e.preventDefault();
		loadStores(0);
	});

	document.getElementById('searchFilterForm')?.addEventListener("submit", function(e) {
		e.preventDefault(); // chặn reload trang khi submit
		loadStores(0);
	});
});



function renderPaginationControls(data) {
	const pagination = document.getElementById("storePagination");
	pagination.innerHTML = "";

	const totalPages = Math.max(1, data.totalPages);
	const currentPage = data.number;

	// Nút Previous
	const prevBtn = document.createElement("button");
	prevBtn.innerHTML = '&laquo;';
	prevBtn.disabled = currentPage === 0;
	prevBtn.classList.add("page-nav-btn");
	if (currentPage === 0) prevBtn.classList.add("disabled");
	prevBtn.addEventListener("click", () => {
		if (currentPage > 0) loadStores(currentPage - 1);
	});
	pagination.appendChild(prevBtn);

	// Logic hiển thị trang
	const maxVisible = 7; // Số trang tối đa hiển thị

	if (totalPages <= maxVisible) {
		// Hiển thị tất cả các trang
		for (let i = 0; i < totalPages; i++) {
			addPageButton(pagination, i, currentPage);
		}
	} else {
		// Luôn hiển thị trang đầu
		addPageButton(pagination, 0, currentPage);

		if (currentPage > 3) {
			addDots(pagination);
		}

		// Các trang giữa
		let start = Math.max(1, currentPage - 2);
		let end = Math.min(totalPages - 2, currentPage + 2);

		for (let i = start; i <= end; i++) {
			addPageButton(pagination, i, currentPage);
		}

		if (currentPage < totalPages - 4) {
			addDots(pagination);
		}

		// Luôn hiển thị trang cuối
		addPageButton(pagination, totalPages - 1, currentPage);
	}

	// Nút Next
	const nextBtn = document.createElement("button");
	nextBtn.innerHTML = '&raquo;';
	nextBtn.disabled = currentPage === totalPages - 1;
	nextBtn.classList.add("page-nav-btn");
	if (currentPage === totalPages - 1) nextBtn.classList.add("disabled");
	nextBtn.addEventListener("click", () => {
		if (currentPage < totalPages - 1) loadStores(currentPage + 1);
	});
	pagination.appendChild(nextBtn);
}

// Hàm phụ trợ
function addPageButton(container, pageIndex, currentPage) {
	const btn = document.createElement("a");
	btn.textContent = pageIndex + 1;
	btn.href = "#";
	if (pageIndex === currentPage) btn.classList.add("active");
	btn.addEventListener("click", (e) => {
		e.preventDefault();
		loadStores(pageIndex);
	});
	container.appendChild(btn);
}

function addDots(container) {
	const dots = document.createElement("span");
	dots.textContent = "...";
	dots.classList.add("pagination-dots");
	container.appendChild(dots);
}
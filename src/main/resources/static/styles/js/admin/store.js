/* ===========================
   THỐNG KÊ CỬA HÀNG
=========================== */
function fetchStoreStats() {
    fetch("/api/admin/store/stats")
        .then(res => res.json())
        .then(data => {
            document.getElementById("totalStoresStat").textContent = data.totalStores ?? 0;
            document.getElementById("activeStoresStat").textContent = data.activeStores ?? 0;
            document.getElementById("inactiveStoresStat").textContent = data.inactiveStores ?? 0;
            document.getElementById("blockedStoresStat").textContent = data.blockedStores ?? 0;
        })
        .catch(err => console.error("Error fetching store stats:", err));
}

/* ===========================
   LOAD DANH SÁCH CỬA HÀNG
=========================== */
function loadStores(page = 0) {
    const size = document.querySelector('select[name="size"]')?.value || 10;
    const keyword = document.querySelector('input[name="keyword"]')?.value || '';
    const statusRaw = document.querySelector('#statusFilter')?.value;
    const isActive = statusRaw === "" ? null : statusRaw === "true"; // convert string to boolean

    let url = `/api/admin/store?page=${page}&size=${size}`;
    if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
    if (isActive !== null) url += `&isActive=${isActive}`;

    fetch(url)
        .then(res => res.ok ? res.json() : Promise.reject("Lỗi tải danh sách cửa hàng"))
        .then(data => {
			console.log("Raw store data:", data); // ✅ In toàn bộ JSON nhận được

            renderStoreTable(data.content);
            renderPaginationControls(data);
        })
        .catch(err => console.error("Error fetching stores:", err));
}

/* ===========================
   RENDER BẢNG CỬA HÀNG
=========================== */
function renderStoreTable(stores) {
    const tbody = document.getElementById("storeTableBody");
    if (!tbody) return;
    tbody.innerHTML = "";

    if (!stores || stores.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" class="text-center">KHÔNG CÓ DỮ LIỆU</td></tr>`;
        return;
    }

    stores.forEach((store, index) => {
		console.log("Store at index", index, ":", store);
        const statusText = store.isOpen ? "Mở cửa" : "Đóng cửa";
        const lockIcon = store.active ? '<i class="fas fa-lock-open"></i>' : '<i class="fas fa-lock"></i>';

        const tr = document.createElement("tr");
		
        tr.innerHTML = `
            <td>${index + 1}</td>
			<td class="align-middle">
			   <div class="d-flex align-items-center">
			     <img src="${store.avatar || '/images/no-avatar.png'}"
			          alt="Avatar"
			          class="rounded-circle me-2"
			          style="width: 40px; height: 40px; object-fit: cover;">
			     <div>
			       <div class="fw-bold">${store.storeName || '(Không tên)'}</div>
			       <small class="text-muted">${store.levelName || ''}</small>
			     </div>
			   </div>
			 </td>
			 <td class="align-middle">
			     <div class="d-flex flex-column">
			       <div class="fw-semibold text-dark">
			         <i class="fas fa-user me-1 text-primary"></i> 
			         ${store.vendorName || '(Chưa có)'}
			       </div>
			       <small class="text-muted">
			         <i class="fas fa-phone-alt me-1 text-secondary"></i>
			         ${store.vendorPhone || 'Không có số'}
			       </small>
			       ${store.vendorEmail 
			         ? `<small class="text-muted">
			              <i class="fas fa-envelope me-1 text-secondary"></i>
			              ${store.vendorEmail}
			            </small>`
			         : ''}
			     </div>
			   </td>
            <td>${store.point ?? 0}</td>
            <td>${store.rating ?? 0}</td>
			<td class=" align-middle">
			    <span class="badge ${store.active ? 'bg-success' : 'bg-secondary'}">
			      ${store.active ? 'Hoạt động' : 'Đã khóa'}
			    </span>
			  </td>            <td><button class="btn-block" data-id="${store.id}">${lockIcon}</button>
                <button class="btn btn-primary btn-sm btn-view" data-id="${store.id}" data-bs-toggle="modal" data-bs-target="#storeDetailModal">
                    <i class="fas fa-eye"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Gắn sự kiện
    tbody.querySelectorAll('.btn-block').forEach(btn => {
        btn.onclick = () => toggleBlockStore(btn.dataset.id);
    });

    tbody.querySelectorAll('.btn-view').forEach(btn => {
        btn.onclick = () => openStoreModal(btn.dataset.id);
    });
}

/* ===========================
   MỞ MODAL CHI TIẾT
=========================== */
function openStoreModal(storeId) {
    fetch(`/api/admin/store/${storeId}`)
        .then(res => res.json())
        .then(store => {
            const modalBody = document.querySelector('#storeDetailModal .modal-body');
            if (!modalBody) return;

            document.querySelector('#storeDetailModalLabel').textContent = store.storeName || "Chi tiết cửa hàng";
            modalBody.innerHTML = `
                <p><strong>Chủ cửa hàng:</strong> ${store.vendorName || 'N/A'}</p>
                <p><strong>Hạng cửa hàng:</strong> ${store.levelName || 'N/A'}</p>
                <p><strong>Điểm:</strong> ${store.point ?? 0}</p>
                <p><strong>Rating:</strong> ${store.rating ?? 0}</p>
                <p><strong>Địa chỉ:</strong> ${store.address || 'Chưa cập nhật'}</p>
            `;
        })
        .catch(err => {
            console.error(err);
            const modalBody = document.querySelector('#storeDetailModal .modal-body');
            if (modalBody) modalBody.innerHTML = "<p>Không thể tải chi tiết cửa hàng.</p>";
        });
}

/* ===========================
   KHÓA/ MỞ KHÓA CỬA HÀNG
=========================== */
function toggleBlockStore(storeId) {
    fetch(`/api/admin/store/${storeId}/block`, { method: 'PATCH' })
        .then(res => res.ok ? res.text() : Promise.reject("Không thể khóa cửa hàng"))
        .then(() => {
            loadStores(0);
            fetchStoreStats();
        })
        .catch(err => console.error(err));
}

/* ===========================
   XÓA CỬA HÀNG
=========================== */
function deleteStore(storeId) {
    fetch(`/api/admin/store/${storeId}`, { method: 'DELETE' })
        .then(res => res.ok ? res.text() : Promise.reject("Không thể xóa cửa hàng"))
        .then(() => {
            alert("Xóa cửa hàng thành công!");
            loadStores(0);
            fetchStoreStats();
        })
        .catch(err => console.error(err));
}

/* ===========================
   PHÂN TRANG
=========================== */
function renderPaginationControls(data) {
    const pagination = document.getElementById("storePagination");
    if (!pagination) return;

    pagination.innerHTML = "";
    const totalPages = Math.max(1, data.totalPages ?? 1);
    const currentPage = data.number ?? 0;

    const createButton = (label, disabled, onClick, active = false) => {
        const btn = document.createElement("button");
        btn.innerHTML = label;
        btn.disabled = disabled;
        if (active) btn.classList.add("active");
        btn.classList.add("page-nav-btn");
        btn.onclick = onClick;
        pagination.appendChild(btn);
    };

    // Previous
    createButton('&laquo;', currentPage === 0, () => loadStores(currentPage - 1));

    // Pages
    const maxVisible = 7;
    let start = Math.max(0, currentPage - 3);
    let end = Math.min(totalPages - 1, currentPage + 3);
    if (end - start < maxVisible - 1) start = Math.max(0, end - maxVisible + 1);

    for (let i = start; i <= end; i++) {
        createButton(i + 1, false, () => loadStores(i), i === currentPage);
    }

    // Next
    createButton('&raquo;', currentPage === totalPages - 1, () => loadStores(currentPage + 1));
}

/* ===========================
   INIT
=========================== */
document.addEventListener("DOMContentLoaded", () => {
    fetchStoreStats();
    loadStores(0);

    document.getElementById("statusFilter")?.addEventListener("change", e => {
        e.preventDefault();
        loadStores(0);
    });

    document.getElementById('searchFilterForm')?.addEventListener("submit", e => {
        e.preventDefault();
        loadStores(0);
    });
});

/* 🏷️ THỐNG KÊ KHUYẾN MÃI */
function fetchPromotionStats() {
	fetch("/api/admin/promotion/stats")
		.then(response => response.json())
		.then(data => {
			document.getElementById("totalPromotionsStat").textContent = data.total || 0;
			document.getElementById("activePromotionsStat").textContent = data.active || 0;
			document.getElementById("upcomingPromotionsStat").textContent = data.upcoming || 0;
			document.getElementById("expiringPromotionsStat").textContent = data.expiring || 0;
			document.getElementById("expiredPromotionsStat").textContent = data.expired || 0;

			populatePromotionTypesFilter(data.typeCounts);
			// TODO: Bổ sung populatePromotionStatusFilter(data.statusCounts); nếu server có API này
		})
		.catch(error => console.error("❌ Error fetching promotion stats:", error));
}


/* ==========================
   KHỞI TẠO BIẾN VÀ MODAL
========================== */
let currentPage = 0;
const pageSize = 10;

const tableBody = document.getElementById("promotionTableBody");
const paginationContainer = document.getElementById("promotionPagination");
const searchFilterForm = document.getElementById("searchFilterForm");

const addPromotionModal = document.getElementById("addPromotionModal");
const promotionForm = document.getElementById("promotionForm");

// Input Fields for Modal (Thêm/Sửa)
const nameInput = document.getElementById("name");
const typeInput = document.getElementById("promoType");
const valueInput = document.getElementById("value");
const bannerInput = document.getElementById("banner");
const ribbonInput = document.getElementById("ribbon");
const startDateInput = document.getElementById("startDate");
const endDateInput = document.getElementById("endDate");

/* ==========================
   TẢI DỮ LIỆU (STATS & TABLE)
========================== */

async function loadPromotions(page = 0) {
	currentPage = page;
	// Lấy giá trị từ các bộ lọc
	const keyword = document.querySelector('input[name="keyword"]')?.value || '';
    const typeValue = document.querySelector('#typeFilter')?.value || '';
	const statusValue = document.querySelector('#statusFilter')?.value || '';

	const sortValue = document.querySelector('#sortFilter')?.value || 'createdAt,desc';
	const fromDateValue = document.querySelector('#fromDate')?.value || '';
	const toDateValue = document.querySelector('#toDate')?.value || '';

	const params = new URLSearchParams({
		page: page,
		size: pageSize,
	});

	if (keyword) params.append('keyword', keyword);
	if (typeValue) params.append('type', typeValue);
	// 🌟 SỬA: Gửi tham số status
	if (statusValue) params.append('status', statusValue);

	if (sortValue) params.append('sort', sortValue);
	if (fromDateValue) params.append('fromDate', fromDateValue);
	if (toDateValue) params.append('toDate', toDateValue);

	try {
		const response = await fetch(`/api/admin/promotion?${params.toString()}`);
		if (!response.ok) {
			const errorData = await handleFetchError(response);
			throw new Error(errorData);
		}

		const data = await response.json();

		renderPromotionTable(data.content);
		renderPagination(data);
	} catch (error) {
		console.error("Error fetching promotions:", error);
		// Hiển thị thông báo lỗi chi tiết hơn nếu có
		const displayError = error.message.includes('Lỗi tải danh sách') ? error.message : "LỖI KẾT NỐI API";
		if (tableBody) tableBody.innerHTML = `<tr><td colspan="7" style="text-align:center;">${displayError}</td></tr>`;
	}
}


/* ==========================
   RENDER GIAO DIỆN (TABLE & PAGINATION)
========================== */

function populatePromotionTypesFilter(typeCounts) {
	const typeFilter = document.getElementById("typeFilter");
	if (!typeFilter) return;

	const selectedValue = typeFilter.value;
	typeFilter.querySelectorAll('option:not([value=""])').forEach(option => option.remove());

	if (typeCounts) {
		for (const [type, count] of Object.entries(typeCounts)) {
			const option = document.createElement("option");
			option.value = type;
			option.textContent = `${type} (${count})`;
			typeFilter.appendChild(option);
		}
	}

	if (selectedValue) {
		typeFilter.value = selectedValue;
	}
}

function renderPromotionTable(promotions) {
	if (!tableBody) return;
	tableBody.innerHTML = "";

	if (!promotions || promotions.length === 0) {
		tableBody.innerHTML = `<tr><td colspan="7" style="text-align:center;">KHÔNG CÓ DỮ LIỆU</td></tr>`;
		return;
	}

	promotions.forEach((promo, index) => {
		const tr = document.createElement("tr");
		const stt = index + 1 + (currentPage * pageSize);

		// 🌟 SỬA: Lấy trạng thái từ Server (dùng trường 'status' trong AdminPromotionResponse)
		const serverStatus = promo.status ? promo.status.toUpperCase() : 'UNKNOWN';
		let statusText;
		let statusClass;

		if (serverStatus === 'SCHEDULED') {
			statusText = 'Sắp bắt đầu';
			statusClass = 'status-upcoming';
		} else if (serverStatus === 'EXPIRED') {
			statusText = 'Đã hết hạn';
			statusClass = 'status-expired';
		} else if (serverStatus === 'INACTIVE') {
			statusText = 'Chưa kích hoạt';
			statusClass = 'status-inactive';
		} else if (serverStatus === 'ACTIVE') {
			statusText = 'Đang hoạt động';
			statusClass = 'status-active';
		} else {
			statusText = 'Chưa xác định';
			statusClass = 'status-unknown';
		}

		// 🌟 SỬA: Khắc phục lỗi 'includes' và xử lý giá trị
		const promoType = escapeHTML(promo.type || '');
		const promoValue = promo.value !== null && promo.value !== undefined ? promo.value : 0;

		// Đảm bảo promoType là string trước khi gọi includes
		const isPercent = promoType.includes('PERCENT');
		const displayUnit = isPercent ? '%' : ' VNĐ';
		const displayValue = `${promoValue}${displayUnit}`;


        const isBanned = serverStatus === 'BANNED';
        const isActive = serverStatus === 'ACTIVE';
        tr.innerHTML = `
            <td>${stt}</td>
            <td>${escapeHTML(promo.name)}</td>
            <td>${promoType} (${displayValue})</td>
            <td><span class="${statusClass}">${statusText}</span></td>
            <td>${formatDateTime(promo.startDate)}</td>
            <td>${formatDateTime(promo.endDate)}</td>
            <td>
            <div class="action-buttons">
                <label class="switch" title="Kích hoạt/Nhấc kích hoạt">
                    <input type="checkbox" class="promo-toggle" data-id="${promo.id}" ${isActive ? 'checked' : ''} ${isBanned ? 'disabled' : ''}>
                    <span class="slider round"></span>
                </label>
                <button class="btn-view" data-id="${promo.id}" title="Chỉnh sửa">
                    <i class="fas fa-edit"></i> Sửa
                </button>
                <button class="btn-delete" data-id="${promo.id}" data-name="${escapeHTML(promo.name)}" title="Xóa">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
            </td>
        `;
		tableBody.appendChild(tr);
	});
}

/* ==========================
   HÀNH ĐỘNG (ADD, EDIT, DELETE)
========================== */

function openAddPromotionModal() {
	promotionForm.reset();
	addPromotionModal.style.display = 'block';
	addPromotionModal.style.opacity = '1';
	addPromotionModal.classList.add('show');
}

function closeModal(modalId) {
	const modal = document.getElementById(modalId);
	if (modal) {
		modal.style.display = 'none';
		modal.style.opacity = '0';
		modal.classList.remove('show');
	}
}

async function handleFetchError(response) {
	let errorMessage = `Lỗi ${response.status}: ${response.statusText}`;
	try {
		const errorText = await response.text();
		try {
			const errorData = JSON.parse(errorText);
			// Lấy thông báo lỗi cụ thể từ server
			if (errorData && errorData.error) {
				errorMessage = errorData.error;
			} else if (errorData && errorData.message) {
				errorMessage = errorData.message;
			} else {
				errorMessage = errorText;
			}
		} catch (parseError) {
			if (errorText && errorText.length < 200) {
				errorMessage = errorText;
			}
		}
	} catch (readError) {
		console.error("Không thể đọc nội dung lỗi:", readError);
	}
	return errorMessage;
}

/**
 * 🔹 Xử lý Lưu (Thêm mới)
 */
async function savePromotion(e) {
	e.preventDefault();

	// Reset thông báo lỗi
	document.querySelectorAll('.error-message').forEach(el => el.textContent = '');

	// Kiểm tra validation cơ bản của HTML5
	if (!promotionForm.checkValidity()) {
		promotionForm.reportValidity();
		return;
	}

	const method = "POST";
	const url = `/api/admin/promotion/global`;

	const body = {
		name: nameInput.value.trim(),
		type: typeInput.value,
		// Chuyển null nếu là chuỗi rỗng để server xử lý
		value: valueInput.value ? parseFloat(valueInput.value) : null,
		banner: bannerInput.value.trim() || null,
		ribbon: ribbonInput.value.trim() || null,
		startDate: startDateInput.value,
		endDate: endDateInput.value,
	};

	try {
		const response = await fetch(url, {
			method,
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(body)
		});

		if (!response.ok) {
			const errorMessage = await handleFetchError(response);
			throw new Error(errorMessage);
		}

		closeModal('addPromotionModal');
		loadPromotions(0);
		fetchPromotionStats();
		alert("Lưu khuyến mãi thành công!");

	} catch (error) {
		console.error("Lỗi khi lưu khuyến mãi:", error.message);
		alert(`LỖI: ${error.message}`);
	}
}


async function confirmDeletePromotion(id, name) {
	const confirmAction = confirm(`Bạn có chắc muốn xóa khuyến mãi "${escapeHTML(name)}"?`);
	if (!confirmAction) return;

	try {
		const response = await fetch(`/api/admin/promotion/${id}`, {
			method: "DELETE",
		});

		if (!response.ok) {
			const errorMessage = await handleFetchError(response);
			throw new Error(errorMessage);
		}

		loadPromotions(0);
		fetchPromotionStats();
		alert("Xóa khuyến mãi thành công!");

	} catch (error) {
		console.error("Lỗi khi xóa khuyến mãi:", error.message);
		alert(`LỖI: ${error.message}`);
	}
}


/* ==========================
   LẮNG NGHE SỰ KIỆN (DOM LOADED)
========================== */
document.addEventListener("DOMContentLoaded", function() {

	// Tải dữ liệu ban đầu
	fetchPromotionStats();
	loadPromotions(0);

	// Listener cho form tìm kiếm và các bộ lọc
	searchFilterForm?.addEventListener("submit", function(e) {
		e.preventDefault();
		loadPromotions(0);
	});

    document.getElementById("typeFilter")?.addEventListener("change", function(e) {
		e.preventDefault();
		loadPromotions(0);
	});

	document.getElementById("statusFilter")?.addEventListener("change", function(e) {
		e.preventDefault();
		loadPromotions(0);
	});

	document.getElementById("sortFilter")?.addEventListener("change", function(e) {
		e.preventDefault();
		loadPromotions(0);
	});

	document.getElementById("fromDate")?.addEventListener("change", function(e) {
		e.preventDefault();
		loadPromotions(0);
	});

	document.getElementById("toDate")?.addEventListener("change", function(e) {
		e.preventDefault();
		loadPromotions(0);
	});

	// Lắng nghe form modal
	promotionForm?.addEventListener("submit", savePromotion);

    // Toggle activate/deactivate
    tableBody?.addEventListener('change', async function(e) {
        const toggle = e.target.closest('.promo-toggle');
        if (toggle) {
            const id = toggle.dataset.id;
            try {
                const endpoint = toggle.checked ? `/api/admin/promotion/${id}/activate` : `/api/admin/promotion/${id}/deactivate`;
                const res = await fetch(endpoint, { method: 'PATCH' });
                if (!res.ok) {
                    const msg = await handleFetchError(res);
                    throw new Error(msg);
                }
                loadPromotions(currentPage);
                fetchPromotionStats();
            } catch (err) {
                alert(err.message || 'Lỗi cập nhật trạng thái');
                loadPromotions(currentPage);
            }
        }
    });

    // Ban / Unban / Edit / Delete
    tableBody?.addEventListener('click', async function(e) {
        const banBtn = e.target.closest('.btn-ban');
        const unbanBtn = e.target.closest('.btn-unban');
        const editBtn = e.target.closest('.btn-view');
        const deleteBtn = e.target.closest('.btn-delete');
        
        if (banBtn) {
            const id = banBtn.dataset.id;
            const res = await fetch(`/api/admin/promotion/${id}/ban`, { method: 'PATCH' });
            if (!res.ok) alert(await handleFetchError(res));
            loadPromotions(currentPage); fetchPromotionStats();
        } else if (unbanBtn) {
            const id = unbanBtn.dataset.id;
            const res = await fetch(`/api/admin/promotion/${id}/unban`, { method: 'PATCH' });
            if (!res.ok) alert(await handleFetchError(res));
            loadPromotions(currentPage); fetchPromotionStats();
        } else if (editBtn) {
            const id = editBtn.dataset.id;
            openEditModal(id);
        } else if (deleteBtn) {
            const id = deleteBtn.dataset.id;
            const name = deleteBtn.dataset.name;
            confirmDeletePromotion(id, name);
        }
    });


	// Đóng modal khi click ra ngoài (nếu dùng modal custom)
	window.addEventListener('click', function(event) {
		if (event.target === addPromotionModal) {
			closeModal('addPromotionModal');
		}
	});
});


/* ==========================
   HÀM TIỆN ÍCH (Helpers)
========================== */
function addPageLink(container, pageIndex, currentPage) {
	const link = document.createElement("a");
	link.textContent = pageIndex + 1;
	link.href = "#";
	if (pageIndex === currentPage) link.classList.add("active");
	link.addEventListener("click", (e) => {
		e.preventDefault();
		if (pageIndex !== currentPage) loadPromotions(pageIndex);
	});
	container.appendChild(link);
}

function addDots(container) {
	const dots = document.createElement("span");
	dots.textContent = "...";
	dots.classList.add("pagination-dots");
	container.appendChild(dots);
}

function renderPagination(data) {
	if (!paginationContainer) return;
	paginationContainer.innerHTML = "";

	const totalPages = Math.max(1, data.totalPages);
	const currentPage = data.number;

	// Nút Previous
	const prevLink = document.createElement("a");
	prevLink.innerHTML = '&laquo;';
	prevLink.href = "#";
	if (currentPage === 0) {
		prevLink.classList.add("disabled");
	} else {
		prevLink.addEventListener("click", (e) => {
			e.preventDefault();
			loadPromotions(currentPage - 1);
		});
	}
	paginationContainer.appendChild(prevLink);

	// Logic hiển thị trang
	const maxVisible = 7;
	if (totalPages <= maxVisible) {
		for (let i = 0; i < totalPages; i++) {
			addPageLink(paginationContainer, i, currentPage);
		}
	} else {
		addPageLink(paginationContainer, 0, currentPage);
		if (currentPage > 3) addDots(paginationContainer);

		let start = Math.max(1, currentPage - 2);
		let end = Math.min(totalPages - 2, currentPage + 2);

		if (currentPage < 3) end = Math.min(totalPages - 2, 4);
		else if (currentPage > totalPages - 4) start = Math.max(1, totalPages - 5);

		for (let i = start; i <= end; i++) addPageLink(paginationContainer, i, currentPage);

		if (currentPage < totalPages - 4) addDots(paginationContainer);
		addPageLink(paginationContainer, totalPages - 1, currentPage);
	}

	// Nút Next
	const nextLink = document.createElement("a");
	nextLink.innerHTML = '&raquo;';
	nextLink.href = "#";
	if (currentPage === totalPages - 1) {
		nextLink.classList.add("disabled");
	} else {
		nextLink.addEventListener("click", (e) => {
			e.preventDefault();
			loadPromotions(currentPage + 1);
		});
	}
	paginationContainer.appendChild(nextLink);
}

function formatDateTime(dateStr) {
	if (!dateStr) return "";
	const date = new Date(dateStr);
	const options = {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
		hour12: false
	};
	return date.toLocaleString("vi-VN", options);
}

function escapeHTML(str) {
	if (!str) return "";
	return str.replace(/[&<>"']/g, function(m) {
		return {
			'&': '&amp;',
			'<': '&lt;',
			'>': '&gt;',
			'"': '&quot;',
			"'": '&#39;'
		}[m];
	});
}

/* ==========================
   CHỈNH SỬA KHUYẾN MÃI
========================== */
async function openEditModal(id) {
    try {
        const res = await fetch(`/api/admin/promotion/${id}`);
        if (!res.ok) throw new Error('Không thể tải thông tin khuyến mãi');
        
        const promo = await res.json();
        
        // Populate form
        document.getElementById('editPromotionId').value = promo.id;
        document.getElementById('editName').value = promo.name;
        document.getElementById('editPromoType').value = promo.type;
        document.getElementById('editValue').value = promo.value;
        document.getElementById('editRibbon').value = promo.ribbon || '';
        
        // Format datetime cho input datetime-local
        if (promo.startDate) {
            const start = new Date(promo.startDate);
            document.getElementById('editStartDate').value = start.toISOString().slice(0, 16);
        }
        if (promo.endDate) {
            const end = new Date(promo.endDate);
            document.getElementById('editEndDate').value = end.toISOString().slice(0, 16);
        }
        
        // Hiển thị banner hiện tại nếu có
        const currentBannerSection = document.getElementById('currentBannerSection');
        const currentBannerPreview = document.getElementById('currentBannerPreview');
        
        if (promo.banner) {
            // Banner có thể là chuỗi nhiều URL phân cách bởi dấu phẩy
            const banners = promo.banner.split(',').map(b => b.trim()).filter(b => b);
            if (banners.length > 0) {
                currentBannerSection.style.display = 'block';
                currentBannerPreview.innerHTML = banners.map(url => `
                    <div style="position: relative;">
                        <img src="${url}" style="width: 150px; height: 100px; object-fit: cover; border-radius: 8px; border: 2px solid #ddd;">
                    </div>
                `).join('');
            } else {
                currentBannerSection.style.display = 'none';
            }
        } else {
            currentBannerSection.style.display = 'none';
        }
        
        // Mở modal
        document.getElementById('editPromotionModal').style.display = 'block';
        document.getElementById('editPromotionModal').classList.add('show');
    } catch (error) {
        console.error('Error loading promotion:', error);
        alert('❌ Lỗi: ' + error.message);
    }
}

// Xử lý submit form edit
document.getElementById('editPromotionForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const id = document.getElementById('editPromotionId').value;
    const formData = new FormData();
    
    formData.append('name', document.getElementById('editName').value);
    formData.append('type', document.getElementById('editPromoType').value);
    formData.append('value', document.getElementById('editValue').value);
    formData.append('ribbon', document.getElementById('editRibbon').value);
    formData.append('startDate', document.getElementById('editStartDate').value);
    formData.append('endDate', document.getElementById('editEndDate').value);
    
    // Upload files nếu có
    const fileInput = document.getElementById('editBanner');
    if (fileInput.files.length > 0) {
        for (let i = 0; i < fileInput.files.length; i++) {
            formData.append('banners', fileInput.files[i]);
        }
    }
    
    try {
        const res = await fetch(`/api/admin/promotion/${id}`, {
            method: 'PUT',
            body: formData
        });
        
        if (res.ok) {
            showSuccessAlert('✅ Cập nhật khuyến mãi thành công!');
            closeModal('editPromotionModal');
            loadPromotions(currentPage);
            fetchPromotionStats();
        } else {
            const error = await res.text();
            showErrorAlert('❌ Lỗi: ' + error);
        }
    } catch (error) {
        console.error('Error updating promotion:', error);
        showErrorAlert('❌ Lỗi kết nối');
    }
});
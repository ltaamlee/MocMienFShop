/* 🏷️ THỐNG KÊ PHƯƠNG THỨC VẬN CHUYỂN */
function fetchDeliveryStats() {
	fetch("/api/admin/delivery/stats")
		.then(response => {
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.json();
		})
		.then(data => {
			// Cập nhật các thẻ thống kê
			document.getElementById("totalDeliveriesStat").textContent = data.total || 0;
			document.getElementById("activeDeliveriesStat").textContent = data.active || 0;
			document.getElementById("inactiveDeliveriesStat").textContent = data.inactive || 0;
		})
		.catch(error => console.error("❌ Error fetching delivery stats:", error));
}


/* ==========================
   KHỞI TẠO BIẾN VÀ MODAL
========================== */
let currentPage = 0;
const pageSize = 10; // Kích thước trang mặc định

const tableBody = document.getElementById("deliveryTableBody");
// Cần thêm container này vào HTML nếu muốn dùng phân trang.
const paginationContainer = document.getElementById("deliveryPagination"); 
const searchFilterForm = document.getElementById("searchFilterForm"); 

const addDeliveryModal = document.getElementById("addDeliveryModal");
const deliveryForm = document.getElementById("deliveryForm");

// Input Fields for Modal (Thêm/Sửa) - Lấy theo thuộc tính 'name' của input
const deliveryNameInput = document.querySelector('#deliveryForm input[name="deliveryName"]');
const descriptionInput = document.querySelector('#deliveryForm textarea[name="description"]');
const basePriceInput = document.querySelector('#deliveryForm input[name="basePrice"]');
const pricePerKMInput = document.querySelector('#deliveryForm input[name="pricePerKM"]');
const maxDistanceInput = document.querySelector('#deliveryForm input[name="maxDistance"]');
const isActiveSwitch = document.getElementById("isActiveSwitch");


/* ==========================
   TẢI DỮ LIỆU (STATS & TABLE)
========================== */

async function loadDeliveries(page = 0) {
	currentPage = page;
	
	const params = new URLSearchParams({
		page: page,
		size: pageSize,
	});

	try {
		const response = await fetch(`/api/admin/delivery?${params.toString()}`);
		if (!response.ok) {
			const errorData = await handleFetchError(response);
			throw new Error(errorData);
		}

		const data = await response.json();
		
		renderDeliveryTable(data.content);
		// 🌟 BỔ SUNG: Render phân trang
		renderPagination(data); 

	} catch (error) {
		console.error("Error fetching deliveries:", error);
		const displayError = error.message.includes('Lỗi tải danh sách') ? error.message : "LỖI KẾT NỐI API";
		if (tableBody) tableBody.innerHTML = `<tr><td colspan="9" style="text-align:center;">${displayError}</td></tr>`;
	}
}


/* ==========================
   RENDER GIAO DIỆN (TABLE & PAGINATION)
========================== */

function renderDeliveryTable(deliveries) {
	if (!tableBody) return;
	tableBody.innerHTML = "";

	if (!deliveries || deliveries.length === 0) {
		tableBody.innerHTML = `<tr><td colspan="9" style="text-align:center;">KHÔNG CÓ DỮ LIỆU</td></tr>`;
		return;
	}

	deliveries.forEach((delivery, index) => {
		const tr = document.createElement("tr");
		const stt = index + 1 + (currentPage * pageSize);

		const isActive = delivery.isActive;
		const statusText = isActive ? 'Đang hoạt động' : 'Tạm ngưng';
		const statusClass = isActive ? 'status-active' : 'status-inactive';

		// Định dạng số tiền
		const formatPrice = (price) => {
			if (price === null || price === undefined) return '0';
			// Sử dụng Intl.NumberFormat để định dạng theo chuẩn VN
			return new Intl.NumberFormat('vi-VN', { minimumFractionDigits: 0 }).format(price);
		};

		tr.innerHTML = `
            <td>${stt}</td>
            <td>${escapeHTML(delivery.deliveryName)}</td>
            <td class="text-end">${formatPrice(delivery.basePrice)}</td>
            <td class="text-end">${formatPrice(delivery.pricePerKM)}</td>
            <td class="text-end">${delivery.maxDistance !== null && delivery.maxDistance !== undefined ? delivery.maxDistance : '-'}</td>
            <td>${escapeHTML(delivery.description || '')}</td>
            <td><span class="${statusClass}">${statusText}</span></td>
            <td>${formatDateTime(delivery.createAt)}</td> <td>
                <button class="btn btn-sm btn-warning btn-edit-delivery" 
                        data-id="${delivery.id}" title="Sửa">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger btn-delete-delivery" 
                        data-id="${delivery.id}" 
                        data-name="${escapeHTML(delivery.deliveryName)}" title="Xóa">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;
		tableBody.appendChild(tr);
	});
}

// 🌟 BỔ SUNG: Hàm phân trang
function addPageLink(container, pageIndex, currentPage) {
	const link = document.createElement("a");
	link.textContent = pageIndex + 1;
	link.href = "#";
	if (pageIndex === currentPage) link.classList.add("active");
	link.addEventListener("click", (e) => {
		e.preventDefault();
		if (pageIndex !== currentPage) loadDeliveries(pageIndex);
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
			loadDeliveries(currentPage - 1);
		});
	}
	paginationContainer.appendChild(prevLink);

	// Logic hiển thị trang (Max 7 nút)
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
			loadDeliveries(currentPage + 1);
		});
	}
	paginationContainer.appendChild(nextLink);
}


/* ==========================
   HÀNH ĐỘNG (ADD, EDIT, DELETE)
========================== */

function openAddDeliveryModal() {
	deliveryForm.reset();
	// Đặt action mặc định cho Thêm mới
	deliveryForm.action = "/admin/delivery/add"; 
	document.getElementById("addDeliveryModalLabel").textContent = "Thêm phương thức vận chuyển";
	// Đảm bảo checkbox được hiển thị theo mặc định nếu có
	isActiveSwitch.checked = false; // Mặc định không kích hoạt

	// Bootstrap 5 modal
	const modal = new bootstrap.Modal(addDeliveryModal);
	modal.show();
}

// Hàm đóng modal (Sử dụng API của Bootstrap)
function closeDeliveryModal() {
	const modal = bootstrap.Modal.getInstance(addDeliveryModal);
	if (modal) {
		modal.hide();
	}
}

/**
 * 🔹 Xử lý Lưu (Thêm mới/Cập nhật)
 */
async function saveDelivery(e) {
	e.preventDefault();

	document.querySelectorAll('.error-message').forEach(el => el.textContent = '');

	if (!deliveryForm.checkValidity()) {
		deliveryForm.reportValidity();
		return;
	}

	const url = deliveryForm.action; 
	const isAdding = url.includes("/add");
	
	// Trích xuất ID nếu là Cập nhật
	let deliveryId = null;
	if (!isAdding) {
		const urlParts = url.split('/');
		deliveryId = urlParts[urlParts.length - 1];
	}

	const body = {
		// ID không cần gửi trong body vì nó là một phần của URL PUT, 
		// nhưng ta cần gửi các trường DTO Request
		deliveryName: deliveryNameInput.value.trim(),
		description: descriptionInput.value.trim() || null,
		basePrice: basePriceInput.value ? parseFloat(basePriceInput.value) : 0,
		pricePerKM: pricePerKMInput.value ? parseFloat(pricePerKMInput.value) : 0,
		maxDistance: maxDistanceInput.value ? parseInt(maxDistanceInput.value) : null,
		isActive: isActiveSwitch.checked // Gửi trạng thái Boolean
	};
	
	// API POST/PUT /api/admin/delivery
	const apiUrl = `/api/admin/delivery${isAdding ? '' : `/${deliveryId}`}`; 

	try {
		const response = await fetch(apiUrl, {
			method: isAdding ? "POST" : "PUT",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(body)
		});

		if (!response.ok) {
			const errorMessage = await handleFetchError(response);
			throw new Error(errorMessage);
		}

		closeDeliveryModal();
		// 🌟 BỔ SUNG: Reset form sau khi lưu thành công và đóng modal
		deliveryForm.reset(); 
		
		loadDeliveries(0); 
		fetchDeliveryStats();
		alert(`Lưu phương thức vận chuyển thành công!`);

	} catch (error) {
		console.error("Lỗi khi lưu phương thức vận chuyển:", error.message);
		alert(`LỖI: ${error.message}`);
	}
}

/**
 * 🔹 Xử lý Xóa
 */
async function confirmDeleteDelivery(id, name) {
	const confirmAction = confirm(`Bạn có chắc muốn xóa phương thức vận chuyển "${escapeHTML(name)}"?`);
	if (!confirmAction) return;

	try {
		const response = await fetch(`/api/admin/delivery/${id}`, {
			method: "DELETE",
		});

		if (!response.ok) {
			const errorMessage = await handleFetchError(response);
			throw new Error(errorMessage);
		}

		loadDeliveries(currentPage); // Tải lại trang hiện tại
		fetchDeliveryStats();
		alert("Xóa phương thức vận chuyển thành công!");

	} catch (error) {
		console.error("Lỗi khi xóa phương thức vận chuyển:", error.message);
		alert(`LỖI: ${error.message}`);
	}
}

/**
 * 🔹 Xử lý Sửa
 */
async function openEditDeliveryModal(id) {
    try {
        const response = await fetch(`/api/admin/delivery/${id}`);
        if (!response.ok) {
            const errorData = await handleFetchError(response);
            throw new Error(errorData);
        }
        const delivery = await response.json();
        
        // Điền dữ liệu vào Form
        deliveryForm.reset(); 
        document.getElementById("addDeliveryModalLabel").textContent = "Cập nhật phương thức vận chuyển";
        
        deliveryNameInput.value = delivery.deliveryName || '';
        descriptionInput.value = delivery.description || '';
        basePriceInput.value = delivery.basePrice !== null && delivery.basePrice !== undefined ? delivery.basePrice : 0;
        pricePerKMInput.value = delivery.pricePerKM !== null && delivery.pricePerKM !== undefined ? delivery.pricePerKM : 0;
        maxDistanceInput.value = delivery.maxDistance !== null && delivery.maxDistance !== undefined ? delivery.maxDistance : '';
        isActiveSwitch.checked = delivery.isActive || false;

        // Đặt action cho Cập nhật
        deliveryForm.action = `/admin/delivery/update/${delivery.id}`; 

        // Mở Modal (Giả định bootstrap đã được tải)
        const modal = new bootstrap.Modal(addDeliveryModal);
        modal.show();

    } catch (error) {
        console.error("Lỗi khi tải chi tiết phương thức vận chuyển:", error.message);
        alert(`LỖI: ${error.message}`);
    }
}


/* ==========================
   HÀM TIỆN ÍCH (Helpers)
========================== */

// Tái sử dụng hàm xử lý lỗi fetch
async function handleFetchError(response) {
	let errorMessage = `Lỗi ${response.status}: ${response.statusText}`;
	try {
		const errorText = await response.text();
		try {
			const errorData = JSON.parse(errorText);
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

// Tái sử dụng hàm định dạng thời gian
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

// Tái sử dụng hàm escape HTML
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
   LẮNG NGHE SỰ KIỆN (DOM LOADED)
========================== */
document.addEventListener("DOMContentLoaded", function() {

	// Tải dữ liệu ban đầu
	fetchDeliveryStats();
	loadDeliveries(0);

	// Lắng nghe form modal cho cả Thêm mới và Cập nhật
	deliveryForm?.addEventListener("submit", saveDelivery);

	// Event Delegation cho các nút Sửa và Xóa trong bảng
	tableBody?.addEventListener('click', function(e) {
		
		const deleteBtn = e.target.closest('.btn-delete-delivery');
		if (deleteBtn) {
			e.preventDefault();
			const id = deleteBtn.dataset.id;
			const name = deleteBtn.dataset.name;
			confirmDeleteDelivery(id, name);
			return; 
		}

		const editBtn = e.target.closest('.btn-edit-delivery');
		if (editBtn) {
			e.preventDefault();
			const id = editBtn.dataset.id;
			openEditDeliveryModal(id);
		}
	});
});
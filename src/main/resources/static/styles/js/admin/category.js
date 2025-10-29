/* ==========================
   KHỞI TẠO BIẾN VÀ MODAL
========================== */
let currentPage = 0;
const pageSize = 10; 

const tableBody = document.getElementById("categoryTableBody");
const paginationContainer = document.getElementById("categoryPagination");
const searchForm = document.getElementById("searchFilterForm");

const categoryModal = new bootstrap.Modal(document.getElementById("categoryModal"));

const form = document.getElementById("categoryForm");
const modalTitle = document.getElementById("categoryModalTitle");
const categoryIdInput = document.getElementById("categoryId");
const categoryNameInput = document.getElementById("categoryName");
const slugInput = document.getElementById("slug");
const isActiveInput = document.getElementById("isActive"); 


/* ==========================
   TẢI DỮ LIỆU (STATS & TABLE)
========================== */

function fetchCategoryStats() {
    fetch("/api/admin/category/stats")
        .then(response => response.ok ? response.json() : Promise.reject('Lỗi tải thống kê'))
        .then(data => {
            // Lưu ý: Thống kê của bạn vẫn dùng 'active', điều này OK
            document.getElementById("totalCategoriesStat").textContent = data.total ?? 0;
            document.getElementById("activeCategoriesStat").textContent = data.active ?? 0;
            document.getElementById("inactiveCategoriesStat").textContent = data.inactive ?? 0;
        })
        .catch(error => console.error("Error fetching category stats:", error));
}

async function loadCategories(page = 0) {
    currentPage = page; 
    const keyword = document.querySelector('input[name="keyword"]')?.value || '';
    const isActiveValue = document.querySelector('#statusFilter')?.value || '';
    const sortValue = document.querySelector('#sortFilter')?.value || 'createdAt,desc'; 
    const fromDateValue = document.querySelector('#fromDate')?.value || '';
    const toDateValue = document.querySelector('#toDate')?.value || '';

    const params = new URLSearchParams({
        page: page,
        size: pageSize,
    });
    
    if (keyword) params.append('keyword', keyword);
    if (isActiveValue) params.append('isActive', isActiveValue); // Gửi 'isActive'
    if (sortValue) params.append('sort', sortValue);
    if (fromDateValue) params.append('fromDate', fromDateValue);
    if (toDateValue) params.append('toDate', toDateValue);

    try {
        const response = await fetch(`/api/admin/category?${params.toString()}`);
        if (!response.ok) throw new Error("Lỗi tải danh sách danh mục");
        
        const data = await response.json(); 
        
        renderCategoryTable(data.content);
        renderPagination(data); 
    } catch (error) {
        console.error("Error fetching categories:", error);
        if (tableBody) tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center;">LỖI TẢI DỮ LIỆU</td></tr>`;
    }
}


/* ==========================
   RENDER GIAO DIỆN (TABLE & PAGINATION)
========================== */

function renderCategoryTable(categories) {
    if (!tableBody) return;
    tableBody.innerHTML = ""; 

    if (!categories || categories.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center;">KHÔNG CÓ DỮ LIỆU</td></tr>`;
        return;
    }

    categories.forEach((cat, index) => {        
        const tr = document.createElement("tr");
        const stt = index + 1 + (currentPage * pageSize);
        const lockIcon = cat.active 
            ? '<i class="fas fa-lock-open" title="Đang hoạt động"></i>' 
            : '<i class="fas fa-lock" title="Ngừng hoạt động"></i>';    

        tr.innerHTML = `
            <td>${stt}</td>
            <td>${escapeHTML(cat.categoryName)}</td>
            <td>${escapeHTML(cat.slug)}</td>
            <td class="text-center"> 
                <button class="btn-block" data-id="${cat.id}">
                    ${lockIcon}
                </button>
            </td>
            <td>${formatDateTime(cat.createdAt)}</td>
            <td class="text-center"> 
                <button class="btn-edit" data-id="${cat.id}" title="Sửa">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn-delete" data-id="${cat.id}" data-name="${escapeHTML(cat.categoryName)}" title="Xóa">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;
        tableBody.appendChild(tr);
    });
}

function renderPagination(data) {
    if (!paginationContainer) return;
    paginationContainer.innerHTML = "";

    const totalPages = Math.max(1, data.totalPages);
    const currentPage = data.currentPage; 

    // Nút Previous
    const prevLink = document.createElement("a");
    prevLink.innerHTML = '&laquo;';
    prevLink.href = "#";
    if (currentPage === 0) {
         prevLink.classList.add("disabled");
    } else {
        prevLink.addEventListener("click", (e) => {
            e.preventDefault();
            loadCategories(currentPage - 1);
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
            loadCategories(currentPage + 1);
        });
    }
    paginationContainer.appendChild(nextLink);
}


/* ==========================
   HÀNH ĐỘNG (ADD, EDIT, DELETE, TOGGLE)
========================== */

function openAddCategoryModal() {
    form.reset();
    categoryIdInput.value = "";
    modalTitle.textContent = "Thêm danh mục";
    isActiveInput.checked = true; 
    categoryModal.show();
}

async function openEditModal(id) {
    try {
        const response = await fetch(`/api/admin/category/${id}`);
        if (!response.ok) throw new Error("Không tìm thấy danh mục.");
        const data = await response.json();

        categoryIdInput.value = data.id;
        categoryNameInput.value = data.categoryName;
        slugInput.value = data.slug || "";
        isActiveInput.checked = data.active; 
        modalTitle.textContent = "Chỉnh sửa danh mục";
        categoryModal.show();
    } catch (error) {
        console.error("Lỗi khi tải danh mục để sửa:", error);
        alert("Không thể tải dữ liệu danh mục.");
    }
}

async function handleFetchError(response) {
    let errorMessage = `Lỗi ${response.status}: ${response.statusText}`;
    try {
        const errorText = await response.text();
        try {
            const errorData = JSON.parse(errorText);
            if (errorData && errorData.error) {
                errorMessage = errorData.error;
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
 * 🔹 Xử lý Lưu (Thêm mới hoặc Cập nhật)
 * (ĐÃ SỬA LỖI LOGIC)
 */
async function saveCategory(e) {
    e.preventDefault(); 
    const id = categoryIdInput.value;
    const method = id ? "PUT" : "POST";
    const url = id ? `/api/admin/category/${id}` : `/api/admin/category`;

    const body = {
        id: id || null,
        categoryName: categoryNameInput.value.trim(),
        slug: slugInput.value.trim(),
        
        // --- SỬA LỖI Ở ĐÂY ---
        // Phải gửi "isActive" để khớp với DTO và Controller
        isActive: isActiveInput.checked 
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
        
        categoryModal.hide();
        loadCategories(id ? currentPage : 0); 
        fetchCategoryStats(); 
        alert("Lưu danh mục thành công!");

    } catch (error) {
        console.error("Lỗi khi lưu danh mục:", error.message);
        alert(`LỖI: ${error.message}`);
    }
}

async function confirmDelete(id, name){
    const confirmAction = confirm(`Bạn có chắc muốn xóa danh mục "${escapeHTML(name)}"?`);
    if (!confirmAction) return; 

    try {
        const response = await fetch(`/api/admin/category/${id}`, {
            method: "DELETE",
        });

        if (!response.ok) {
           const errorMessage = await handleFetchError(response);
           throw new Error(errorMessage);
        }
        
        loadCategories(0); 
        fetchCategoryStats();
        alert("Xóa danh mục thành công!");

    } catch (error) {
        console.error("Lỗi khi xóa danh mục:", error.message);
        alert(`LỖI: ${error.message}`);
    }
}

async function toggleCategoryStatus(id) {
    const confirmAction = confirm("Bạn có chắc muốn đổi trạng thái danh mục này không?");
    if (!confirmAction) return; 

    try {
        const response = await fetch(`/api/admin/category/${id}/toggle`, {
            method: 'PATCH', 
            headers: { 'Content-Type': 'application/json' },
        });

        if (!response.ok) {
           const errorMessage = await handleFetchError(response);
           throw new Error(errorMessage);
        }
        
        fetchCategoryStats(); 
        loadCategories(currentPage); 
        alert("Đổi trạng thái thành công!");

    } catch (error)
    {
        console.error("Lỗi khi đổi trạng thái:", error.message);
        alert(`LỖI: ${error.message}`);
    }
}


/* ==========================
   LẮNG NGHE SỰ KIỆN (DOM LOADED)
========================== */
document.addEventListener("DOMContentLoaded", function() {
    
    fetchCategoryStats();
    loadCategories(0); 

    // Listener cho form tìm kiếm
    searchForm?.addEventListener("submit", function(e) {
        e.preventDefault();
        loadCategories(0);
    });

    // Listener cho Trạng thái
    document.getElementById("statusFilter")?.addEventListener("change", function(e) {
        e.preventDefault();
        loadCategories(0);
    });
    
    // THÊM LISTENER CHO CÁC BỘ LỌC MỚI
    document.getElementById("sortFilter")?.addEventListener("change", function(e) {
        e.preventDefault();
        loadCategories(0);
    });
    
    document.getElementById("fromDate")?.addEventListener("change", function(e) {
        e.preventDefault();
        loadCategories(0);
    });
    
    document.getElementById("toDate")?.addEventListener("change", function(e) {
        e.preventDefault();
        loadCategories(0);
    });

    // Lắng nghe các form modal
    form?.addEventListener("submit", saveCategory);

    // Event Delegation cho các nút Sửa/Xóa/Khóa
    tableBody?.addEventListener('click', function(e) {
        const editBtn = e.target.closest('.btn-edit');
        const deleteBtn = e.target.closest('.btn-delete');
        const blockBtn = e.target.closest('.btn-block'); 

        if (editBtn) {
            e.preventDefault();
            const id = editBtn.dataset.id;
            openEditModal(id);
        }

        if (deleteBtn) {
            e.preventDefault();
            const id = deleteBtn.dataset.id;
            const name = deleteBtn.dataset.name;
            confirmDelete(id, name); 
        }
        
        if (blockBtn) { 
            e.preventDefault();
            const id = blockBtn.dataset.id;
            toggleCategoryStatus(id);
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
        if (pageIndex !== currentPage) loadCategories(pageIndex);
    });
    container.appendChild(link);
}

function addDots(container) {
    const dots = document.createElement("span");
    dots.textContent = "...";
    dots.classList.add("pagination-dots");
    container.appendChild(dots);
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
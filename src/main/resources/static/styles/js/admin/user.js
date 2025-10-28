/*THỐNG KÊ NGƯỜI DÙNG*/
function fetchUserStats() {
	fetch("/api/admin/user/stats")
		.then(response => response.json())
		.then(data => {
			document.getElementById("totalUsersStat").textContent = data.totalUsers-1;
			document.getElementById("activeUsersStat").textContent = data.activeUsers-1;
			document.getElementById("inactiveUsersStat").textContent = data.inactiveUsers-1;
			document.getElementById("blockedUsersStat").textContent = data.blockedUsers;
		})
		.catch(error => console.error("Error fetching user stats:", error));
}



/* ==========================
   DANH SÁCH NGƯỜI DÙNG
========================== */

document.addEventListener("DOMContentLoaded", function() {
    
    fetchUserStats();
    loadUsers(0); // load trang đầu tiên khi mở trang

    document.getElementById("statusFilter")?.addEventListener("change", function(e) {
        e.preventDefault();
        loadUsers(0);
    });

    document.getElementById("roleFilter")?.addEventListener("change", function(e) {
        e.preventDefault();
		console.log("Role selected:", this.value);
        loadUsers(0);
    });

    document.querySelector('select[name="size"]')?.addEventListener("change", function(e) {
        e.preventDefault();
        loadUsers(0);
    });

    document.getElementById('searchFilterForm')?.addEventListener("submit", function(e){
        e.preventDefault(); // chặn reload trang khi submit
        loadUsers(0);
    });

});


function loadUsers(page = 0) {
    const size = document.querySelector('select[name="size"]')?.value || 10;
    const keyword = document.querySelector('input[name="keyword"]')?.value || '';
    const status = document.querySelector('#statusFilter')?.value || '';
    const role = document.querySelector('#roleFilter')?.value || '';


    let url = `/api/admin/user?page=${page}&size=${size}`;

    if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
    if (status) url += `&status=${status}`;
    if (role) url += `&role=${encodeURIComponent(role)}`;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error("Lỗi tải danh sách người dùng");
            return response.json();
        })
        .then(data => {
            
            renderUserTable(data.content);
            renderPaginationControls(data);
        })
        .catch(error => console.error("Error fetching users:", error));
}

function renderUserTable(users) {
    const tbody = document.getElementById("userTableBody");
    
    if (!tbody) {
        console.error("Không tìm thấy element có id='userTableBody'");
        return;
    }
    
    tbody.innerHTML = "";

    if (!users || users.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" style="text-align:center;">KHÔNG CÓ DỮ LIỆU</td></tr>`;
        return;
    }

    // Lọc bỏ admin
    const filteredUsers = users.filter(user => user.roleName?.toLowerCase() !== 'admin');

    if (filteredUsers.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" style="text-align:center;">KHÔNG CÓ DỮ LIỆU</td></tr>`;
        return;
    }

    filteredUsers.forEach((user, index) => {        
        const tr = document.createElement("tr");

        const lockIcon = user.isActive === false
            ? '<i class="fas fa-lock"></i>'
            : '<i class="fas fa-lock-open"></i>';

        const lastLogin = user.lastLoginAt
            ? new Date(user.lastLoginAt).toLocaleString()
            : 'Chưa đăng nhập';

        tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${user.username || ''}</td>
            <td>${user.fullName || ''}</td>
            <td>${user.email || ''}</td>
            <td>${user.phone || ''}</td>
            <td>${user.roleName || ''}</td>
            <td>${user.status || ''}</td>
            <td>${lastLogin}</td>
            <td>
                <button class="btn-block" data-id="${user.userId}">${lockIcon}</button>
                <button class="btn-view" data-id="${user.userId}"><i class="fas fa-eye"></i></button>
                <button class="btn-delete" onclick="deleteUser(${user.userId})">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });

    // Gắn sự kiện cho nút khóa
    document.querySelectorAll('.btn-block').forEach(btn => {
        btn.addEventListener('click', function() {
            const userId = this.dataset.id;
            toggleBlockUser(userId);
        });
    });
}


function toggleBlockUser(userId) {
    fetch(`/api/admin/user/${userId}/block`, {
        method: 'PATCH'
    })
    .then(response => {
        if (!response.ok) throw new Error("Không thể khóa user");
        return response.text();
    })
    .then(() => {
        console.log("User blocked/unblocked:", userId);
        loadUsers(0); // reload danh sách để cập nhật trạng thái
        fetchUserStats(); // cập nhật số liệu thống kê
    })
    .catch(error => console.error(error));
}


function deleteUser(userId) {
    // Hỏi xác nhận
    if (!confirm("Bạn có chắc chắn muốn xóa người dùng này?")) {
        return; // người dùng hủy
    }

    fetch(`/api/admin/user/${userId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error("Không thể xóa user. Lỗi server: " + (text || response.statusText));
            });
        }
        return response.text();
    })
    .then(() => {
        console.log("[USER DELETED] UserID:", userId);
        alert("Xóa người dùng thành công!");
        loadUsers(0);       // reload danh sách người dùng
        fetchUserStats();   // cập nhật thống kê
    })
    .catch(error => {
        console.error("Lỗi khi xóa người dùng:", error);
        alert("LỖI: Không thể xóa người dùng. Vui lòng kiểm tra console để biết chi tiết.");
    });
}



function renderPaginationControls(data) {
	const pagination = document.getElementById("userPagination");
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
		if (currentPage > 0) loadUsers(currentPage - 1);
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
		if (currentPage < totalPages - 1) loadUsers(currentPage + 1);
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
		loadUsers(pageIndex);
	});
	container.appendChild(btn);
}

function addDots(container) {
	const dots = document.createElement("span");
	dots.textContent = "...";
	dots.classList.add("pagination-dots");
	container.appendChild(dots);
}

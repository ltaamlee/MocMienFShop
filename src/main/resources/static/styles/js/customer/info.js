document.addEventListener("DOMContentLoaded", () => {
	const editBtn = document.getElementById('btn-edit-info');
	const popup = document.getElementById('editInfoPopup');
	const form = document.getElementById('editInfoForm');

	// Nếu không có form hoặc nút (tránh lỗi ở trang khác)
	if (!editBtn || !popup || !form) return;

	// 👉 Mở popup
	editBtn.addEventListener('click', () => {
		popup.classList.add('active');  // dùng class thay vì style.display
	});

	// 👉 Đóng popup (gọi khi bấm X)
	window.closeEditPopup = function () {
		popup.classList.remove('active');
	};

	// 👉 Khi submit form
	form.addEventListener('submit', (e) => {
		e.preventDefault();

		const formData = new FormData(form);

		fetch(form.action, {
			method: "POST",
			body: formData
		})
		.then(res => {
			if (!res.ok) throw new Error("Lưu thất bại");
			return res.json();
		})
		.then(data => {
			// ✅ Cập nhật UI
			document.getElementById('display-hoTen').value = data.hoTen || "";
			document.getElementById('display-sdt').value = data.sdt || "";
			document.getElementById('display-diaChi').value = data.diaChi || "";
			document.getElementById('display-ngaySinh').value = data.ngaySinh || "";

			// Nếu backend trả email trực tiếp
			if (data.email) {
				document.getElementById('display-email').value = data.email;
			}
			// Nếu backend trả email nằm trong user
			else if (data.user && data.user.email) {
				document.getElementById('display-email').value = data.user.email;
			}

			closeEditPopup();
			alert("Cập nhật thông tin thành công!");
		})
		.catch(err => {
			console.error("Lỗi khi lưu:", err);
			alert("Lỗi khi lưu thông tin!");
		});
	});
});

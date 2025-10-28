document.addEventListener("DOMContentLoaded", () => {
	const editBtn = document.getElementById('btn-edit-info');
	const popup = document.getElementById('editInfoPopup');
	const form = document.getElementById('editInfoForm');

	// Nếu không có form hoặc nút (tránh lỗi ở trang khác)
	if (!editBtn || !popup || !form) return;

	// 👉 Mở popup
	editBtn.addEventListener('click', () => {
		popup.classList.add('active');  // hiển thị popup
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
			// ✅ Cập nhật lại giao diện hiển thị thông tin cá nhân
			document.getElementById('display-fullName').value = data.fullName || "";
			document.getElementById('display-dob').value = data.dob || "";
			document.getElementById('display-gender').value = data.gender || "";
			document.getElementById('display-phone').value = data.phone || "";
			document.getElementById('display-email').value = data.email || "";
			document.getElementById('display-point').value = data.point || "";
			document.getElementById('display-ewallet').value = data.eWallet || "";

			closeEditPopup();
			alert("Cập nhật thông tin thành công!");
			console.log("✅ Thông tin sau khi cập nhật:", data);
		})
		.catch(err => {
			console.error("❌ Lỗi khi lưu:", err);
			alert("Đã xảy ra lỗi khi lưu thông tin!");
		});
	});
});

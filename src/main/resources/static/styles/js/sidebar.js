// --- SET ACTIVE SIDEBAR MENU ---
document.addEventListener('DOMContentLoaded', () => {
	// Lấy URL hiện tại
	const currentUrl = window.location.pathname;
	
	// Tìm tất cả link trong sidebar
	const sidebarLinks = document.querySelectorAll('.sidebar-menu li a');
	
	sidebarLinks.forEach(link => {
		// Xóa class active từ tất cả link
		link.classList.remove('active');
		
		// So sánh href của link với URL hiện tại
		if (link.getAttribute('href') === currentUrl) {
			link.classList.add('active');
		}
	});
});

// --- CLICK EVENT (Optional: nếu muốn active ngay khi click) ---
document.addEventListener('click', (e) => {
	const link = e.target.closest('.sidebar-menu li a');
	
	if (link) {
		// Xóa active từ tất cả link
		document.querySelectorAll('.sidebar-menu li a').forEach(el => {
			el.classList.remove('active');
		});
		
		// Thêm active vào link được click
		link.classList.add('active');
	}
});
document.addEventListener('DOMContentLoaded', () => {

	// Lấy userId từ thuộc tính data của div.user-info
	const userInfoElement = document.querySelector('.user-info');
	const userId = userInfoElement ? userInfoElement.dataset.userId : null; 

	// Kiểm tra trước khi kết nối
	if (!userId) {
	    console.error("Lỗi: Không tìm thấy User ID. Không thể kết nối WebSocket.");
	    return; // Dừng nếu không có userId
	}
	    // Khởi tạo WebSocket
    const socket = new SockJS('/ws-chat');
    const stompClient = Stomp.over(socket);

    // Connect WebSocket
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe(`/queue/notifications-${userId}`, function(notification) {
			console.log('Thông báo nhận được:', notification.body);
            const data = JSON.parse(notification.body);
            displayToast(data);
            addNotificationToList(data);
        });
    });

    // Hiển thị toast
    function displayToast(data) {
        const toastEl = document.getElementById('notificationToast');
        const toastTitle = document.getElementById('toastTitle');
        const toastBody = document.getElementById('toastBody');
        const toastTime = document.getElementById('toastTime');

        toastTitle.textContent = `Thông báo: ${data.type}`;
        toastBody.textContent = data.message;
        toastTime.textContent = new Date(data.timestamp).toLocaleTimeString();

        const bsToast = bootstrap.Toast.getOrCreateInstance(toastEl);
        bsToast.show();
    }

    // Thêm notification vào dropdown/danh sách
    function addNotificationToList(data) {
        const notificationList = document.getElementById('notificationList');
        const notificationCountEl = document.getElementById('notificationCount');

        if (notificationList.textContent.includes('Không có thông báo')) {
            notificationList.innerHTML = '';
        }

        const item = document.createElement('div');
        item.style.padding = '5px 0';
        item.textContent = `${data.type}: ${data.message}`;
        notificationList.prepend(item);

        let count = parseInt(notificationCountEl.textContent || '0') + 1;
        notificationCountEl.textContent = count;
        notificationCountEl.style.display = 'inline-block';
    }

    // DOM elements
    const bell = document.querySelector('.notification-bell');
    const dropdown = document.getElementById('notificationDropdown');
    const notificationCountEl = document.getElementById('notificationCount');

    // Toggle dropdown khi bấm chuông
    bell.addEventListener('click', (e) => {
        e.stopPropagation();
        dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';

        // Khi mở dropdown, reset badge
        if (dropdown.style.display === 'block') {
            notificationCountEl.style.display = 'none';
            notificationCountEl.textContent = '0';
        }
    });

    // Click ngoài dropdown để đóng
    document.addEventListener('click', () => {
        dropdown.style.display = 'none';
    });

});

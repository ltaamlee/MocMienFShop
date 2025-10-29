// Biến toàn cục để giữ đối tượng biểu đồ (để có thể hủy và vẽ lại)
let revenueChart = null;

/* ==========================
   KHỞI CHẠY KHI TẢI TRANG
========================== */
document.addEventListener("DOMContentLoaded", function() {
    
    // 1. Tải các thẻ thống kê KPI
    fetchStats();
    
    // 2. Tải các đơn hàng gần đây
    fetchRecentOrders();

    // 3. Tải dữ liệu biểu đồ mặc định (7 ngày qua)
    loadChartData();

    // 4. Gắn listener cho form lọc biểu đồ
    document.getElementById("chartFilterForm").addEventListener("submit", function(e) {
        e.preventDefault();
        loadChartData();
    });
});

/* ==========================
   TẢI DỮ LIỆU TỪ API
========================== */

/**
 * 🔹 1. Tải các thẻ thống kê (KPIs)
 */
async function fetchStats() {
    try {
        // API này bạn sẽ tạo ở Bước 3
        const response = await fetch("/api/admin/dashboard/stats");
        if (!response.ok) throw new Error("Lỗi tải thống kê");
        
        const data = await response.json(); // Mong đợi DTO có totalRevenue, totalOrders...

        document.getElementById("totalRevenueStat").textContent = formatCurrency(data.totalRevenue ?? 0);
        document.getElementById("totalOrdersStat").textContent = data.totalOrders ?? 0;
        document.getElementById("newCustomersStat").textContent = data.newCustomers ?? 0;
        document.getElementById("totalPartnersStat").textContent = data.totalPartners ?? 0;

    } catch (error) {
        console.error("Error fetching stats:", error);
        // Có thể hiển thị lỗi ra giao diện
    }
}

/**
 * 🔹 2. Tải các đơn hàng gần đây
 */
async function fetchRecentOrders() {
    const tableBody = document.getElementById("recentOrdersTableBody");
    try {
        // API này bạn sẽ tạo
        const response = await fetch("/api/admin/dashboard/recent-orders?limit=5");
        if (!response.ok) throw new Error("Lỗi tải đơn hàng");

        const orders = await response.json(); // Mong đợi một List<OrderResponseDTO>
		
		console.log (orders);
        
        tableBody.innerHTML = ""; // Xóa dòng "Đang tải..."
        
        if (!orders || orders.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-3">Không có đơn hàng nào gần đây.</td></tr>`;
            return;
        }

        orders.forEach(order => {
            const tr = document.createElement("tr");
            const statusBadge = getStatusBadge(order.status); // Hàm helper
            
            tr.innerHTML = `
                <td>#${order.id}</td>
                <td>${escapeHTML(order.customerName)}</td>
                <td>${formatDateTime(order.createdAt)}</td>
                <td>${statusBadge}</td>
                <td>${formatCurrency(order.totalAmount)}</td>
            `;
            tableBody.appendChild(tr);
        });

    } catch (error) {
        console.error("Error fetching recent orders:", error);
        tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger py-3">Không thể tải đơn hàng.</td></tr>`;
    }
}

/**
 * 🔹 3. Tải dữ liệu và vẽ biểu đồ
 */
async function loadChartData() {
    // Lấy giá trị từ các bộ lọc
    const type = document.getElementById("chartType").value;
    const fromDate = document.getElementById("chartFromDate").value;
    const toDate = document.getElementById("chartToDate").value;

    // Xây dựng URL API dựa trên bộ lọc
    const params = new URLSearchParams({ type });
    if (type === 'custom') {
        if (fromDate) params.append('fromDate', fromDate);
        if (toDate) params.append('toDate', toDate);
    }

	console.log("Đang gọi API chart với URL:", `/api/admin/dashboard/chart?${params.toString()}`);
	
    try {
        // API này bạn sẽ tạo
        const response = await fetch(`/api/admin/dashboard/chart?${params.toString()}`);
        if (!response.ok) throw new Error("Lỗi tải dữ liệu biểu đồ");

        const chartData = await response.json(); // Mong đợi { labels: [], data: [] }
        
        // Vẽ biểu đồ với dữ liệu mới
        renderRevenueChart(chartData);

    } catch (error) {
        console.error("Error fetching chart data:", error);
    }
}

/* ==========================
   VẼ BIỂU ĐỒ (Chart.js)
========================== */

/**
 * 🔹 4. Dùng Chart.js để vẽ biểu đồ
 */
function renderRevenueChart(chartData) {
    const ctx = document.getElementById('revenueChart').getContext('2d');

    // Nếu biểu đồ đã tồn tại, hủy nó đi trước khi vẽ lại
    if (revenueChart) {
        revenueChart.destroy();
    }

    revenueChart = new Chart(ctx, {
        type: 'line', // Loại biểu đồ: đường
        data: {
            labels: chartData.labels, // Mảng các ngày (ví dụ: ["20/10", "21/10"])
            datasets: [{
                label: 'Doanh thu (VND)',
                data: chartData.data, // Mảng doanh thu (ví dụ: [100000, 150000])
                borderColor: 'rgba(0, 123, 255, 1)', // Màu xanh primary
                backgroundColor: 'rgba(0, 123, 255, 0.1)',
                fill: true,
                tension: 0.1 // Làm mượt đường
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        // Định dạng tiền tệ cho trục Y
                        callback: function(value, index, values) {
                            return new Intl.NumberFormat('vi-VN').format(value) + ' đ';
                        }
                    }
                }
            }
        }
    });
}

/* ==========================
   HÀM TIỆN ÍCH (Helpers)
========================== */

/**
 * 🔹 Định dạng tiền tệ VND
 */
function formatCurrency(number) {
    if (typeof number !== 'number') return '0 đ';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(number);
}

/**
 * 🔹 Định dạng ngày giờ
 */
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

/**
 * 🔹 Chống XSS
 */
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

/**
 * 🔹 Tạo badge cho trạng thái đơn hàng
 */
function getStatusBadge(status) {
    status = (status || 'UNKNOWN').toUpperCase();

    switch (status) {
        case 'PENDING':
            // Đổi class thành "status-pending"
            return '<span class="badge status-pending">Chờ xử lý</span>';
        case 'NEW':
            return '<span class="badge status-new">Đơn hàng mới</span>';
        case 'CONFIRMED':
            return '<span class="badge status-confirmed">Đã xác nhận</span>';
        case 'SHIPPING':
            return '<span class="badge status-shipping">Đang giao</span>';
        case 'DELIVERED':
            return '<span class="badge status-delivered">Đã giao</span>'; // Đổi ở đây
        case 'CANCELED':
            return '<span class="badge status-canceled">Đã hủy</span>';
        case 'RETURNED_REFUNDED':
            return '<span class="badge status-refunded">Trả hàng</span>';
        default:
            return `<span class="badge status-unknown">${escapeHTML(status)}</span>`;
    }
}
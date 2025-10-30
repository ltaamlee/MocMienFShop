(() => {
	const fmtMoney = n => (Number(n || 0)).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });

	const grossEl = document.getElementById('grossStat');
	const ordersEl = document.getElementById('ordersStat');
	const avgEl = document.getElementById('avgStat');
	const feeEl = document.getElementById('feeStat');
	const netEl = document.getElementById('netStat');

	const fromInput = document.getElementById('fromDate');
	const toInput = document.getElementById('toDate');
	const applyBtn = document.getElementById('applyBtn');
	const chartMode = document.getElementById('chartMode');
	const yearInput = document.getElementById('yearInput');
	const subtitle = document.getElementById('chartSubtitle');
	const recentTbody = document.getElementById('recentTbody');

	// default: tháng hiện tại
	const now = new Date();
	const yyyy = now.getFullYear();
	const mm = String(now.getMonth() + 1).padStart(2, '0');
	const dd = String(now.getDate()).padStart(2, '0');
	const firstDay = `${yyyy}-${mm}-01`;
	const today = `${yyyy}-${mm}-${dd}`;
	fromInput.value = firstDay;
	toInput.value = today;

	let chart;
	function renderChart(labels, values, label) {
		const ctx = document.getElementById('revenueChart').getContext('2d');
		if (chart) chart.destroy();
		chart = new Chart(ctx, {
			type: 'line',
			data: {
				labels,
				datasets: [{ label, data: values, tension: .25 }]
			},
			options: {
				responsive: true,
				interaction: { mode: 'index', intersect: false },
				scales: { y: { beginAtZero: true } }
			}
		});
	}

	async function loadSummary(from, to) {
		const res = await fetch(`/api/vendor/revenue/summary?from=${from}&to=${to}`);
		const j = await res.json();
		grossEl.textContent = fmtMoney(j.gross);
		ordersEl.textContent = j.orders;
		avgEl.textContent = fmtMoney(j.avgOrder);
		feeEl.textContent = fmtMoney(j.platformFee);
		netEl.textContent = fmtMoney(j.netToStore);
	}

	async function loadDaily(from, to) {
		const res = await fetch(`/api/vendor/revenue/daily?from=${from}&to=${to}`);
		const j = await res.json();
		const labels = j.map(p => new Date(p.date).toLocaleDateString('vi-VN'));
		const values = j.map(p => p.total);
		subtitle.textContent = `Theo ngày: ${from} → ${to}`;
		renderChart(labels, values, 'Doanh thu');
	}

	async function loadMonthly(year) {
		const res = await fetch(`/api/vendor/revenue/monthly?year=${year}`);
		const j = await res.json();
		const labels = j.map(p => `Th ${p.month}`);
		const values = j.map(p => p.total);
		subtitle.textContent = `Theo tháng năm ${year}`;
		renderChart(labels, values, 'Doanh thu');
	}

	async function loadRecent() {
		const res = await fetch(`/api/vendor/revenue/recent?limit=10`);
		const j = await res.json();
		if (!Array.isArray(j) || j.length === 0) {
			recentTbody.innerHTML = `<tr><td colspan="5" class="text-center">Chưa có dữ liệu</td></tr>`;
			return;
		}
		recentTbody.innerHTML = j.map(o => `
      <tr>
        <td>${o.id}</td>
        <td>${new Date(o.createdAt).toLocaleString('vi-VN')}</td>
        <td class="text-right money">${fmtMoney(o.total)}</td>
        <td>${o.status}</td>
        <td>${o.paid ? 'Đã thanh toán' : 'Chưa'}</td>
      </tr>
    `).join('');
	}

	chartMode.addEventListener('change', () => {
		const mode = chartMode.value;
		yearInput.style.display = (mode === 'monthly') ? 'inline-block' : 'none';
	});

	applyBtn.addEventListener('click', async () => {
		const mode = chartMode.value;
		if (mode === 'daily') {
			await Promise.all([
				loadSummary(fromInput.value, toInput.value),
				loadDaily(fromInput.value, toInput.value)
			]);
		} else {
			const y = Number(yearInput.value || new Date().getFullYear());
			await loadMonthly(y);
			// đồng thời update summary theo cả năm
			await loadSummary(`${y}-01-01`, `${y}-12-31`);
		}
		await loadRecent();
	});

	// first load
	(async () => {
		await loadSummary(fromInput.value, toInput.value);
		await loadDaily(fromInput.value, toInput.value);
		await loadRecent();
	})();

})();

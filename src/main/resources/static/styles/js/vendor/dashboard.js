// ===================== DEMO DOANH THU 2024–2025 =====================
const RAW_REVENUE = [
  // 2024
  {month:'01/2024',value: 95},{month:'02/2024',value:102},{month:'03/2024',value:110},
  {month:'04/2024',value:120},{month:'05/2024',value:138},{month:'06/2024',value:145},
  {month:'07/2024',value:152},{month:'08/2024',value:160},{month:'09/2024',value:168},
  {month:'10/2024',value:175},{month:'11/2024',value:182},{month:'12/2024',value:190},
  // 2025
  {month:'01/2025',value:120},{month:'02/2025',value:135},{month:'03/2025',value: 98},
  {month:'04/2025',value:150},{month:'05/2025',value:175},{month:'06/2025',value:162},
  {month:'07/2025',value:190},{month:'08/2025',value:210},{month:'09/2025',value:198},
  {month:'10/2025',value:223},{month:'11/2025',value:200},{month:'12/2025',value:205}
];

// === Zero các tháng tương lai để test (ví dụ hiện tại 10/2025 → 11,12/2025 = 0)
function zeroFutureMonths(list){
  const now = moment().startOf('month');
  return list.map(({month,value})=>{
    const m = moment(month,'MM/YYYY').startOf('month');
    return {month, value: m.isAfter(now) ? 0 : value};
  });
}

// === Bộ DEMO đầy đủ (giữ nguyên các phần khác của cậu)
const DEMO = {
  revenueByMonth: zeroFutureMonths(RAW_REVENUE),

  employeesPie: [
    {label:'Bán hàng',value:5},{label:'Kho',value:3},{label:'Shipper',value:2},
    {label:'Quản lý',value:1},{label:'Khác',value:1}
  ],
  customersByMonth: [
    {month:'01/2025',value:20},{month:'02/2025',value:23},{month:'03/2025',value:18},
    {month:'04/2025',value:26},{month:'05/2025',value:29},{month:'06/2025',value:31},
    {month:'07/2025',value:34},{month:'08/2025',value:39},{month:'09/2025',value:41},
    {month:'10/2025',value:44},{month:'11/2025',value:0},{month:'12/2025',value:0}
  ],
  ordersByDayOct2025: Array.from({length:31},(_,i)=>{
    const d=i+1; const val=Math.max(2,Math.min(18,Math.round(8+6*Math.sin(i/3)+(i%5)-(i%7)/2)));
    return {day:`${String(d).padStart(2,'0')}/10/2025`,value:val};
  }),
  topProducts: [
    { name:'Bó cưới Tulip pastel', unit:'bó',   sold:12, price:1750000, img:'/uploads/products/1761126694834-product10.jpg' },
    { name:'Hộp hoa rainbow',      unit:'hộp',  sold:18, price: 990000, img:'/uploads/products/1761126618487-product9.jpg' },
    { name:'Giỏ cắm chướng hồng',  unit:'giỏ',  sold:20, price: 780000, img:'/uploads/products/1761120567031-product8.jpg' },
    { name:'Kệ khai trương đỏ',    unit:'kệ',   sold: 8, price:2450000, img:'/uploads/products/1761112372655-product5.jpg' },
    { name:'Bó mix vàng cam',      unit:'bó',   sold:22, price: 820000, img:'/uploads/products/1761112408842-product6.webp' },
    { name:'Bình baby khói',       unit:'bình', sold:16, price: 650000, img:'/uploads/products/1761120493443-product6.jpg' }
  ],
  recentOrders: [
    {code:'10245',customer:'Nguyễn Văn A',date:'20/10/2025 14:22',total:1250000,status:'Đã đóng đơn'},
    {code:'10246',customer:'Trần Thị B',  date:'20/10/2025 15:05',total: 820000,status:'Đang xử lý'},
    {code:'10247',customer:'Lê Hoàng C',  date:'21/10/2025 09:18',total:1570000,status:'Đã thanh toán'},
    {code:'10248',customer:'Phạm Minh D', date:'21/10/2025 10:02',total: 640000,status:'Đang giao'},
    {code:'10249',customer:'Võ Gia E',    date:'22/10/2025 08:41',total: 910000,status:'Đã đóng đơn'}
  ]
};

// ===== Rollup helpers (lấy từ revenueByMonth đã zero-future) =====
const monthMap = Object.fromEntries(DEMO.revenueByMonth.map(m => [m.month, m.value]));

function monthsBetween(startStr, endStr){
  const s = moment(startStr,'YYYY-MM-DD');
  const e = moment(endStr,'YYYY-MM-DD');
  const items=[];
  const cur = s.clone().startOf('month');
  const last = e.clone().startOf('month');
  while(cur.isSameOrBefore(last)){ items.push(cur.format('MM/YYYY')); cur.add(1,'month'); }
  return items;
}

function rollupQuarter(){
  const groups = {};
  DEMO.revenueByMonth.forEach(({month,value})=>{
    const [mm,yyyy] = month.split('/');
    const q = Math.floor((+mm-1)/3)+1;
    const key = `${yyyy}-Q${q}`;
    groups[key] = (groups[key]||0) + value;
  });
  return Object.entries(groups).sort((a,b)=>a[0].localeCompare(b[0]))
         .map(([label,value])=>({label,value}));
}
function rollupYear(){
  const groups = {};
  DEMO.revenueByMonth.forEach(({month,value})=>{
    const yyyy = month.split('/')[1];
    groups[yyyy] = (groups[yyyy]||0) + value;
  });
  return Object.entries(groups).sort((a,b)=>a[0].localeCompare(b[0]))
         .map(([label,value])=>({label,value}));
}

// ===== Chart.js (chỉ 1 phiên bản hàm) =====

function makeLineConfig(labels,data,label){
  return {
    type:'line',
    data:{labels,datasets:[{label,data,tension:0.3,fill:false,pointRadius:3,borderWidth:2}]},
    options:{responsive:true,maintainAspectRatio:false,plugins:{legend:{display:true}},
      scales:{x:{grid:{display:false}},y:{beginAtZero:true}}}
  };
}
function makePieConfig(labels,data,label){
  return {
    type:'pie',
    data:{labels,datasets:[{label,data}]},
    options:{responsive:true,maintainAspectRatio:false,plugins:{legend:{position:'right'}}}
  };
}
function destroyIfAny(){ if(chartInstance){ chartInstance.destroy(); chartInstance=null; } }
function setChartTitle(t){ const el=document.getElementById('chartTitle'); if(el) el.textContent=t; }

function updateChart(rangeOrStart, endOpt){
  const ctx=document.getElementById('dashboardChart'); if(!ctx) return;

  if(currentChartType==='revenue'){
    const mode = (['month','quarter','year','custom'].includes(rangeOrStart)) ? rangeOrStart : $('#timeRange').val();

    if(mode==='year'){
      const y = rollupYear();
      setChartTitle('Doanh thu theo năm (triệu VND)');
      destroyIfAny();
      chartInstance=new Chart(ctx, makeLineConfig(y.map(x=>x.label), y.map(x=>x.value), 'Doanh thu/năm'));
      return;
    }
    if(mode==='quarter'){
      const q = rollupQuarter();
      setChartTitle('Doanh thu theo quý (triệu VND)');
      destroyIfAny();
      chartInstance=new Chart(ctx, makeLineConfig(q.map(x=>x.label), q.map(x=>x.value), 'Doanh thu/quý'));
      return;
    }
    if(mode==='custom' && endOpt){
      const labels = monthsBetween(rangeOrStart, endOpt);
      const data = labels.map(lbl=> monthMap[lbl] || 0);
      setChartTitle('Doanh thu (tùy chỉnh)');
      destroyIfAny();
      chartInstance=new Chart(ctx, makeLineConfig(labels, data, 'Doanh thu/tháng'));
      return;
    }
    // default: month
    setChartTitle('Doanh thu theo tháng (triệu VND)');
    destroyIfAny();
    chartInstance=new Chart(ctx, makeLineConfig(
      DEMO.revenueByMonth.map(x=>x.month),
      DEMO.revenueByMonth.map(x=>x.value),
      'Doanh thu/tháng'
    ));
    return;
  }

  if(currentChartType==='employees'){
    setChartTitle('Cơ cấu Nhân viên'); destroyIfAny();
    chartInstance=new Chart(ctx, makePieConfig(DEMO.employeesPie.map(x=>x.label), DEMO.employeesPie.map(x=>x.value), 'Nhân viên'));
  }else if(currentChartType==='customers'){
    setChartTitle('Khách hàng mới theo tháng'); destroyIfAny();
    chartInstance=new Chart(ctx, makeLineConfig(DEMO.customersByMonth.map(x=>x.month), DEMO.customersByMonth.map(x=>x.value), 'Khách hàng mới'));
  }else if(currentChartType==='orders'){
    setChartTitle('Số lượng đơn hàng theo ngày (10/2025)'); destroyIfAny();
    chartInstance=new Chart(ctx, makeLineConfig(DEMO.ordersByDayOct2025.map(x=>x.day), DEMO.ordersByDayOct2025.map(x=>x.value), 'Đơn hàng/ngày'));
  }
}

function showChart(type){
  currentChartType = type;
  const mode = $('#timeRange').val();
  const drp  = $('#customDateRange').val();
  if(type==='revenue' && mode==='custom' && drp){
    const [s,e] = drp.split(' - ');
    updateChart(moment(s,'DD/MM/YYYY').format('YYYY-MM-DD'), moment(e,'DD/MM/YYYY').format('YYYY-MM-DD'));
  }else{
    updateChart(mode);
  }
}
window.showChart = showChart;
window.updateChart = updateChart;

// ===== Boot =====
document.addEventListener('DOMContentLoaded', ()=>{
  renderTopProducts();
  renderRecentOrders();
  showChart('revenue'); // mặc định: theo tháng
});

// ====== Helpers rollup doanh thu ======

function rollupQuarter(){
  // trả về [{label:'2024-Q1', value:...}, ... , {label:'2025-Q4', value:...}]
  const groups = {};
  DEMO.revenueByMonth.forEach(({month, value})=>{
    const [mm, yyyy] = month.split('/'); // 'MM', 'YYYY'
    const q = Math.floor((parseInt(mm,10)-1)/3) + 1;
    const key = `${yyyy}-Q${q}`;
    groups[key] = (groups[key]||0) + value;
  });
  return Object.entries(groups)
    .sort((a,b)=>a[0].localeCompare(b[0]))
    .map(([label,value])=>({label,value}));
}

function rollupYear(){
  const groups = {};
  DEMO.revenueByMonth.forEach(({month, value})=>{
    const yyyy = month.split('/')[1];
    groups[yyyy] = (groups[yyyy]||0) + value;
  });
  return Object.entries(groups)
    .sort((a,b)=>a[0].localeCompare(b[0]))
    .map(([label,value])=>({label,value}));
}


// =========================
// BẢNG ĐƠN HÀNG GẦN ĐÂY
// =========================
function renderRecentOrders() {
	const tbody = document.getElementById('recentOrdersBody');
	if (!tbody) return;
	tbody.innerHTML = DEMO.recentOrders.map(o => `
    <tr>
      <td style="padding:8px;border-bottom:1px solid #f4f4f4;">${o.code}</td>
      <td style="padding:8px;border-bottom:1px solid #f4f4f4;">${o.customer}</td>
      <td style="padding:8px;border-bottom:1px solid #f4f4f4;">${o.date}</td>
      <td style="padding:8px;border-bottom:1px solid #f4f4f4;text-align:right;">${o.total.toLocaleString('vi-VN')}₫</td>
      <td style="padding:8px;border-bottom:1px solid #f4f4f4;text-align:center;">
        <span style="padding:4px 8px;border-radius:999px;background:#f3f6ff;border:1px solid #dfe7ff;">${o.status}</span>
      </td>
    </tr>
  `).join('');
}

// =========================
// CHART.JS (revenue/employee/customer/orders)
// =========================
let currentChartType = 'revenue';
let chartInstance = null;

function makeLineConfig(labels, data, label) {
	return {
		type: 'line',
		data: { labels, datasets: [{ label, data, tension: 0.3, fill: false, pointRadius: 3, borderWidth: 2 }] },
		options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: true } }, scales: { x: { grid: { display: false } }, y: { beginAtZero: true } } }
	};
}
function makePieConfig(labels, data, label) {
	return {
		type: 'pie',
		data: { labels, datasets: [{ label, data }] },
		options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right' } } }
	};
}
function destroyIfAny() { if (chartInstance) { chartInstance.destroy(); chartInstance = null; } }
function setChartTitle(t) { const el = document.getElementById('chartTitle'); if (el) el.textContent = t; }

function updateChart() {
	const ctx = document.getElementById('dashboardChart');
	if (!ctx) return;
	if (currentChartType === 'revenue') {
		setChartTitle('Biểu đồ Doanh thu');
		destroyIfAny();
		chartInstance = new Chart(ctx, makeLineConfig(DEMO.revenueByMonth.map(x => x.month), DEMO.revenueByMonth.map(x => x.value), 'Doanh thu (triệu)'));
	} else if (currentChartType === 'employees') {
		setChartTitle('Cơ cấu Nhân viên (giả lập)');
		destroyIfAny();
		chartInstance = new Chart(ctx, makePieConfig(DEMO.employeesPie.map(x => x.label), DEMO.employeesPie.map(x => x.value), 'Nhân viên'));
	} else if (currentChartType === 'customers') {
		setChartTitle('Khách hàng mới theo tháng ');
		destroyIfAny();
		chartInstance = new Chart(ctx, makeLineConfig(DEMO.customersByMonth.map(x => x.month), DEMO.customersByMonth.map(x => x.value), 'Khách hàng mới'));
	} else if (currentChartType === 'orders') {
		setChartTitle('Số lượng đơn hàng theo ngày (10/2025)');
		destroyIfAny();
		chartInstance = new Chart(ctx, makeLineConfig(DEMO.ordersByDayOct2025.map(x => x.day), DEMO.ordersByDayOct2025.map(x => x.value), 'Đơn hàng/ngày'));
	}
}

// Click vào các thẻ số liệu để đổi chart
function showChart(type) { currentChartType = type; updateChart(); }
window.showChart = showChart; // để HTML gọi được

// =========================
// BOOT
// =========================
document.addEventListener('DOMContentLoaded', () => {
	renderTopProducts();
	renderRecentOrders();
	showChart('revenue'); // chart mặc định
});
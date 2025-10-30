function getCsrf() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    return token && header ? { header, token } : null;
}
async function loadProfile() {
    const res = await fetch('/api/shipper/me');
    if (!res.ok) return;
    const me = await res.json();
    const form = document.getElementById('profileForm');
    form.vehicleNumber.value = me.vehicleNumber || '';
    form.vehicleType.value = me.vehicleType || '';
    form.license.value = me.license || '';
}

async function saveProfile(e) {
    e.preventDefault();
    const form = e.target;
    const body = {
        vehicleNumber: form.vehicleNumber.value,
        vehicleType: form.vehicleType.value,
        license: form.license.value
    };
    const csrf = getCsrf();
    const headers = { 'Content-Type':'application/json' };
    if (csrf) headers[csrf.header] = csrf.token;
    const res = await fetch('/api/shipper/profile', { method: 'PUT', headers, body: JSON.stringify(body) });
    if (!res.ok) { alert('Lưu thất bại'); return; }
    alert('Đã lưu');
}

document.addEventListener('DOMContentLoaded', () => {
    loadProfile();
    document.getElementById('profileForm')?.addEventListener('submit', saveProfile);
});



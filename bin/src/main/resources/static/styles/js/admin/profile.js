// Hiển thị preview khi chọn file
function previewAvatar(event) {
    const file = event.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = e => document.getElementById('avatarPreview').src = e.target.result;
    reader.readAsDataURL(file);
}

// Upload file lên Cloudinary, trả về Promise URL
async function uploadToCloudinary(file) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("folder", "profile/admin");

    const res = await fetch('/api/upload', { method: 'POST', body: formData });
    const data = await res.json();
    if (data.url) return data.url;
    throw new Error(data.error || "Upload thất bại");
}

// Xử lý submit form
document.getElementById('profileForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const fileInput = document.getElementById('file');
    // Nếu chọn file mới, upload trước
    if (fileInput.files.length > 0) {
        try {
            const url = await uploadToCloudinary(fileInput.files[0]);
            document.getElementById('avatarUrl').value = url; // set vào hidden input
        } catch (err) {
            console.error("Upload avatar thất bại:", err);
            alert("Upload avatar thất bại!");
            return;
        }
    }

    // Tạo FormData từ form
    const formData = new FormData(this);

    try {
        const res = await fetch('/admin/profile/update', { method: 'POST', body: formData });
        const data = await res.json();

        if (data.success) {
            alert("Cập nhật profile thành công!");
        } else {
            alert("Cập nhật thất bại: " + data.message);
        }
    } catch (err) {
        console.error("Submit thất bại:", err);
        alert("Cập nhật thất bại!");
    }
});

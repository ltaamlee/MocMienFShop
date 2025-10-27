// Hiển thị preview và upload avatar
function previewAvatar(event) {
    const file = event.target.files[0];
    if (!file) return;

    // Hiển thị preview
    const reader = new FileReader();
    reader.onload = e => document.getElementById('avatarPreview').src = e.target.result;
    reader.readAsDataURL(file);

    // Upload lên Cloudinary (API chung)
    const formData = new FormData();
    formData.append("file", file);
    formData.append("folder", "profile/admin"); // phân folder

    fetch('/api/upload', { method: 'POST', body: formData })
        .then(res => res.json())
        .then(data => {
            if (data.url) {
                document.getElementById('avatarUrl').value = data.url; // gán URL avatar vào hidden input
            } else {
                alert("Upload thất bại: " + (data.error || ""));
            }
        })
        .catch(err => {
            console.error("Lỗi upload:", err);
            alert("Upload thất bại!");
        });
}

// Submit form qua fetch
document.getElementById('profileForm').addEventListener('submit', function(e){
    e.preventDefault();
    const form = this;
    const formData = new FormData(form);

    fetch('/admin/profile/update', {
        method: 'POST',
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            alert("Cập nhật thành công!");
        } else {
            alert("Cập nhật thất bại: " + data.message);
        }
    })
    .catch(err => {
        console.error(err);
        alert("Cập nhật thất bại!");
    });
});

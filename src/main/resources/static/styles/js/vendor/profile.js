// Hiển thị preview khi chọn file
function previewAvatar(event) {
    const file = event.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = e => document.getElementById('avatarPreview').src = e.target.result;
    reader.readAsDataURL(file);
}

// Upload file lên Cloudinary, trả về Promise URL
function uploadToCloudinary(file) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("folder", "profile/vendor"); // folder

    return fetch('/api/upload', { method: 'POST', body: formData })
        .then(res => res.json())
        .then(data => {
            if (data.url) return data.url;
            else throw new Error(data.error || "Upload thất bại");
        });
}

// Xử lý submit form
document.getElementById('profileForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const fileInput = document.getElementById('file');

    // Nếu chọn file mới, upload trước
    if (fileInput.files.length > 0) {
        try {
            const url = await uploadToCloudinary(fileInput.files[0]);
            document.getElementById('avatarUrl').value = url; // set hidden input
        } catch (err) {
            console.error("Upload avatar thất bại:", err);
            alert("Upload avatar thất bại!");
            return; // dừng submit nếu upload fail
        }
    }

    // Tạo FormData từ form
    const formData = new FormData(this);

    // Debug FormData trước khi gửi
    console.log("FormData trước khi gửi:");
    for (let [key, value] of formData.entries()) {
        console.log(key, value);
    }

    try {
        const res = await fetch('/api/vendor/profile/update', { method: 'POST', body: formData });
        const data = await res.json();

        if (data.success) {
            alert("Cập nhật thành công!");
        } else {
            alert("Cập nhật thất bại: " + data.message);
        }
    } catch (err) {
        console.error("Submit thất bại:", err);
        alert("Cập nhật thất bại!");
    }
});

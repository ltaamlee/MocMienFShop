document.addEventListener("DOMContentLoaded", () => {
  const avatarInput = document.getElementById("avatar-input");
  const previewPopup = document.getElementById("preview-popup");
  const previewImage = document.getElementById("preview-image");
  const confirmUpload = document.getElementById("confirm-upload");
  const avatarPreview = document.getElementById("avatar-preview");

  if (!avatarInput) return;

  console.log("avatar.js loaded ✅");

  // 🔹 Khi chọn ảnh
  avatarInput.addEventListener("change", function (e) {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (event) {
        previewImage.src = event.target.result;
        previewPopup.style.display = "flex";
        document.body.style.overflow = "hidden";
      };
      reader.readAsDataURL(file);
    }
  });

  // 🔹 Khi xác nhận upload (AJAX)
  confirmUpload?.addEventListener("click", async function () {
    const file = avatarInput.files[0];
    if (!file) return;

    // 🌀 Thêm hiệu ứng đang tải
    confirmUpload.disabled = true;
    const originalText = confirmUpload.textContent;
    confirmUpload.textContent = "Đang lưu...";
    confirmUpload.classList.add("loading");

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch("/account/avatar/upload", {
        method: "POST",
        body: formData,
      });

      const data = await response.json();

      if (data.url) {
        // 💫 Hiệu ứng fade-out avatar cũ
        avatarPreview.style.opacity = "0.5";

        // Cập nhật ảnh chính
        avatarPreview.src = data.url;
        const headerAvatar = document.querySelector(".avatar-img");
        if (headerAvatar) headerAvatar.src = data.url;

        // 💫 Fade-in lại
        setTimeout(() => {
          avatarPreview.style.opacity = "1";
        }, 150);

        previewPopup.style.display = "none";
        document.body.style.overflow = "auto";
        avatarInput.value = "";
      } else {
        alert(data.error || "Có lỗi khi upload ảnh.");
      }
    } catch (err) {
      console.error(err);
      alert("Không thể upload ảnh. Vui lòng thử lại.");
    } finally {
      // ✅ Khôi phục nút
      confirmUpload.disabled = false;
      confirmUpload.textContent = originalText;
      confirmUpload.classList.remove("loading");
    }
  });

  // 🔹 Đóng popup
  window.closePreviewPopup = function () {
    previewPopup.style.display = "none";
    document.body.style.overflow = "auto";
    avatarInput.value = "";
    previewImage.src = "";
  };
});

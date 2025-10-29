document.addEventListener("DOMContentLoaded", () => {
  const avatarInput = document.getElementById("avatar-input");
  const previewPopup = document.getElementById("preview-popup");
  const previewImage = document.getElementById("preview-image");
  const formUpload = document.querySelector('form[enctype="multipart/form-data"]');
  const confirmUpload = document.getElementById("confirm-upload");

  if (!avatarInput) return;

  console.log("avatar.js loaded ✅");

  avatarInput.addEventListener("change", function (e) {
    const file = e.target.files[0];
    console.log("File selected:", file); // Debug
    if (file) {
      const reader = new FileReader();
      reader.onload = function (event) {
        previewImage.src = event.target.result;
        previewPopup.style.display = "flex";
        document.body.style.overflow = "hidden";
        console.log("Popup hiển thị!");
      };
      reader.readAsDataURL(file);
    }
  });

  confirmUpload?.addEventListener("click", function () {
    previewPopup.style.display = "none";
    document.body.style.overflow = "auto";
    formUpload.submit();
  });

  window.closePreviewPopup = function () {
    previewPopup.style.display = "none";
    document.body.style.overflow = "auto";
    avatarInput.value = "";
    previewImage.src = "";
  };
});


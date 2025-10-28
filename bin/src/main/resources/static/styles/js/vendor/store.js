// store.js — tách riêng hoàn toàn khỏi HTML
console.log("✅ store.js loaded!");
let hasStore = false;

/* ---------- DOM refs ---------- */
const modal = document.getElementById("storeModal");
const openBtn = document.getElementById("openRegisterModalBtn");
const closeBtn = document.getElementById("closeModalBtn");
const cancelBtn = document.getElementById("cancelModalBtn");

const submitBtn = document.getElementById("submitRegisterBtn");
const submitText = document.querySelector("#submitRegisterBtn .btn-text");
const submitLoading = document.getElementById("submitLoading");

const vendorIdInput = document.getElementById("vendorId");

const storeNameInput = document.getElementById("storeName");
const addressInput = document.getElementById("address");
const avatarFileInput = document.getElementById("avatarFile");
const coverFileInput = document.getElementById("coverFile");
const featureFilesInput = document.getElementById("featureFiles");
const isOpenToggle = document.getElementById("isOpenToggle");

const emptyStateBlock = document.getElementById("emptyStateBlock");
const tableBody = document.getElementById("storeTableBody");

const statWallet = document.getElementById("statWallet");
const statPoint = document.getElementById("statPoint");
const statRating = document.getElementById("statRating");
const statStatus = document.getElementById("statStatus");

/* ---------- Modal helpers ---------- */
function openModal() {
	modal.classList.add("show");
	modal.setAttribute("aria-hidden", "false");
	applyRegisterLock(); // khóa/mở nút theo hasStore mỗi lần mở
}
function closeModal() {
	modal.classList.remove("show");
	modal.setAttribute("aria-hidden", "true");
}
// --------- KHÓA / MỞ NÚT SUBMIT KHI ĐÃ CÓ SHOP ----------
function applyRegisterLock() {
	const note = document.getElementById("registerLockNote");
	if (hasStore) {
		submitBtn.disabled = true;
		submitBtn.classList.add("is-disabled");
		if (note) {
			note.style.display = "block";
			note.textContent = "Bạn đã có cửa hàng. Không thể gửi đăng ký mới.";
		}
	} else {
		submitBtn.disabled = false;
		submitBtn.classList.remove("is-disabled");
		if (note) note.style.display = "none";
	}
}

openBtn?.addEventListener("click", openModal);
closeBtn?.addEventListener("click", closeModal);
cancelBtn?.addEventListener("click", closeModal);

window.addEventListener("click", (e) => {
	if (e.target === modal) closeModal();
});

/* ---------- Upload ảnh (dùng UploadController có sẵn) ---------- */
async function uploadImage(file, folder = "store") {
	if (!file) return null;
	const formData = new FormData();
	formData.append("file", file);
	formData.append("folder", folder);

	const res = await fetch("/api/upload", { method: "POST", body: formData });
	if (!res.ok) {
		console.error("Upload fail", res.status);
		return null;
	}
	const data = await res.json();
	return data?.url || null;
}

async function loadMyStore() {
	const res = await fetch(`/api/vendor/store/me`);
	if (!res.ok) { console.error("loadMyStore fail"); return; }
	const data = await res.json();

	if (Array.isArray(data) && data.length === 0) {
		// chưa có shop
		hasStore = false;
		emptyStateBlock.style.display = "block";
		tableBody.innerHTML = "";
		statWallet.textContent = "0";
		statPoint.textContent = "0";
		statRating.textContent = "0.0";
		statStatus.textContent = "Chưa đăng ký";
		applyRegisterLock();
		return;
	}

	// đã có shop
	hasStore = true;
	emptyStateBlock.style.display = "none";
	renderStore(data);
	applyRegisterLock();
}

// --- trong submit handler: nếu có shop thì chặn ---

/* ---------- Submit đăng ký ---------- */
submitBtn?.addEventListener("click", async () => {
	// UI loading
	submitBtn.disabled = true;
	submitLoading.classList.remove("d-none");
	submitText.textContent = "Đang gửi...";

	try {
		const vendorId = vendorIdInput?.value?.trim();
		const storeName = storeNameInput.value.trim();
		const address = addressInput.value.trim();

		if (!storeName || !address) {
			alert("Vui lòng nhập Tên cửa hàng và Địa chỉ.");
			return;
		}

		// Upload ảnh
		const avatarUrl = await uploadImage(avatarFileInput.files[0], "store/avatar");
		const coverUrl = await uploadImage(coverFileInput.files[0], "store/cover");

		const featureUrls = [];
		const featureFiles = Array.from(featureFilesInput.files || []);
		for (const f of featureFiles) {
			const url = await uploadImage(f, "store/feature");
			if (url) featureUrls.push(url);
		}

		const payload = {
			vendorId: vendorId ? parseInt(vendorId) : null,
			storeName,
			address,
			avatar: avatarUrl,
			cover: coverUrl,
			featureImages: featureUrls,
			isOpen: isOpenToggle.checked
		};

		const res = await fetch("/api/vendor/store/register", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(payload)
		});

		if (!res.ok) {
			const msg = await res.text();
			alert("Đăng ký thất bại: " + msg);
			return;
		}

		const created = await res.json();
		renderStore(created);
		closeModal();
	} catch (err) {
		console.error(err);
		alert("Có lỗi xảy ra khi đăng ký shop.");
	} finally {
		submitBtn.disabled = false;
		submitLoading.classList.add("d-none");
		submitText.textContent = "Gửi đăng ký";
	}
});


/* ---------- Render ---------- */
function renderStore(store) {
  const isActive = Boolean(store.active ?? store.isActive);
  const isOpen   = Boolean(store.open   ?? store.isOpen);

  // cập nhật stats
  statWallet.textContent = formatMoney(store.eWallet || 0);
  statPoint.textContent  = store.point ?? 0;
  statRating.textContent = store.rating ?? "0.0";
  statStatus.textContent = isActive ? (isOpen ? "Đang mở" : "Tạm đóng") : "Chờ duyệt";

  // badge & toggle
  const badge = isActive
    ? `<span class="badge badge-active"><i class="fa fa-circle"></i> Hoạt động</span>`
    : `<span class="badge badge-pending"><i class="fa fa-clock"></i> Chờ duyệt</span>`;

  const openToggle = `
    <label class="switch" title="${isActive ? '' : 'Chưa được duyệt'}">
      <input type="checkbox"
             data-role="open-toggle"
             ${isOpen ? 'checked' : ''}
             ${isActive ? '' : 'disabled'}>
      <span class="slider"></span>
    </label>
  `;

  // build 1 lần toàn bộ row
  const rowHtml = `
    <tr>
      <td>
        <div class="shop-info">
          <div class="shop-logo"
               style="background-image:url('${store.avatar ?? ""}'); background-size:cover; background-position:center;">
            ${!store.avatar ? (store.storeName ? store.storeName.charAt(0).toUpperCase() : "S") : ""}
          </div>
          <div class="shop-details">
            <h4>${store.storeName ?? "-"}</h4>
            <p>${store.address ?? ""}</p>
          </div>
        </div>
      </td>

      <td>
        <div class="owner-info">
          <div class="owner-avatar"><i class="fa fa-user"></i></div>
          <div class="owner-details">
            <h5>${store.ownerName ?? "-"}</h5>
            <p>Chủ shop</p>
          </div>
        </div>
      </td>

      <td class="contact-info">
        <p><i class="fa fa-phone"></i> <span>${store.ownerPhone ?? "-"}</span></p>
        <p><i class="fa fa-envelope"></i> <span>${store.ownerEmail ?? "-"}</span></p>
      </td>

      <td>
        <div class="status-cell">
          <span class="badge badge-view">${store.levelDisplayName ?? "NEW"}</span>
          <small>Giảm giá: ${store.levelDiscount ?? 0}%</small>
        </div>
      </td>

      <td>
        <div class="status-cell">
          <span>${store.point ?? 0} điểm</span>
          <span>${formatMoney(store.eWallet || 0)} đ</span>
        </div>
      </td>

      <td>${badge}</td>
      <td class="toggle-cell">${openToggle}</td>
    </tr>
  `;

  // gán vào DOM 1 lần
  tableBody.innerHTML = rowHtml;

  // rồi mới bind sự kiện toggle
  bindOpenToggle();
}


// gắn sự kiện cho công tắc "Mở cửa"
function bindOpenToggle() {
	const input = document.querySelector('#storeTableBody input[data-role="open-toggle"]');
	if (!input) return;

	input.addEventListener('change', async (e) => {
		const checked = e.target.checked;

		// khóa tạm để tránh spam
		e.target.disabled = true;

		try {
			const res = await fetch('/api/vendor/store/open', {
				method: 'PATCH',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ open: checked })
			});

			if (!res.ok) {
				const msg = await res.text();
				alert('Cập nhật trạng thái thất bại: ' + msg);
				e.target.checked = !checked; // revert
				return;
			}

			const updated = await res.json();
			renderStore(updated); // vẽ lại toàn bộ row + stats
		} catch (err) {
			console.error(err);
			alert('Có lỗi mạng khi cập nhật.');
			e.target.checked = !checked; // revert
		} finally {
			e.target.disabled = false;
		}
	});
}



/* ---------- Helpers ---------- */
function formatMoney(num) {
	const n = parseFloat(num || 0);
	return n.toLocaleString("vi-VN", { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

/* ---------- Init ---------- */
document.addEventListener("DOMContentLoaded", loadMyStore);

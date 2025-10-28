document.addEventListener("DOMContentLoaded", () => {
  // ================================
  // 🏠 POPUP THÊM / SỬA ĐỊA CHỈ
  // ================================
  const modal = document.getElementById("addressModal");
  const form = document.getElementById("addressForm");
  const title = document.getElementById("modalTitle");
  const cancelBtn = document.getElementById("cancelBtn");

  const id = document.getElementById("id");
  const fullName = document.getElementById("fullName");
  const phone = document.getElementById("phone");
  const line = document.getElementById("line");
  const ward = document.getElementById("ward");
  const district = document.getElementById("district");
  const province = document.getElementById("province");
  const isDefault = document.getElementById("isDefault");

  // 👉 Nút "Thêm địa chỉ mới"
  const addBtn = document.querySelector(".btn-add-address");
  if (addBtn) {
    addBtn.addEventListener("click", (e) => {
      e.preventDefault();
      title.textContent = "Thêm địa chỉ mới";
      form.reset();
      id.value = "";
      modal.classList.remove("hidden");
    });
  }

  // 👉 Nút "Cập nhật"
  document.querySelectorAll(".edit-address").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      title.textContent = "Cập nhật địa chỉ";
      id.value = btn.dataset.id || "";
      fullName.value = btn.dataset.fullname || "";
      phone.value = btn.dataset.phone || "";
      line.value = btn.dataset.line || "";
      ward.value = btn.dataset.ward || "";
      district.value = btn.dataset.district || "";
      province.value = btn.dataset.province || "";
      isDefault.checked = btn.dataset.isdefault === "true";
      modal.classList.remove("hidden");
    });
  });

  // 👉 Đóng popup
  cancelBtn.addEventListener("click", () => modal.classList.add("hidden"));
  modal.addEventListener("click", (e) => {
    if (e.target === modal) modal.classList.add("hidden");
  });

  // ================================
  // 📦 GỬI FORM & CẬP NHẬT LẠI TRANG
  // ================================
  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const formData = new FormData(form);
    fetch(form.action, {
      method: "POST",
      body: formData,
    })
      .then((res) => {
        if (!res.ok) throw new Error("Không thể lưu địa chỉ!");
        return res.text();
      })
      .then(() => {
        modal.classList.add("hidden");
        window.location.reload(); // ✅ reload để cập nhật danh sách
      })
      .catch((err) => {
        alert(err.message);
      });
  });

  // ================================
  // 📍 CHỌN ĐỊA CHỈ 3 CẤP
  // ================================
  const popup = document.getElementById("addressPopup");
  const provinceList = document.getElementById("provinceList");
  const districtList = document.getElementById("districtList");
  const wardList = document.getElementById("wardList");
  const tabs = document.querySelectorAll(".address-tabs .tab");
  const searchBox = document.getElementById("searchBox");

  let selectedProvince = null;
  let selectedDistrict = null;
  let selectedWard = null;

  // 🔹 Mở popup
  const openBtn = document.getElementById("openAddressPopup");
  if (openBtn) {
    openBtn.addEventListener("click", (e) => {
      e.preventDefault();
      popup.classList.remove("hidden");
      tabs.forEach((t) => t.classList.remove("active"));
      tabs[0].classList.add("active");
      document.querySelectorAll(".address-list").forEach((l) => l.classList.remove("active"));
      provinceList.classList.add("active");
      loadProvinces();
    });
  }

  // 🔹 Đóng popup
  document.getElementById("cancelPopup").addEventListener("click", () => {
    popup.classList.add("hidden");
  });

  // 🔹 Xác nhận chọn
  document.getElementById("confirmPopup").addEventListener("click", () => {
    if (!selectedProvince || !selectedDistrict || !selectedWard) {
      alert("Vui lòng chọn đầy đủ Tỉnh, Huyện, Xã");
      return;
    }
    province.value = selectedProvince.name;
    district.value = selectedDistrict.name;
    ward.value = selectedWard.name;
    popup.classList.add("hidden");
  });

  // 🔹 Chuyển tab
  tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
      tabs.forEach((t) => t.classList.remove("active"));
      tab.classList.add("active");
      document.querySelectorAll(".address-list").forEach((l) => l.classList.remove("active"));
      document.getElementById(tab.dataset.tab + "List").classList.add("active");
      searchBox.value = ""; // reset tìm kiếm
    });
  });

  // ================================
  // 🔍 TÌM KIẾM (API v1)
  // ================================

  searchBox.addEventListener("input", (e) => {
    const keyword = e.target.value.trim();
    if (keyword.length < 2) return;

    const activeTab = document.querySelector(".address-tabs .tab.active").dataset.tab;
    let url = "";

    if (activeTab === "province")
      url = `https://provinces.open-api.vn/api/v1/p/search/?q=${encodeURIComponent(keyword)}`;
    else if (activeTab === "district" && selectedProvince)
      url = `https://provinces.open-api.vn/api/v1/d/search/?q=${encodeURIComponent(keyword)}`;
    else if (activeTab === "ward" && selectedDistrict)
      url = `https://provinces.open-api.vn/api/v1/w/search/?q=${encodeURIComponent(keyword)}`;

    if (!url) return;

    const activeList = document.querySelector(".address-list.active");
    activeList.innerHTML = "<li>Đang tìm kiếm...</li>";

    fetch(url)
      .then((res) => res.json())
      .then((data) => {
        const results = data.results || [];
        if (results.length === 0) {
          activeList.innerHTML = "<li>Không tìm thấy kết quả</li>";
          return;
        }
        if (activeTab === "province") showProvinces(results);
        else if (activeTab === "district") showDistricts(results);
        else if (activeTab === "ward") showWards(results);
      })
      .catch(() => {
        activeList.innerHTML = "<li>Lỗi tải dữ liệu</li>";
      });
  });

  // ================================
  // ⚙️ LOAD DỮ LIỆU
  // ================================
  function loadProvinces() {
    provinceList.innerHTML = "<li>Đang tải...</li>";
    fetch("https://provinces.open-api.vn/api/?depth=1")
      .then((res) => res.json())
      .then((data) => showProvinces(data))
      .catch(() => (provinceList.innerHTML = "<li>Lỗi tải dữ liệu</li>"));
  }

  function showProvinces(list) {
    provinceList.innerHTML = "";
    list.forEach((p) => {
      const li = document.createElement("li");
      li.textContent = p.name;
      li.onclick = () => selectProvince(p);
      provinceList.appendChild(li);
    });
  }

  function selectProvince(p) {
    selectedProvince = p;
    selectedDistrict = null;
    selectedWard = null;
    tabs[1].click();
    districtList.innerHTML = "<li>Đang tải...</li>";
    fetch(`https://provinces.open-api.vn/api/p/${p.code}?depth=2`)
      .then((res) => res.json())
      .then((data) => showDistricts(data.districts))
      .catch(() => (districtList.innerHTML = "<li>Lỗi tải dữ liệu</li>"));
  }

  function showDistricts(list) {
    districtList.innerHTML = "";
    list.forEach((d) => {
      const li = document.createElement("li");
      li.textContent = d.name;
      li.onclick = () => selectDistrict(d);
      districtList.appendChild(li);
    });
  }

  function selectDistrict(d) {
    selectedDistrict = d;
    selectedWard = null;
    tabs[2].click();
    wardList.innerHTML = "<li>Đang tải...</li>";
    fetch(`https://provinces.open-api.vn/api/d/${d.code}?depth=2`)
      .then((res) => res.json())
      .then((data) => showWards(data.wards))
      .catch(() => (wardList.innerHTML = "<li>Lỗi tải dữ liệu</li>"));
  }

  function showWards(list) {
    wardList.innerHTML = "";
    list.forEach((w) => {
      const li = document.createElement("li");
      li.textContent = w.name;
      li.onclick = () => {
        selectedWard = w;
        wardList.querySelectorAll("li").forEach((x) => x.classList.remove("active"));
        li.classList.add("active");
      };
      wardList.appendChild(li);
    });
  }
});

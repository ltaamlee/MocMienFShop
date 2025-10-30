document.addEventListener("DOMContentLoaded", () => {
  // ---------------------------
  // 🧭 PHẦN 1: XỬ LÝ CHUNG
  // ---------------------------
  const form = document.getElementById('checkoutForm');

  // ---------------------------
  // 💳 PHẦN 2: CHỌN PHƯƠNG THỨC THANH TOÁN
  // ---------------------------
  const radios = document.querySelectorAll('input[name="paymentMethod"]');
  const paymentInput = document.getElementById('paymentMethodInput');
  const qrBox = document.getElementById('qrBox');

  radios.forEach(radio => {
    radio.addEventListener('change', (e) => {
      const selectedValue = e.target.value;
      paymentInput.value = selectedValue;

      // đổi style active
      document.querySelectorAll('.payment-tab').forEach(tab => tab.classList.remove('active'));
      e.target.closest('.payment-tab').classList.add('active');

      if (qrBox) qrBox.classList.add('hidden');
    });
  });

  // ---------------------------
  // 📦 PHẦN 3: CHỌN ĐỊA CHỈ CÓ SẴN
  // ---------------------------
  const changeBtn = document.querySelector('.change-btn');
  const addressModal = document.getElementById('addressModal');
  const addAddressModal = document.getElementById('addAddressModal');
  const cancelAddressBtn = document.getElementById('cancelAddressBtn');
  const confirmAddressBtn = document.getElementById('confirmAddressBtn');

  if (changeBtn) {
    changeBtn.addEventListener('click', (e) => {
      e.preventDefault();
      addressModal.classList.remove('hidden');
    });
  }

  if (cancelAddressBtn) {
    cancelAddressBtn.addEventListener('click', () => {
      addressModal.classList.add('hidden');
    });
  }

  if (confirmAddressBtn) {
    confirmAddressBtn.addEventListener('click', () => {
      const selected = document.querySelector('input[name="selectedAddress"]:checked');
      if (!selected) {
        alert("Vui lòng chọn một địa chỉ giao hàng!");
        return;
      }

      const li = selected.closest('li');
      const name = li.querySelector('strong').textContent;
      const phone = li.querySelector('span').textContent;
      const addr = li.querySelector('div span:last-child').textContent;

      // cập nhật hiển thị
      document.getElementById('selectedName').textContent = name;
      document.getElementById('selectedPhone').textContent = phone;
      document.getElementById('selectedAddress').textContent = addr;

      // cập nhật input ẩn trong form
      form.querySelector('input[name="receiver"]').value = name;
      form.querySelector('input[name="phone"]').value = phone;
      form.querySelector('input[name="address"]').value = addr;

      addressModal.classList.add('hidden');
    });
  }

  // ---------------------------
  // 🏠 PHẦN 4: THÊM ĐỊA CHỈ MỚI
  // ---------------------------
  const openAddAddressBtn = document.getElementById('openAddAddressBtn');
  const cancelAddAddressBtn = document.getElementById('cancelAddAddressBtn');
  const addAddressForm = document.getElementById('addAddressForm');
  

  if (openAddAddressBtn) {
    openAddAddressBtn.addEventListener('click', () => {
      addressModal.classList.add('hidden');
      addAddressModal.classList.remove('hidden');
    });
  }

  if (cancelAddAddressBtn) {
    cancelAddAddressBtn.addEventListener('click', () => {
      addAddressModal.classList.add('hidden');
      addressModal.classList.remove('hidden');
    });
  }

  if (addAddressForm) {
    addAddressForm.addEventListener('submit', async (e) => {
      e.preventDefault();

      const newFullName = document.getElementById('newFullName').value.trim();
      const newPhone = document.getElementById('newPhone').value.trim();
      const line = document.getElementById('newDetailAddress').value.trim();
      const province = document.getElementById('province').value;
      const district = document.getElementById('district').value;
      const ward = document.getElementById('ward').value;
      const isDefault = document.getElementById('isDefault')?.checked ?? true;

      if (!newFullName || !newPhone || !line || province === "Chọn Tỉnh/TP" || district === "Chọn Quận/Huyện" || ward === "Chọn Phường/Xã") {
        alert("⚠️ Vui lòng điền đầy đủ thông tin địa chỉ!");
        return;
      }

      try {
        const response = await fetch('/account/address/add-ajax', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            fullName: newFullName,
            phone: newPhone,
            line: line,
            ward: ward,
            district: district,
            province: province,
            isDefault: isDefault
          })
        });

        if (!response.ok) throw new Error("Không thể thêm địa chỉ mới!");

        const newAddress = await response.json();
        const addressText = `${newAddress.line}, ${newAddress.ward}, ${newAddress.district}, ${newAddress.province}`;

        // cập nhật hiển thị
        document.getElementById('selectedName').textContent = newAddress.fullName;
        document.getElementById('selectedPhone').textContent = newAddress.phone;
        document.getElementById('selectedAddress').textContent = addressText;

        // cập nhật input ẩn trong form
        form.querySelector('input[name="receiver"]').value = newAddress.fullName;
        form.querySelector('input[name="phone"]').value = newAddress.phone;
        form.querySelector('input[name="address"]').value = addressText;

        addAddressModal.classList.add('hidden');
        alert("✅ Đã thêm địa chỉ mới thành công!");
      } catch (err) {
        console.error(err);
        alert("❌ Lỗi khi thêm địa chỉ. Vui lòng thử lại!");
      }
    });
  }

  // ================================
  // 📍 CHỌN ĐỊA CHỈ 3 CẤP (API tỉnh/huyện/xã Việt Nam)
  // ================================
  const provinceSelect = document.getElementById("province");
  const districtSelect = document.getElementById("district");
  const wardSelect = document.getElementById("ward");

  // 🔹 Load danh sách tỉnh khi mở form
  if (provinceSelect) {
    fetch("https://provinces.open-api.vn/api/?depth=1")
      .then(res => res.json())
      .then(data => {
        data.forEach(p => {
          const opt = document.createElement("option");
          opt.value = p.name;
          opt.textContent = p.name;
          opt.dataset.code = p.code;
          provinceSelect.appendChild(opt);
        });
      })
      .catch(() => {
        provinceSelect.innerHTML = "<option>Lỗi tải Tỉnh/TP</option>";
      });
  }

  // 🔹 Khi chọn tỉnh -> load huyện
  if (provinceSelect && districtSelect) {
    provinceSelect.addEventListener("change", (e) => {
      const code = e.target.selectedOptions[0].dataset.code;
      districtSelect.innerHTML = "<option>Đang tải...</option>";
      wardSelect.innerHTML = "<option>Chọn Phường/Xã</option>";

      if (!code) return;
      fetch(`https://provinces.open-api.vn/api/p/${code}?depth=2`)
        .then(res => res.json())
        .then(data => {
          districtSelect.innerHTML = "<option>Chọn Quận/Huyện</option>";
          data.districts.forEach(d => {
            const opt = document.createElement("option");
            opt.value = d.name;
            opt.textContent = d.name;
            opt.dataset.code = d.code;
            districtSelect.appendChild(opt);
          });
        })
        .catch(() => {
          districtSelect.innerHTML = "<option>Lỗi tải Quận/Huyện</option>";
        });
    });
  }

  // 🔹 Khi chọn huyện -> load xã
  if (districtSelect && wardSelect) {
    districtSelect.addEventListener("change", (e) => {
      const code = e.target.selectedOptions[0].dataset.code;
      wardSelect.innerHTML = "<option>Đang tải...</option>";

      if (!code) return;
      fetch(`https://provinces.open-api.vn/api/d/${code}?depth=2`)
        .then(res => res.json())
        .then(data => {
          wardSelect.innerHTML = "<option>Chọn Phường/Xã</option>";
          data.wards.forEach(w => {
            const opt = document.createElement("option");
            opt.value = w.name;
            opt.textContent = w.name;
            wardSelect.appendChild(opt);
          });
        })
        .catch(() => {
          wardSelect.innerHTML = "<option>Lỗi tải Phường/Xã</option>";
        });
    });
  }

});

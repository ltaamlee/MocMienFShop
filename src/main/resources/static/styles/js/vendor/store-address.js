document.addEventListener("DOMContentLoaded", () => {
  const provinceSelect = document.getElementById("provinceSelect");
  const districtSelect = document.getElementById("districtSelect");
  const wardSelect = document.getElementById("wardSelect");

  if (!provinceSelect || !districtSelect || !wardSelect) return;

  function clearSelect(select, placeholder) {
    select.innerHTML = "";
    const opt = document.createElement("option");
    opt.value = "";
    opt.textContent = placeholder;
    select.appendChild(opt);
  }

  function enable(select, enabled) {
    select.disabled = !enabled;
  }

  function loadProvinces() {
    clearSelect(provinceSelect, "Tỉnh/Thành phố");
    enable(districtSelect, false); clearSelect(districtSelect, "Quận/Huyện");
    enable(wardSelect, false); clearSelect(wardSelect, "Phường/Xã");
    fetch("https://provinces.open-api.vn/api/?depth=1")
      .then(res => res.json())
      .then(list => {
        list.forEach(p => {
          const opt = document.createElement("option");
          opt.value = p.code;
          opt.textContent = p.name;
          provinceSelect.appendChild(opt);
        });
      });
  }

  function loadDistricts(provinceCode) {
    clearSelect(districtSelect, "Quận/Huyện");
    enable(districtSelect, true);
    enable(wardSelect, false); clearSelect(wardSelect, "Phường/Xã");
    fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`)
      .then(res => res.json())
      .then(data => {
        (data.districts || []).forEach(d => {
          const opt = document.createElement("option");
          opt.value = d.code; opt.textContent = d.name;
          districtSelect.appendChild(opt);
        });
      });
  }

  function loadWards(districtCode) {
    clearSelect(wardSelect, "Phường/Xã");
    enable(wardSelect, true);
    fetch(`https://provinces.open-api.vn/api/d/${districtCode}?depth=2`)
      .then(res => res.json())
      .then(data => {
        (data.wards || []).forEach(w => {
          const opt = document.createElement("option");
          opt.value = w.code; opt.textContent = w.name;
          wardSelect.appendChild(opt);
        });
      });
  }

  provinceSelect.addEventListener("change", () => {
    const code = provinceSelect.value; if (!code) { loadProvinces(); return; }
    loadDistricts(code);
  });
  districtSelect.addEventListener("change", () => {
    const code = districtSelect.value; if (!code) { clearSelect(wardSelect, "Phường/Xã"); enable(wardSelect, false); return; }
    loadWards(code);
  });

  loadProvinces();
});



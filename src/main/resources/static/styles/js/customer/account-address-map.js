
(function(){
  let map, marker, autocomplete, mapsReady = false, usingLeaflet = false;

  function setAddressFields(address, ward, district, province) {
    const lineInput = document.getElementById('line');
    const wardInput = document.getElementById('ward');
    const districtInput = document.getElementById('district');
    const provinceInput = document.getElementById('province');
    if(lineInput) lineInput.value = address || '';
    if(wardInput) wardInput.value = ward || '';
    if(districtInput) districtInput.value = district || '';
    if(provinceInput) provinceInput.value = province || '';
  }

  // ---- GOOGLE REVERSE GEOCODE ----
  function googleReverseGeocode(lat, lng) {
    if(!window.google || !google.maps) return;
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({location: {lat, lng}}, function(results, status) {
      if(status === 'OK' && results && results.length) {
        // Ưu tiên lấy address chuẩn nhất
        setAddressFromGoogleComponents(results[0]);
      }
    });
  }
  function extractVNAddressParts(addressComponents) {
    let province = "", district = "", ward = "", line = "";
  
    function find(types) {
      return addressComponents.find(c => types.some(t => c.types.includes(t)));
    }
  
    // Province
    let provinceComp = find(["administrative_area_level_1"]);
    if (provinceComp) province = provinceComp.long_name;
    // District/City
    let districtComp = find([
      "administrative_area_level_2", // Quận/Huyện thông thường
      "locality" // TP Thủ Đức, Thành phố thuộc tỉnh...
    ]);
    if (districtComp) district = districtComp.long_name;
    // Ward/Commune
    let wardComp = find(["sublocality_level_1", "administrative_area_level_3"]);
    if (wardComp) ward = wardComp.long_name;
  
    // Chống lặp: district == province thì district để trống
    if (district && province && district === province) district = "";
    if (ward && district && ward === district) ward = "";
  
    // Lấy số nhà, tên đường
    let street = find(["route"]);
    let street_number = find(["street_number"]);
    let premise = find(["premise"]);
    let sublocality2 = find(["sublocality_level_2"]);
    line = "";
    if (street && street_number) line = street_number.long_name + " " + street.long_name;
    else if (street) line = street.long_name;
    else if (premise) line = premise.long_name;
    else if (sublocality2) line = sublocality2.long_name;
  
    // Nếu vẫn chưa có line thì lấy formatted_address trừ phần ward, district, province ở cuối
    // Chỉ lấy phần trước dấu phẩy đầu tiên trong ba chuỗi này (nếu xuất hiện ở cuối)
    if (!line && addressComponents.formatted_address) {
      let addressStr = addressComponents.formatted_address;
      if (province) addressStr = addressStr.replace(new RegExp(",?\\s*"+province+"$", "g"), "");
      if (district) addressStr = addressStr.replace(new RegExp(",?\\s*"+district+"$", "g"), "");
      if (ward) addressStr = addressStr.replace(new RegExp(",?\\s*"+ward+"$", "g"), "");
      line = addressStr.trim();
    }
  
    return { line, ward, district, province };
  }
  
  function setAddressFromGoogleComponents(place) {
    // Chuyển đổi cho address_components nhưng truyền cả formatted_address vào nếu cần
    const comps = place.address_components || [];
    comps.formatted_address = place.formatted_address || "";
    const parts = extractVNAddressParts(comps);
    setAddressFields(parts.line, parts.ward, parts.district, parts.province);
  }

  // ---- NOMINATIM REVERSE ----
  function osmReverseGeocode(lat, lng) {
    fetch(`https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lng}&format=json&accept-language=vi-VN`)
      .then(r=>r.json())
      .then(data => {
        if (!data.address) return;
		const a = data.address;
		let line = a.house_number ? `${a.house_number} ${a.road || ''}` : (a.road || '');

		const ward = a.suburb || a.village || a.town || a.neighbourhood || '';

		let district = a.city_district || a.district || a.county || a.city || '';
		let province = a.state || a.region || a.province || '';

		// Nếu district = province (ví dụ "Thành phố Dĩ An" / "Thành phố Hồ Chí Minh") → sửa lại
		if (district === province) {
		  // Với Việt Nam, nếu có city thì dùng làm district
		  if (a.city && a.city !== province) district = a.city;
		  else district = '';
		}

		// Loại trùng cấp thành phố
		if (ward && ward === district) ward = '';
		if (district && district === province) district = '';

		setAddressFields(line, ward, district, province);
      });
  }

  function initMap() {
    const mapEl = document.getElementById('addressMap');
    if (!mapEl || !window.google || !google.maps) return;

    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');
    const lineInput = document.getElementById('line');
    const provinceInput = document.getElementById('province');
    const districtInput = document.getElementById('district');
    const wardInput = document.getElementById('ward');
    const initial = { lat: 10.776889, lng: 106.700806 }; // HCM center default
    const position = (
      latInput && lngInput && latInput.value && lngInput.value
    ) ? { lat: parseFloat(latInput.value), lng: parseFloat(lngInput.value) } : initial;

    map = new google.maps.Map(mapEl, {
      center: position,
      zoom: 14,
    });
    marker = new google.maps.Marker({
      position,
      map,
      draggable: true,
    });
    const updateLatLng = (pos) => {
      if (latInput) latInput.value = pos.lat().toFixed(7);
      if (lngInput) lngInput.value = pos.lng().toFixed(7);
    };
    // Drag or click on map
    google.maps.event.addListener(marker, 'dragend', function() {
      const pos = marker.getPosition();
      updateLatLng(pos);
      googleReverseGeocode(pos.lat(), pos.lng());
    });
    map.addListener('click', function(e){
      marker.setPosition(e.latLng);
      updateLatLng(e.latLng);
      googleReverseGeocode(e.latLng.lat(), e.latLng.lng());
    });
    // Trigger resize + center after modal shows
    setTimeout(() => {
      google.maps.event.trigger(map, 'resize');
      map.setCenter(marker.getPosition());
    }, 100);
    // Places autocomplete cho input line
    if (lineInput) {
      autocomplete = new google.maps.places.Autocomplete(lineInput, {
        componentRestrictions: { country: 'vn' },
        fields: ['geometry', 'address_components', 'formatted_address']
      });
      autocomplete.addListener('place_changed', function() {
        const place = autocomplete.getPlace();
        if (!place.geometry) return;
        const loc = place.geometry.location;
        map.panTo(loc);
        marker.setPosition(loc);
        updateLatLng(loc);
        setAddressFromGoogleComponents(place);
      });
    }
  }
  // --- LEAFLET (OSM) ---
  function initLeaflet() {
    const mapEl = document.getElementById('addressMap');
    if (!mapEl || typeof L === 'undefined') return;
    usingLeaflet = true;

    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');
    const position = (latInput && lngInput && latInput.value && lngInput.value)
      ? [parseFloat(latInput.value), parseFloat(lngInput.value)]
      : [10.776889, 106.700806];

    map = L.map(mapEl).setView(position, 14);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap'
    }).addTo(map);

    marker = L.marker(position, { draggable: true }).addTo(map);
    const updateLatLng = (lat, lng) => {
      if (latInput) latInput.value = lat.toFixed(7);
      if (lngInput) lngInput.value = lng.toFixed(7);
    };
    marker.on('dragend', function(e){
      const { lat, lng } = e.target.getLatLng();
      updateLatLng(lat, lng);
      osmReverseGeocode(lat, lng);
    });
    map.on('click', function(e){
      const { lat, lng } = e.latlng;
      marker.setLatLng([lat, lng]);
      updateLatLng(lat, lng);
      osmReverseGeocode(lat, lng);
    });
    setTimeout(() => { map.invalidateSize(); }, 100);
    // Nếu người dùng nhập/xóa field địa chỉ --> forward geocode chưa hỗ trợ OSM giản đơn
  }
  function loadLeafletAndInit() {
    if (typeof L !== 'undefined') return initLeaflet();
    const css = document.createElement('link');
    css.rel = 'stylesheet';
    css.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
    document.head.appendChild(css);
    const js = document.createElement('script');
    js.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
    js.async = true;
    js.onload = initLeaflet;
    document.body.appendChild(js);
  }

  // Callback Google, chỉ chạy nếu script & api đã sẵn sàng
  window.initAddressMap = function(){
    mapsReady = true;
    const modal = document.getElementById('addressModal');
    if (modal && !modal.classList.contains('hidden')) {
      setTimeout(() => initMap(), 100);
    }
  }
  document.addEventListener('DOMContentLoaded', function(){
    const modal = document.getElementById('addressModal');
    const openBtn = document.querySelector('.btn-add-address');
    const ensureInit = () => {
      setTimeout(() => {
        if (mapsReady && window.google && google.maps) initMap();
        else loadLeafletAndInit();
      }, 200);
    };
    if (openBtn) openBtn.addEventListener('click', ensureInit);
    document.querySelectorAll('.edit-address').forEach(btn => {
      btn.addEventListener('click', ensureInit);
    });
    if (modal && !modal.classList.contains('hidden')) ensureInit();
  });
})();

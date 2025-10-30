(function(){
  let map, marker, autocomplete, mapsReady = false, usingLeaflet = false;

  function setAddressFields(address, ward, district, province) {
    const lineInput = document.getElementById('line');
    if(lineInput) lineInput.value = address || '';
    // Try to set selects by matching text
    const provinceSelect = document.getElementById('provinceSelect');
    const districtSelect = document.getElementById('districtSelect');
    const wardSelect = document.getElementById('wardSelect');
    function selectByText(select, text){
      if (!select || !text) return;
      const idx = Array.from(select.options).findIndex(o => o.text === text);
      if (idx > 0) { select.selectedIndex = idx; select.dispatchEvent(new Event('change')); }
    }
    if (provinceSelect && province) selectByText(provinceSelect, province);
    // District and ward will be set after options load; try delayed
    if (district && districtSelect) setTimeout(()=>selectByText(districtSelect, district), 500);
    if (ward && wardSelect) setTimeout(()=>selectByText(wardSelect, ward), 900);
  }

  function extractVNAddressParts(addressComponents) {
    let province = "", district = "", ward = "", line = "";
    function find(types) { return addressComponents.find(c => types.some(t => c.types?.includes(t))); }
    const provinceComp = find(["administrative_area_level_1"]);
    if (provinceComp) province = provinceComp.long_name;
    let districtComp = find(["administrative_area_level_2","locality"]);
    if (districtComp) district = districtComp.long_name;
    let wardComp = find(["sublocality_level_1","administrative_area_level_3"]);
    if (wardComp) ward = wardComp.long_name;
    if (district && province && district === province) district = "";
    if (ward && district && ward === district) ward = "";
    let street = find(["route"]);
    let street_number = find(["street_number"]);
    let premise = find(["premise"]);
    let sublocality2 = find(["sublocality_level_2"]);
    if (street && street_number) line = street_number.long_name + " " + street.long_name;
    else if (street) line = street.long_name;
    else if (premise) line = premise.long_name;
    else if (sublocality2) line = sublocality2.long_name;
    return { line, ward, district, province };
  }

  function setAddressFromGoogleComponents(place) {
    const comps = place.address_components || [];
    const parts = extractVNAddressParts(comps);
    setAddressFields(parts.line, parts.ward, parts.district, parts.province);
  }

  function googleReverseGeocode(lat, lng) {
    if(!window.google || !google.maps) return;
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({location: {lat, lng}}, function(results, status) {
      if(status === 'OK' && results && results.length) {
        const place = results[0];
        setAddressFromGoogleComponents(place);
      }
    });
  }

  // ---- OSM Reverse Geocode ----
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
        if (ward && ward === district) district = '';
        if (district && district === province) district = '';
        setAddressFields(line, ward, district, province);
      })
      .catch(()=>{});
  }

  function initMap() {
    const mapEl = document.getElementById('addressMap');
    if (!mapEl || !window.google || !google.maps) return;
    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');
    const lineInput = document.getElementById('line');
    const initial = { lat: 10.776889, lng: 106.700806 };
    const position = (latInput?.value && lngInput?.value)
      ? { lat: parseFloat(latInput.value), lng: parseFloat(lngInput.value) }
      : initial;

    map = new google.maps.Map(mapEl, { center: position, zoom: 14 });
    marker = new google.maps.Marker({ position, map, draggable: true });
    const updateLatLng = (pos) => {
      if (latInput) latInput.value = pos.lat().toFixed(7);
      if (lngInput) lngInput.value = pos.lng().toFixed(7);
    };
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
    setTimeout(() => {
      google.maps.event.trigger(map, 'resize');
      map.setCenter(marker.getPosition());
    }, 200);

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

  function initLeaflet() {
    const mapEl = document.getElementById('addressMap');
    if (!mapEl || typeof L === 'undefined') return;
    usingLeaflet = true;
    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');
    const position = (latInput?.value && lngInput?.value)
      ? [parseFloat(latInput.value), parseFloat(lngInput.value)]
      : [10.776889, 106.700806];
    const mapInstance = L.map(mapEl).setView(position, 14);
    map = mapInstance;
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(mapInstance);
    const mk = L.marker(position, { draggable: true }).addTo(mapInstance);
    marker = mk;
    const updateLatLng = (lat, lng) => {
      if (latInput) latInput.value = lat.toFixed(7);
      if (lngInput) lngInput.value = lng.toFixed(7);
    };
    mk.on('dragend', function(e){ const { lat, lng } = e.target.getLatLng(); updateLatLng(lat, lng); osmReverseGeocode(lat, lng); });
    mapInstance.on('click', function(e){ const { lat, lng } = e.latlng; mk.setLatLng([lat, lng]); updateLatLng(lat, lng); osmReverseGeocode(lat, lng); });
    setTimeout(() => { mapInstance.invalidateSize(); }, 200);
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

  function ensureInit() {
    setTimeout(() => {
      if (document.getElementById('addressMap') == null) return;
      if (window.google && google.maps) initMap();
      else loadLeafletAndInit();
    }, 250);
  }

  window.initVendorAddressMap = function(){
    mapsReady = true;
    const modal = document.getElementById('storeModal');
    if (modal && modal.classList.contains('show')) ensureInit();
  }

  document.addEventListener('DOMContentLoaded', function(){
    const openBtn = document.getElementById('openRegisterModalBtn');
    if (openBtn) openBtn.addEventListener('click', () => setTimeout(ensureInit, 300));
    const modal = document.getElementById('storeModal');
    if (modal) {
      const observer = new MutationObserver(() => { if (modal.classList.contains('show')) ensureInit(); });
      observer.observe(modal, { attributes: true, attributeFilter: ['class'] });
    }
  });
})();




document.addEventListener("DOMContentLoaded", () => {
  /* ---------------- SLIDER ---------------- */
  const slides = document.querySelectorAll(".slide");
  const dots = document.querySelectorAll(".dot");
  let index = 0;

  function showSlide(i) {
    slides.forEach((s, idx) => {
      s.classList.toggle("active", idx === i);
      dots[idx].classList.toggle("active", idx === i);
    });
  }

  function nextSlide() {
    index = (index + 1) % slides.length;
    showSlide(index);
  }

  function prevSlide() {
    index = (index - 1 + slides.length) % slides.length;
    showSlide(index);
  }

  const nextBtn = document.querySelector(".next");
  const prevBtn = document.querySelector(".prev");

  if (nextBtn && prevBtn) {
    nextBtn.addEventListener("click", nextSlide);
    prevBtn.addEventListener("click", prevSlide);
  }

  dots.forEach((dot, i) => dot.addEventListener("click", () => {
    index = i;
    showSlide(index);
  }));

  if (slides.length > 0) setInterval(nextSlide, 3000);

  
  
  /* ---------------- AVATAR DROPDOWN ---------------- */
  const avatarBtn = document.getElementById("avatar-btn");
  const menu = document.querySelector(".user-menu");

  if (avatarBtn && menu) {
    avatarBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      menu.classList.toggle("active");
    });

    document.addEventListener("click", (e) => {
      if (!menu.contains(e.target)) {
        menu.classList.remove("active");
      }
    });
  }
});


document.addEventListener("DOMContentLoaded", function() {
    const logoutLink = document.getElementById("logout-link");

    // ✅ Kiểm tra tồn tại
    if (!logoutLink) return;

    logoutLink.addEventListener("click", function(e) {
        e.preventDefault(); // chặn reload trang

        fetch('/logout', {
            method: 'POST',
            credentials: 'same-origin',
            headers: { 'Content-Type': 'application/json' }
        })
        .then(res => {
            if (!res.ok) throw new Error('Đăng xuất thất bại');
            return res.text();
        })
        .then(msg => {
            alert(msg);
            window.location.href = '/login';
        })
        .catch(err => {
            console.error(err);
            alert('Có lỗi xảy ra khi đăng xuất');
        });
    });
});



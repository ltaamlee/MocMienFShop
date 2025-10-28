document.addEventListener("DOMContentLoaded", () => {
  const toggles = document.querySelectorAll(".menu-toggle");

  toggles.forEach(toggle => {
    toggle.addEventListener("click", (e) => {
      e.preventDefault();
      const parent = toggle.closest(".menu-item");
      parent.classList.toggle("open");
    });
  });
});

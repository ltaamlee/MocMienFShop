// Hiện spinner
function showSpinner() {
  const spinner = document.getElementById('flowerSpinner');
  if (spinner) {
    spinner.style.display = 'flex';
    spinner.classList.add('animate');
  }
}

// Ẩn spinner
function hideSpinner() {
  const spinner = document.getElementById('flowerSpinner');
  if (spinner) {
    spinner.style.display = 'none';
    spinner.classList.remove('animate');
  }
}


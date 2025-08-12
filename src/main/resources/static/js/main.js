document.addEventListener('DOMContentLoaded', () => {
  const sidenav = document.getElementById("mySidenav");
  const overlay = document.getElementById("overlay");
  const mainNav = document.getElementById("mainNav"); // The desktop sidebar

  function toggleNav() {
    const isOpen = sidenav.style.width === "250px";
    if (isOpen) {
      sidenav.style.width = "0";
      overlay.style.opacity = 0;
      setTimeout(() => {
        overlay.style.display = "none";
      }, 400); // Wait for transition to finish before hiding
    } else {
      sidenav.style.width = "250px";
      overlay.style.display = "block";
      setTimeout(() => {
        overlay.style.opacity = 1;
      }, 10);
    }
  }

  // toggleNav fonksiyonunu global erişime açmak için window objesine ekleyebiliriz
  window.toggleNav = toggleNav;

  const searchForm = document.getElementById('search-form');
  const searchTypeSelect = document.getElementById('search-type');
  const searchQueryInput = document.getElementById('search-query');
  const searchButton = document.getElementById('search-button');
  const resultsContainer = document.getElementById('search-results-container');

  if (!searchForm) {
    console.error('search-form element not found!');
    // No search form on this page, exit early to prevent errors
    return;
  }

  searchForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const query = searchQueryInput.value.trim();
    const type = searchTypeSelect.value;

    if (!query) {
      resultsContainer.innerHTML = `<div class="no-results">Please enter a search term.</div>`;
      return;
    }

    searchButton.disabled = true;
    searchButton.textContent = 'Searching...';
    resultsContainer.innerHTML = `<div class="no-results">Loading...</div>`;

    try {
      const response = await fetch(`/api/books/search?type=${type}&query=${query}`);
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Server Error: ${response.status}. ${errorText}`);
      }
      const books = await response.json();
      renderResults(books);
    } catch (error) {
      console.error('Fetch error:', error);
      resultsContainer.innerHTML = `<div class="no-results" style="background-color: #fdeeee; color: #d93025;">An error occurred. Check the console for details.</div>`;
    } finally {
      searchButton.disabled = false;
      searchButton.textContent = 'Search';
    }
  });

  function renderResults(books) {
    if (!books || books.length === 0) {
      resultsContainer.innerHTML = `<div class="no-results">No books found for your query.</div>`;
      return;
    }

    const tableRows = books.map(book => {
      const availableClass = book.availableCopies > 0 ? 'available' : 'unavailable';
      return `
        <tr data-available="${book.availableCopies}"
            data-title="${book.title || 'N/A'}"
            data-id="${book.bookID}">
          <td>${book.title || 'N/A'}</td>
          <td>${book.author ? (book.author.firstName + ' ' + book.author.lastName) : 'N/A'}</td>
          <td>${book.isbn || 'N/A'}</td>
          <td>${book.topic || 'N/A'}</td>
          <td class="${availableClass}">${book.availableCopies}</td>
        </tr>
      `;
    }).join('');

    resultsContainer.innerHTML = `
      <table class="results-table">
        <thead>
          <tr>
            <th>Title</th>
            <th>Author</th>
            <th>ISBN</th>
            <th>Topic</th>
            <th>Available</th>
          </tr>
        </thead>
        <tbody>
          ${tableRows}
        </tbody>
      </table>
    `;

    // Satırlara tıklama eventi ekle
    const rows = document.querySelectorAll('.results-table tbody tr');
    rows.forEach(row => {
      row.addEventListener('click', function () {
        const available = parseInt(this.getAttribute('data-available'));
        const title = this.getAttribute('data-title');
        const bookId = this.getAttribute('data-id');

       window.location.href = `/borrow-page?bookId=${bookId}`;
      });
    });
  }
});
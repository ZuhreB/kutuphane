document.addEventListener('DOMContentLoaded', () => {
  const sidenav = document.getElementById("mySidenav");
  const overlay = document.getElementById("overlay");
  // mainNav burada doğrudan kullanılmıyor, ancak diğer elemanlar için tanımlanmış.

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

  // Sadece searchForm varsa event listener ekle
  if (searchForm) {
    searchForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const query = searchQueryInput.value.trim();
      const type = searchTypeSelect.value;

      if (!query) {
        resultsContainer.innerHTML = `<div class="no-results">Lütfen bir arama terimi girin.</div>`;
        return;
      }

      searchButton.disabled = true;
      searchButton.textContent = 'Aranıyor...';
      resultsContainer.innerHTML = `<div class="no-results">Yükleniyor...</div>`;

      try {
        const response = await fetch(`/api/books/search?type=${type}&query=${query}`);
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Sunucu Hatası: ${response.status}. ${errorText}`);
        }
        const books = await response.json();
        renderResults(books);
      } catch (error) {
        console.error('Veri çekme hatası:', error);
        resultsContainer.innerHTML = `<div class="no-results" style="background-color: #fdeeee; color: #d93025;">Bir hata oluştu. Detaylar için konsolu kontrol edin.</div>`;
      } finally {
        searchButton.disabled = false;
        searchButton.textContent = 'Ara';
      }
    });
  } else {
    console.warn('ID "search-form" olan arama formu bulunamadı. Arama işlevi aktif olmayacak.');
  }


  function renderResults(books) {
    if (!books || books.length === 0) {
      resultsContainer.innerHTML = `<div class="no-results">Aramanızla eşleşen kitap bulunamadı.</div>`;
      return;
    }

    const tableRows = books.map(book => {
      const availableClass = book.availableCopies > 0 ? 'available' : 'unavailable';
      // 'Available Date' mantığı:
      // Eğer kitap müsaitse (availableCopies > 0), bugünün tarihini göster.
      // Aksi takdirde, eğer expectedReturnDate varsa onu göster, yoksa 'N/A' (Bilgi Yok) göster.
      const availableDate = book.availableCopies > 0
          ? new Date().toLocaleDateString('tr-TR', { year: 'numeric', month: '2-digit', day: '2-digit' }) // Bugünün tarihi
          : (book.expectedReturnDate ? new Date(book.expectedReturnDate).toLocaleDateString('tr-TR', { year: 'numeric', month: '2-digit', day: '2-digit' }) : 'N/A'); // Backend'den gelen beklenen iade tarihi

      return `
        <tr data-available="${book.availableCopies}"
            data-title="${book.title || 'N/A'}"
            data-id="${book.bookID}">
          <td>${book.title || 'N/A'}</td>
          <td>${book.author ? (book.author.firstName + ' ' + book.author.lastName) : 'N/A'}</td>
          <td>${book.isbn || 'N/A'}</td>
          <td>${book.topic || 'N/A'}</td>
          <td class="${availableClass}">${book.availableCopies}</td>
          <td>${availableDate}</td> </tr>
      `;
    }).join('');

    resultsContainer.innerHTML = `
      <table class="results-table">
        <thead>
          <tr>
            <th>Başlık</th>
            <th>Yazar</th>
            <th>ISBN</th>
            <th>Konu</th>
            <th>Mevcut</th>
            <th>Durum Tarihi</th> </tr>
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
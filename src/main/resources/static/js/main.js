document.addEventListener('DOMContentLoaded', () => {
    const sidenav = document.getElementById("mySidenav");
    const overlay = document.getElementById("overlay");
    const mainArticle = document.getElementById('mainArticle'); // mainArticle elementini al

    // toggleNav fonksiyonu (global erişim için window objesine eklenmiş)
    function toggleNav() {
        const isOpen = sidenav.style.width === "250px";
        if (isOpen) {
            sidenav.style.width = "0";
            overlay.style.opacity = 0;
            setTimeout(() => {
                overlay.style.display = "none";
            }, 400); // Geçiş bitince gizle
        } else {
            sidenav.style.width = "250px";
            overlay.style.display = "block";
            setTimeout(() => {
                overlay.style.opacity = 1;
            }, 10);
        }
    }
    window.toggleNav = toggleNav; // HTML içinde onclick="toggleNav()" kullanılabilmesi için
// side bar tıklamasında farkedici dinleme şeysi
    const sidebarLinks = document.querySelectorAll('#mainNav a[data-fragment-url], #mySidenav a[data-fragment-url]');

    sidebarLinks.forEach(link => {
        link.addEventListener('click', async (event) => {
            event.preventDefault(); // Sayfanın yeniden yüklenmesini engelle

            const fragmentUrl = link.dataset.fragmentUrl;

            // Tüm aktif linklerin 'active' sınıfını kaldır
            sidebarLinks.forEach(item => item.classList.remove('active'));
            // Tıklanan linke 'active' sınıfını ekle
            link.classList.add('active');

            if (sidenav && sidenav.style.width === "250px") {
                toggleNav();
            }

            // İçerik yüklenirken bir yükleme mesajı göster
            mainArticle.innerHTML = '<div class="loading-placeholder" style="text-align: center; padding: 20px;">İçerik Yükleniyor...</div>';

            try {
                const response = await fetch(fragmentUrl);
                if (response.ok) {
                    const htmlContent = await response.text();
                    mainArticle.innerHTML = htmlContent; // mainArticle içeriğini güncelle

                    // Yüklenen fragment içindeki scriptleri çalıştırcak bu bölüm
                    executeScriptsInElement(mainArticle);

                    // Fragment yüklendikten sonra arama formu gibi belirli elementlerin tekrar dinleyiciye bağlanması gerekirse
                    //her fragmentta dinamik olarak eklenen yada çıkarılan elementler için
                    // mesela arama formu sadece bazı fragmentlarda varsa o fragment yüklendiğinde listenerı tekrar bağlamalıyım
                    attachSearchFormListener();

                } else {
                    console.error('Fragment yüklenirken hata oluştu:', response.statusText);
                    mainArticle.innerHTML = '<p style="color: red; text-align: center;">İçerik yüklenirken bir hata oluştu.</p>';
                }
            } catch (error) {
                console.error('Fetch hatası:', error);
                mainArticle.innerHTML = '<p style="color: red; text-align: center;">Sunucuya bağlanılamadı. Lütfen internet bağlantınızı kontrol edin.</p>';
            }
        });
    });

    // Sayfa ilk yüklendiğinde varsayılan bir fragment'ı yükle
    const defaultFragmentLink = document.querySelector('#mainNav a[data-fragment-url="/employee/fragments/panel"]');
    if (defaultFragmentLink) {
        defaultFragmentLink.click(); // Sayfa yüklendiğinde Panel linkine tıklamış gibi yap
    }

    // AJAX ile yüklenen HTML içindeki scriptleri çalıştırmak için yardımcı fonksiyon
    function executeScriptsInElement(element) {
        const scripts = element.querySelectorAll('script');
        scripts.forEach(oldScript => {
            const newScript = document.createElement('script');
            // Orjinal script'in tüm özelliklerini (src, type vb.) kopyala
            Array.from(oldScript.attributes).forEach(attr => newScript.setAttribute(attr.name, attr.value));
            // Script içeriğini kopyala
            newScript.textContent = oldScript.textContent;
            // Yeni script etiketini eskisinin yerine koy
            oldScript.parentNode.replaceChild(newScript, oldScript);
        });
    }

    // Arama formu dinleyicisini bir fonksiyona koyduk
    // Böylece dinamik olarak yüklenen fragment'lar içinde arama formu varsa,
    // o form tekrar dinleyiciye bağlanabilir.
    function attachSearchFormListener() {
        const searchForm = document.getElementById('search-form');
        const searchTypeSelect = document.getElementById('search-type');
        const searchQueryInput = document.getElementById('search-query');
        const searchButton = document.getElementById('search-button');
        const resultsContainer = document.getElementById('search-results-container');


        if (searchForm && !searchForm.hasAttribute('data-listener-attached')) { // Listener'ın zaten eklenip eklenmediğini kontrol et
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
                    renderResults(books, resultsContainer); // renderResults'a resultsContainer'ı da geçir
                } catch (error) {
                    console.error('Veri çekme hatası:', error);
                    resultsContainer.innerHTML = `<div class="no-results" style="background-color: #fdeeee; color: #d93025;">Bir hata oluştu. Detaylar için konsolu kontrol edin.</div>`;
                } finally {
                    searchButton.disabled = false;
                    searchButton.textContent = 'Ara';
                }
            });
            searchForm.setAttribute('data-listener-attached', 'true'); // Listener eklendiğini işaretle
        }
    }


    function renderResults(books, container) {
        if (!books || books.length === 0) {
            container.innerHTML = `<div class="no-results">Aramanızla eşleşen kitap bulunamadı.</div>`;
            return;
        }

        const tableRows = books.map(book => {
            const availableClass = book.availableCopies > 0 ? 'available' : 'unavailable';
            const availableDate = book.availableCopies > 0
                ? new Date().toLocaleDateString('tr-TR', { year: 'numeric', month: '2-digit', day: '2-digit' })
                : (book.expectedReturnDate ? new Date(book.expectedReturnDate).toLocaleDateString('tr-TR', { year: 'numeric', month: '2-digit', day: '2-digit' }) : 'N/A');

            return `
                <tr data-available="${book.availableCopies}"
                    data-title="${book.title || 'N/A'}"
                    data-id="${book.bookID}">
                    <td>${book.title || 'N/A'}</td>
                    <td>${book.author ? (book.author.firstName + ' ' + book.author.lastName) : 'N/A'}</td>
                    <td>${book.isbn || 'N/A'}</td>
                    <td>${book.topic || 'N/A'}</td>
                    <td class="${availableClass}">${book.availableCopies}</td>
                    <td>${availableDate}</td>
                </tr>
            `;
        }).join('');

        container.innerHTML = `
            <table class="results-table">
                <thead>
                    <tr>
                        <th>Başlık</th>
                        <th>Yazar</th>
                        <th>ISBN</th>
                        <th>Konu</th>
                        <th>Mevcut</th>
                        <th>Durum Tarihi</th>
                    </tr>
                </thead>
                <tbody>
                    ${tableRows}
                </tbody>
            </table>
        `;

        // Satırlara tıklama eventi ekle
        const rows = container.querySelectorAll('.results-table tbody tr');
        rows.forEach(row => {
            row.addEventListener('click', function() {
                const available = parseInt(this.getAttribute('data-available'));
                const title = this.getAttribute('data-title');

                if (available > 0) {
                    alert(`"${title}" kitabını alabilirsiniz!`);
                } else {
                    alert(`"${title}" kitabı şu anda alınamaz (Müsait değil)`);
                }
            });
        });
    }

    // Sayfa ilk yüklendiğinde veya bir fragment yüklendiğinde arama formu dinleyicisini bağla
    attachSearchFormListener();
});
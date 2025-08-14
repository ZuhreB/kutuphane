package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmployeeController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private UserService  userService;
    @Autowired
    private BorrowService borrowService;


    /**
     * Ana çalışan paneli sayfasını gösterir.
     * Bu metod, tam sayfa yüklemesi içindir (örneğin ilk giriş veya tarayıcı yenilemesi).
     * mainArticle içeriği artık JavaScript tarafından dinamik olarak yüklenecektir.
     */
    @GetMapping("/employee/page")
    public String showAdminPage(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login"; // Yetkisiz erişim ise giriş sayfasına yönlendir
        }

        model.addAttribute("pageTitle", "Çalışan Paneli");
        model.addAttribute("loggedUser", loggedUser);
        // Bu metod sadece layout'u döndürür. mainArticle içeriği JS ile yüklenecektir.
        // Aşağıdaki satırlar, eğer main-content.html'de bu veriler gerekliyse,
        // direkt olarak main-content.html fragment'ına da eklenmelidir.
        // Ancak ilk yüklemede JS tarafından çekileceği için burada olması çok kritik değil.
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("books", bookService.getAllBooks());

        // Eski yaklaşım: model.addAttribute("contentFragment", "fragments/main-content.html :: main-content.html");
        // Artık bu satıra gerek yok, çünkü JS dinamik olarak çekecek.
        return "layout";
    }

    /**
     * Eski üye ekleme formu endpoint'i.
     * Bu metot, doğrudan bu URL'ye gidildiğinde tam sayfayı yükler.
     * Dinamik AJAX yüklemesi için aşağıda "/employee/fragments/members/add" metodu kullanılacaktır.
     */
    @GetMapping("/employee/members/add")
    public String showAddMemberFormFullPage(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Yeni Üye Ekle");
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("contentFragment", "fragments/member-registration.html :: contentFragment"); // contentFragment olarak kullandığını varsaydım
        return "layout";
    }

    // --- AJAX İle Yüklenecek Fragment Endpoint'leri ---

    /**
     * Çalışan Paneli (Dashboard) fragment'ını döndürür.
     * main-content.html dosyasındaki "contentFragment" adlı Thymeleaf fragment'ını temsil eder.
     */
    @GetMapping("/employee/fragments/panel")
    public String getPanelFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login"; // Veya AJAX isteği için 401 Unauthorized yanıtı döndür
        }

        // Panelin ihtiyacı olan verileri buraya ekle
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("books", bookService.getAllBooks()); // Eğer panelde kitap listesi varsa
        return "fragments/main-content :: contentFragment";
    }

    /**
     * Kitap Listesi fragment'ını döndürür.
     * list-books-content.html dosyasındaki "contentFragment" adlı Thymeleaf fragment'ını temsil eder.
     */
    @GetMapping("/employee/fragments/books")
    public String getBooksFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);

        model.addAttribute("books", bookService.getAllBooks()); // Kitap listesi için veri çek
        // Diğer filtreleme veya sıralama için de buradan veri çekilebilir
        return "fragments/list-books-content :: contentFragment";
    }

    /**
     * Kitap Ekleme Formu fragment'ını döndürür.
     * add-book-content.html dosyasındaki "contentFragment" adlı Thymeleaf fragment'ını temsil eder.
     */
    @GetMapping("/employee/fragments/books/add")
    public String getAddBookFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book()); // Yeni kitap objesi
        model.addAttribute("authors", authorService.findAll()); // Yazar listesi
        model.addAttribute("publishers", publisherService.findAll()); // Yayıncı listesi
        return "fragments/add-book-content :: contentFragment";
    }

    /**
     * Kitap Ödünç Verme Formu fragment'ını döndürür.
     * lend-book-form.html dosyasındaki "contentFragment" adlı Thymeleaf fragment'ını temsil eder.
     */
    @GetMapping("/employee/fragments/borrows/new")
    public String getLendBookFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        // Ödünç verme için gerekli veriler (örneğin kitaplar, kullanıcılar)
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("users", userService.getAllUsers());
        return "fragments/lend-book-form :: contentFragment";
    }

    /**
     * Ödünç Alınan Kitaplar Listesi fragment'ını döndürür.
     * list-borrowed.html dosyasındaki "contentFragment" adlı Thymeleaf fragment'ını temsil eder.
     */
    @GetMapping("/employee/fragments/borrowed")
    public String getBorrowedBooksFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        // Ödünç alınan kitapların listesi için veri çek
         model.addAttribute("borrowedBooks", borrowService.getBorrowedBooks());
        return "fragments/list-borrowed :: contentFragment";
    }

    /**
     * Üye Listesi fragment'ını döndürür.
     * list-user.html dosyasındaki "contentFragment" adlı Thymeleaf fragment'ını temsil eder (varsayımsal).
     */
    @GetMapping("/employee/fragments/members")
    public String getMembersFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("users", userService.getAllUsers()); // Tüm kullanıcıları çek (üyeleri)
        return "fragments/list-user :: contentFragment"; // Üye listesi için varsayılan fragment
    }

    /**
     * Yeni Üye Kayıt Formu fragment'ını döndürür.
     * member-registration.html dosyasındaki "contentFragment" adlı Thymeleaf fragment'ını temsil eder.
     */
    @GetMapping("/employee/fragments/members/add")
    public String getAddMemberFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        // Üye kayıt formu için başlangıç verileri gerekirse eklenebilir.
        model.addAttribute("newUser", new User());
        return "fragments/member-registration :: contentFragment";
    }

    /**
     * Yeni üye kayıt formunun POST isteğini işler (API endpoint).
     * Bu metod, AJAX çağrısı ile form verilerini alır ve veritabanına kaydeder.
     * Bu bir API endpoint'idir, HTML fragment döndürmez.
     */
    @PostMapping("/api/members/register")
    @ResponseBody
    public User registerMember(@RequestBody User user, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            // Yetkisiz erişim durumunda uygun bir hata kodu veya boş bir nesne döndürülebilir
            // Örneğin: throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            return null; // veya özel bir hata nesnesi
        }
        user.setRole("USER"); // Varsayılan rolü "USER" olarak ayarla
        userService.saveUser(user);
        return user; // Kaydedilen kullanıcı nesnesini döndür
    }
}
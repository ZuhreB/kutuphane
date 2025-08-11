document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');
    const errorMessageDiv = document.getElementById('error-message');
    const submitButton = loginForm.querySelector('.login-btn');

    // URL'deki hata parametresini kontrol et (örneğin, oturum süresi dolduysa)
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('error')) {
        const errorKey = urlParams.get('error');
        let message = 'An unknown error occurred.';
        if (errorKey === 'session_expired') {
            message = 'Your session has expired. Please log in again.';
        } else if (errorKey === 'unauthorized') {
            message = 'You are not authorized to view this page.';
        }
        errorMessageDiv.textContent = message;
        errorMessageDiv.style.display = 'block';
    }

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        errorMessageDiv.style.display = 'none';
        submitButton.disabled = true;
        submitButton.textContent = 'Logging in...';

        const formData = new FormData(loginForm);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch('/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                window.location.href = await response.text();
            } else {
                errorMessageDiv.textContent = await response.text() || 'Invalid username or password.';
                errorMessageDiv.style.display = 'block';
            }
        } catch (error) {
            console.error('Login fetch error:', error);
            errorMessageDiv.textContent = 'Could not connect to the server. Please try again.';
            errorMessageDiv.style.display = 'block';
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Log in';
        }
    });
});
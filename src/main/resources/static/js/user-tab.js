// user-tab.js
document.addEventListener('DOMContentLoaded', function() {
    // Инициализация вкладки User
    const userTabTrigger = document.getElementById('user-tab');
    const userTableBody = document.getElementById('userTableBody');

    if (userTabTrigger) {
        // Обработчик переключения на вкладку User
        userTabTrigger.addEventListener('shown.bs.tab', loadUserData);

        // Если вкладка User активна при загрузке страницы
        if (userTabTrigger.classList.contains('active')) {
            loadUserData();
        }
    }

    // Функция загрузки данных пользователя
    function loadUserData() {
        console.log('Loading current user data...');
        fetch('/api/v1/users/current', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(user => {
                console.log('User data received:', user);
                renderUserData(user);
            })
            .catch(error => {
                console.error('Error loading user data:', error);
                showError(error.message);
            });
    }

    // Функция отображения данных пользователя
    function renderUserData(user) {
        console.log(user)
        const rolesHtml = user.roles.map(role =>
            `<span class="badge bg-primary role-badge">${role}</span>`
        ).join(' ');

        userTableBody.innerHTML = `
            <tr>
                <td>${user.id}</td>
                <td>${user.name || '-'}</td>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${rolesHtml}</td>
            </tr>
        `;
    }

    // Функция отображения ошибки
    function showError(message) {
        userTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center text-danger">
                    Error: ${message}
                </td>
            </tr>
        `;
    }
});
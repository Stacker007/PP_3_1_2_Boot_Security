document.addEventListener('DOMContentLoaded', function() {
    // Инициализация переменных
    const usersTable = document.getElementById('usersTable');
    const tableBody = usersTable.querySelector('tbody');
    const createUserForm = document.getElementById('createUserForm');
    const editModal = new bootstrap.Modal(document.getElementById('editModal'));
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

    // Загрузка данных при старте
    loadUsers();

    // Обработчик формы создания пользователя
    createUserForm.addEventListener('submit', function(e) {
        e.preventDefault();
        createUser();
    });

    // Функция загрузки пользователей
    function loadUsers() {
        fetch('/api/v1/users')
            .then(response => response.json())
            .then(users => {
                renderUsersTable(users);
            })
            .catch(error => {
                showAlert('Ошибка загрузки пользователей: ' + error.message, 'danger');
            });
    }

    // Функция рендеринга таблицы
    function renderUsersTable(users) {
        tableBody.innerHTML = '';

        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.name || ''}</td>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>
                    ${user.roles.map(role =>
                `<span class="badge bg-primary role-badge">${role.role}</span>`
            ).join(' ')}
                </td>
                <td class="action-buttons">
                    <button type="button" class="btn btn-sm btn-outline-primary edit-btn"
                            data-user-id="${user.id}">
                        <i class="fas fa-edit"></i> Edit
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger delete-btn"
                            data-user-id="${user.id}">
                        <i class="fas fa-trash-alt"></i> Delete
                    </button>
                </td>
            `;
            tableBody.appendChild(row);
        });

        // Добавляем обработчики для кнопок
        addTableEventListeners();
    }

    // Функция создания пользователя
    function createUser() {
        const formData = new FormData(createUserForm);
        const roles = Array.from(createUserForm.querySelectorAll('input[name="roles"]:checked'))
            .map(checkbox => ({ role: checkbox.value }));

        const user = {
            name: formData.get('name'),
            email: formData.get('email'),
            username: formData.get('username'),
            password: formData.get('password'),
            roles: roles
        };

        fetch('/api/v1/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.message); });
                }
                return response.json();
            })
            .then(() => {
                createUserForm.reset();
                showAlert('Пользователь успешно создан', 'success');
                loadUsers(); // Обновляем таблицу
            })
            .catch(error => {
                showAlert('Ошибка создания пользователя: ' + error.message, 'danger');
            });
    }

    // Функция добавления обработчиков для кнопок в таблице
    function addTableEventListeners() {
        // Обработчики кнопок редактирования
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const userId = this.getAttribute('data-user-id');
                fetch(`/api/v1/users/${userId}`)
                    .then(response => response.json())
                    .then(user => {
                        openEditModal(user);
                    });
            });
        });

        // Обработчики кнопок удаления
        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const userId = this.getAttribute('data-user-id');
                openDeleteModal(userId);
            });
        });
    }

    // Функция открытия модального окна редактирования
    function openEditModal(user) {
        // Заполняем форму редактирования
        document.getElementById('editUserId').value = user.id;
        document.getElementById('editName').value = user.name;
        document.getElementById('editEmail').value = user.email;
        document.getElementById('editUsername').value = user.username;

        // Сбрасываем чекбоксы ролей
        document.querySelectorAll('#editForm input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = false;
        });

        // Устанавливаем выбранные роли
        user.roles.forEach(role => {
            const checkbox = document.getElementById(`editRole_${role.role}`);
            if (checkbox) {
                checkbox.checked = true;
            }
        });

        // Показываем модальное окно
        editModal.show();
    }

    // Функция открытия модального окна удаления
    function openDeleteModal(userId) {
        document.getElementById('deleteUserId').value = userId;
        deleteModal.show();
    }

    // Функция показа уведомлений
    function showAlert(message, type) {
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show`;
        alert.role = 'alert';
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        const container = document.querySelector('.container.py-4');
        container.prepend(alert);

        setTimeout(() => {
            alert.classList.remove('show');
            setTimeout(() => alert.remove(), 150);
        }, 5000);
    }
});
document.addEventListener('DOMContentLoaded', function() {
    // Инициализация переменных
    const usersTable = document.getElementById('usersTable');
    const tableBody = usersTable.querySelector('tbody');
    const createUserForm = document.getElementById('createUserForm');
    const editModal = new bootstrap.Modal(document.getElementById('editModal'));
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
    const editForm = document.getElementById('editForm');
    const deleteForm = document.getElementById('deleteForm');
    let currentEditUserId = null;

    loadUsers();

    createUserForm.addEventListener('submit', function(e) {
        e.preventDefault();
        createUser();
    });
    editForm.addEventListener('submit', function (e){
        e.preventDefault();
        editUser();
    });
    deleteForm.addEventListener('submit', handleDeleteUser);


    function loadUsers() {
        fetch('/api/v1/users',{
            credentials: 'include'
        })
            .then(response => response.json())
            .then(users => {
                renderUsersTable(users);
            })
            .catch(error => {
                showAlert('Ошибка загрузки пользователей: ' + error.message, 'danger');
            });
    }

    function renderUsersTable(users) {
        console.log(users)
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
                `<span class="badge bg-primary role-badge">${role}</span>`
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

        addTableEventListeners();
    }

    // Функция создания пользователя
    function createUser() {
        const formData = new FormData(createUserForm);
        const roles = Array.from(createUserForm.querySelectorAll('input[name="roles"]:checked'))
            .map(checkbox => checkbox.value);

        const user = {
            name: formData.get('name'),
            email: formData.get('email'),
            username: formData.get('username'),
            password: formData.get('password'),
            roles: roles
        };
        console.log("Отправляемые данные:", JSON.stringify(user, null, 2));

        fetch('/api/v1/users', {
            credentials: 'include',
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
    function editUser(e) {

        const formData = new FormData(editForm);
        const roles = Array.from(editForm.querySelectorAll('input[name="roles"]:checked'))
            .map(checkbox => checkbox.value);

        const user = {
            id: parseInt(document.getElementById('editUserId').value),
            name: formData.get('name'),
            email: formData.get('email'),
            username: formData.get('username'),
            password: formData.get('password'),
            roles: roles
        };

        fetch(`/api/v1/users/${user.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user)
        })
            .then(handleResponse)
            .then(() => {
                editModal.hide();
                showAlert('Пользователь успешно обновлен', 'success');
                loadUsers();
            })
            .catch(error => {
                showAlert('Ошибка обновления пользователя: ' + error.message, 'danger');
            });
    }

    function handleDeleteUser(e) {
        e.preventDefault();
        const userId = document.getElementById('deleteUserId').value;

        fetch(`/api/v1/users/${userId}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json' // Явно указываем, что ожидаем JSON
            },
        })
            .then(response => {
                if (!response.ok) {
                    // Если ответ не OK, пытаемся получить текст ошибки
                    return response.text().then(text => {
                        throw new Error(text || 'Ошибка сервера');
                    });
                }
                // Если ответ успешный и пустой, просто продолжаем
                return Promise.resolve();
            })
            .then(() => {
                deleteModal.hide();
                showAlert('Пользователь успешно удален', 'success');
                loadUsers();
            })
            .catch(error => {
                showAlert('Ошибка удаления пользователя: ' + error.message, 'danger');
                console.error('Error:', error);
            });
    }
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
    function loadUserForEdit(userId) {
        fetch(`/api/v1/users/${userId}`)
            .then(handleResponse)
            .then(user => {
                currentEditUserId = user.id;
                fillEditForm(user);
                editModal.show();
            })
            .catch(error => {
                showAlert('Ошибка загрузки данных пользователя: ' + error.message, 'danger');
            });
    }
    //Заполнение формы редактирования
    function fillEditForm(user) {
        document.getElementById('editUserId').value = user.id;
        document.getElementById('editName').value = user.name || '';
        document.getElementById('editEmail').value = user.email || '';
        document.getElementById('editUsername').value = user.username || '';
        document.getElementById('editPassword').value = '';

        // Сбрасываем чекбоксы ролей
        document.querySelectorAll('#editForm input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = false;
        });

        // Устанавливаем выбранные роли
        user.roles.forEach(role => {
            const checkbox = document.querySelector(`#editForm input[value="${role.role}"]`);
            if (checkbox) {
                checkbox.checked = true;
            }
        });
    }

    // Обработчик ответа от сервера
    function handleResponse(response) {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || 'Ошибка сервера'); });
        }
        return response.json();
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
        // Сначала получаем данные пользователя
        fetch(`/api/v1/users/${userId}`)
            .then(response => response.json())
            .then(user => {
                // Заполняем поля в модальном окне
                document.getElementById('deleteUserId').value = user.id;
                document.getElementById('modalUserId').textContent = user.id;
                document.getElementById('modalUserName').textContent = user.name || 'N/A';
                document.getElementById('modalUserEmail').textContent = user.email || 'N/A';

                // Форматируем роли для отображения
                const rolesText = user.roles.map(role => role.role).join(', ') || 'N/A';
                document.getElementById('modalUserRoles').textContent = rolesText;

                // Показываем модальное окно
                deleteModal.show();
            })
            .catch(error => {
                console.error('Ошибка загрузки данных пользователя:', error);
                showAlert('Не удалось загрузить данные пользователя', 'danger');
            });
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
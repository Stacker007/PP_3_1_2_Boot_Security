document.addEventListener('DOMContentLoaded', function() {
    // Initialize Bootstrap tabs
    var tabElms = document.querySelectorAll('a[data-bs-toggle="tab"], button[data-bs-toggle="tab"]');
    tabElms.forEach(function(tabEl) {
        tabEl.addEventListener('shown.bs.tab', function (event) {
            // Handle tab change if needed
        });
    });

    // Edit Modal Handler
    var editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var userId = button.getAttribute('data-bs-userid');
            var userName = button.getAttribute('data-bs-username');
            var userEmail = button.getAttribute('data-bs-useremail');
            var userUsername = button.getAttribute('data-bs-userusername');
            var userRoles = button.getAttribute('data-bs-userroles').split(',');

            document.getElementById('editUserId').value = userId;
            document.getElementById('editName').value = userName;
            document.getElementById('editEmail').value = userEmail;
            document.getElementById('editUsername').value = userUsername;

            // Clear all checkboxes first
            document.querySelectorAll('#editModal input[type="checkbox"]').forEach(checkbox => {
                checkbox.checked = false;
            });

            // Check the appropriate roles
            userRoles.forEach(role => {
                var checkbox = document.getElementById('editRole_' + role.trim());
                if (checkbox) {
                    checkbox.checked = true;
                }
            });
        });
    }

    // Delete Modal Handler
    var deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var userId = button.getAttribute('data-bs-userid');
            var userName = button.getAttribute('data-bs-username');
            var userEmail = button.getAttribute('data-bs-useremail');
            var userRoles = button.getAttribute('data-bs-userroles');

            document.getElementById('modalUserId').textContent = userId;
            document.getElementById('modalUserName').textContent = userName;
            document.getElementById('modalUserEmail').textContent = userEmail;
            document.getElementById('modalUserRoles').textContent = userRoles;
            document.getElementById('deleteUserId').value = userId;
        });
    }
});
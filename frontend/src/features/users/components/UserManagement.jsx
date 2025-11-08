/**
 * UserManagement Component
 * Main component for user management page
 * Based on reference-user.html and OpenAPI User spec
 */
import { useState, useEffect } from 'react';
import UserHeader from './UserHeader';
import UserTable from './UserTable';
import Pagination from '../../../shared/components/Pagination';
import ViewUserModal from './ViewUserModal';
import UserFormModal from './UserFormModal';
import Toast from '../../../shared/components/Toast';
import { usersApi } from '../api/userApi';
import { getCurrentUser, isAdmin } from '../../../utils/jwtUtils';
import './UserManagement.css';

export default function UserManagement() {
  // State for user list and pagination
  const [users, setUsers] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [first, setFirst] = useState(true);
  const [last, setLast] = useState(true);

  // State for search
  const [searchString, setSearchString] = useState('');
  const [searchInput, setSearchInput] = useState(''); // For input field value
  const [debounceTimeout, setDebounceTimeout] = useState(null);

  // State for modals
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);

  // State for toast notifications
  const [toast, setToast] = useState({
    show: false,
    type: 'success',
    title: '',
    message: ''
  });

  // State for loading
  const [loading, setLoading] = useState(false);

  // Get current user from JWT token
  const [currentUser, setCurrentUser] = useState(null);
  const [userIsAdmin, setUserIsAdmin] = useState(false);

  // Load current user from token on mount
  useEffect(() => {
    const user = getCurrentUser();
    if (user) {
      setCurrentUser(user);
      setUserIsAdmin(isAdmin());
    } else {
      // If no valid user, redirect to login
      console.warn('No valid user token found');
      // Optional: redirect to login or show error
    }
  }, []);

  // Debounced search - update searchString after delay
  useEffect(() => {
    // Clear previous timeout
    if (debounceTimeout) {
      clearTimeout(debounceTimeout);
    }

    // If search input is empty, immediately clear search and reset to page 0
    if (!searchInput || searchInput.trim().length === 0) {
      setSearchString('');
      setCurrentPage(0); // Reset to first page when search is cleared
      return;
    }

    // Set new timeout for search
    const timeout = setTimeout(() => {
      setSearchString(searchInput.trim());
      setCurrentPage(0); // Reset to first page on search
    }, 500); // 500ms debounce

    setDebounceTimeout(timeout);

    // Cleanup
    return () => {
      if (timeout) {
        clearTimeout(timeout);
      }
    };
  }, [searchInput]);

  // Fetch users on component mount and when filters change
  useEffect(() => {
    fetchUsers();
  }, [currentPage, pageSize, searchString]);

  const fetchUsers = async () => {
    try {
      setLoading(true);

      // Создаем объект параметров без searchString, если он пустой
      const params = {
        page: currentPage,
        size: pageSize
      };

      // Добавляем searchString только если он не пустой
      if (searchString && searchString.trim().length > 0) {
        params.searchString = searchString;
      }

      const response = await usersApi.getUsers(params);

      setUsers(response.content);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      setFirst(response.first);
      setLast(response.last);
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось загрузить пользователей');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchInputChange = (value) => {
    setSearchInput(value);
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const handleResetFilters = () => {
    setSearchInput('');
    setSearchString('');
    setCurrentPage(0);
  };

  // Modal handlers
  const handleViewUser = (user) => {
    setSelectedUser(user);
    setViewModalOpen(true);
  };

  const handleEditUser = (user) => {
    setSelectedUser(user);
    setFormModalOpen(true);
  };

  const handleCreateUser = () => {
    setSelectedUser(null);
    setFormModalOpen(true);
  };

  const handleDeleteUser = async (user) => {
    if (!confirm(`Вы уверены, что хотите удалить пользователя "${user.firstName} ${user.lastName}" (@${user.username})?`)) {
      return;
    }

    try {
      await usersApi.deleteUser(user.id);
      showToast('success', 'Успех', 'Пользователь успешно удален');
      fetchUsers(); // Refresh list
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось удалить пользователя');
    }
  };

  const handleSaveUser = async (userData) => {
    try {
      if (selectedUser) {
        // Update existing user
        await usersApi.updateUser(selectedUser.id, userData);
        showToast('success', 'Успех', 'Пользователь успешно обновлен');
      } else {
        // Create new user
        await usersApi.createUser(userData);
        showToast('success', 'Успех', 'Пользователь успешно создан');
      }

      setFormModalOpen(false);
      setSelectedUser(null);
      fetchUsers(); // Refresh list
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось сохранить пользователя');
    }
  };

  const showToast = (type, title, message) => {
    setToast({
      show: true,
      type,
      title,
      message
    });
  };

  const hideToast = () => {
    setToast(prev => ({ ...prev, show: false }));
  };

  return (
    <div className="user-management">
      <div className="header">
        <h1>{userIsAdmin ? 'Управление пользователями' : 'Пользователи'}</h1>
        <div className="header-right">
          {currentUser && <UserHeader user={currentUser} />}
          {userIsAdmin && (
            <button className="btn btn-primary" onClick={handleCreateUser}>
              ➕ Создать пользователя
            </button>
          )}
        </div>
      </div>

      {/* Filters */}
      <div className="filters">
        <div className="filters-grid">
          <div className="filter-group">
            <label htmlFor="searchString">Поиск</label>
            <input
              type="text"
              id="searchString"
              value={searchInput}
              onChange={(e) => handleSearchInputChange(e.target.value)}
              placeholder="Поиск по ID, username, имени или фамилии..."
            />
          </div>
          <div className="filter-group">
            <button className="btn btn-primary" onClick={handleResetFilters}>
              🔄 Сбросить фильтры
            </button>
          </div>
        </div>
      </div>

      {/* Users Table */}
      <div className="user-table-container">
        {loading ? (
          <div className="loading-state">Загрузка...</div>
        ) : (
          <>
            <UserTable
              users={users}
              onView={handleViewUser}
              onEdit={handleEditUser}
              onDelete={handleDeleteUser}
              isAdmin={userIsAdmin}
            />

            {users.length > 0 && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                totalElements={totalElements}
                pageSize={pageSize}
                first={first}
                last={last}
                onPageChange={handlePageChange}
              />
            )}
          </>
        )}
      </div>

      {/* Modals */}
      <ViewUserModal
        user={selectedUser}
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
      />

      <UserFormModal
        user={selectedUser}
        isOpen={formModalOpen}
        onClose={() => {
          setFormModalOpen(false);
          setSelectedUser(null);
        }}
        onSave={handleSaveUser}
      />

      {/* Toast Notifications */}
      <Toast
        show={toast.show}
        type={toast.type}
        title={toast.title}
        message={toast.message}
        onClose={hideToast}
      />
    </div>
  );
}
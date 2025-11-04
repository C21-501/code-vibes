/**
 * UserManagement Component
 * Main component for user management page
 * Based on reference-user.html and OpenAPI User spec
 */
import { useState, useEffect } from 'react';
import Sidebar from './Sidebar';
import UserHeader from './UserHeader';
import UserTable from './UserTable';
import Pagination from './Pagination';
import ViewUserModal from './ViewUserModal';
import UserFormModal from './UserFormModal';
import Toast from './Toast';
import { getUsers, createUser, updateUser, deleteUser } from '../api/userApi';
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

  // Mock current user (in real app, would come from auth context)
  const currentUser = {
    id: 1,
    username: 'i.ivanov',
    firstName: 'Иван',
    lastName: 'Иванов',
    role: 'ADMIN'
  };

  // Fetch users on component mount and when filters change
  useEffect(() => {
    fetchUsers();
  }, [currentPage, pageSize, searchString]);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await getUsers({
        page: currentPage,
        size: pageSize,
        searchString: searchString
      });

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

  const handleSearch = (value) => {
    setSearchString(value);
    setCurrentPage(0); // Reset to first page on search
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const handleResetFilters = () => {
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
      await deleteUser(user.id);
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
        await updateUser(selectedUser.id, userData);
        showToast('success', 'Успех', 'Пользователь успешно обновлен');
      } else {
        // Create new user
        await createUser(userData);
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
    <div className="container">
      <Sidebar currentPage="users" />
      
      <main className="main-content">
        <div className="header">
          <h1>Управление пользователями</h1>
          <div className="header-right">
            <UserHeader user={currentUser} />
            <button className="btn btn-primary" onClick={handleCreateUser}>
              ➕ Создать пользователя
            </button>
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
                value={searchString}
                onChange={(e) => handleSearch(e.target.value)}
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
      </main>

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


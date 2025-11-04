/**
 * SystemManagement Component
 * Main component for system management page with subsystems support
 * Based on OpenAPI System and Subsystem spec
 */
import { useState, useEffect, useRef } from 'react';
import Sidebar from '../../../shared/components/Sidebar';
import SystemTable from './SystemTable';
import Pagination from '../../../shared/components/Pagination';
import ViewSystemModal from './ViewSystemModal';
import SystemFormModal from './SystemFormModal';
import SubsystemFormModal from './SubsystemFormModal';
import ViewSubsystemModal from './ViewSubsystemModal';
import Toast from '../../../shared/components/Toast';
import { getSystems, createSystem, updateSystem, deleteSystem } from '../api/systemApi';
import { createSubsystem, updateSubsystem, deleteSubsystem } from '../api/subsystemApi';
import { getCurrentUser } from '../../../utils/jwtUtils';
import './SystemManagement.css';

export default function SystemManagement() {
  // State for system list and pagination
  const [systems, setSystems] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [first, setFirst] = useState(true);
  const [last, setLast] = useState(true);

  // State for search
  const [searchName, setSearchName] = useState('');
  const [debouncedSearchName, setDebouncedSearchName] = useState('');
  const debounceTimeout = useRef(null);

  // State for system modals
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [selectedSystem, setSelectedSystem] = useState(null);

  // State for subsystem modals
  const [viewSubsystemModalOpen, setViewSubsystemModalOpen] = useState(false);
  const [formSubsystemModalOpen, setFormSubsystemModalOpen] = useState(false);
  const [selectedSubsystem, setSelectedSubsystem] = useState(null);
  const [selectedSystemId, setSelectedSystemId] = useState(null);
  const [selectedSystemForView, setSelectedSystemForView] = useState(null);
  
  // Ref for refreshing subsystems in SystemTable
  const [subsystemRefreshTrigger, setSubsystemRefreshTrigger] = useState(0);

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

  // Load current user from token on mount
  useEffect(() => {
    const user = getCurrentUser();
    if (user) {
      setCurrentUser(user);
    } else {
      console.warn('No valid user token found');
    }
  }, []);

  // Debounce search input - запрос на бэкенд только после 500ms и минимум 3 символа
  useEffect(() => {
    // Clear previous timeout
    if (debounceTimeout.current) {
      clearTimeout(debounceTimeout.current);
    }

    // If search is empty or less than 3 characters, search immediately (empty search)
    if (!searchName || searchName.trim().length < 3) {
      // If empty, clear immediately
      if (!searchName || searchName.trim().length === 0) {
        setDebouncedSearchName('');
        return;
      }
      // If 1-2 characters, don't search yet
      return;
    }

    // Set timeout for search with 3+ characters
    debounceTimeout.current = setTimeout(() => {
      setDebouncedSearchName(searchName.trim());
    }, 500);

    // Cleanup
    return () => {
      if (debounceTimeout.current) {
        clearTimeout(debounceTimeout.current);
      }
    };
  }, [searchName]);

  // Fetch systems on component mount and when filters change
  useEffect(() => {
    fetchSystems();
  }, [currentPage, pageSize, debouncedSearchName]);

  const fetchSystems = async () => {
    try {
      setLoading(true);
      const response = await getSystems({
        page: currentPage,
        size: pageSize,
        name: debouncedSearchName
      });

      setSystems(response.content);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      setFirst(response.first);
      setLast(response.last);
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось загрузить системы');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (value) => {
    setSearchName(value);
    setCurrentPage(0); // Reset to first page on search
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const handleResetFilters = () => {
    setSearchName('');
    setDebouncedSearchName('');
    setCurrentPage(0);
  };

  // Modal handlers
  const handleViewSystem = (system) => {
    setSelectedSystem(system);
    setViewModalOpen(true);
  };

  const handleEditSystem = (system) => {
    setSelectedSystem(system);
    setFormModalOpen(true);
  };

  const handleCreateSystem = () => {
    setSelectedSystem(null);
    setFormModalOpen(true);
  };

  const handleDeleteSystem = async (system) => {
    if (!confirm(`Вы уверены, что хотите удалить систему "${system.name}"?`)) {
      return;
    }

    try {
      await deleteSystem(system.id);
      showToast('success', 'Успех', 'Система успешно удалена');
      fetchSystems(); // Refresh list
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось удалить систему');
    }
  };

  const handleSaveSystem = async (systemData) => {
    try {
      if (selectedSystem) {
        // Update existing system
        await updateSystem(selectedSystem.id, systemData);
        showToast('success', 'Успех', 'Система успешно обновлена');
      } else {
        // Create new system
        await createSystem(systemData);
        showToast('success', 'Успех', 'Система успешно создана');
      }
      
      setFormModalOpen(false);
      setSelectedSystem(null);
      fetchSystems(); // Refresh list
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось сохранить систему');
    }
  };

  // Subsystem handlers
  const handleViewSubsystem = (systemId, subsystem, system) => {
    setSelectedSubsystem(subsystem);
    setSelectedSystemId(systemId);
    setSelectedSystemForView(system);
    setViewSubsystemModalOpen(true);
  };

  const handleEditSubsystem = async (systemId, subsystem) => {
    setSelectedSubsystem(subsystem);
    setSelectedSystemId(systemId);
    setFormSubsystemModalOpen(true);
  };

  const handleAddSubsystem = async (systemId) => {
    setSelectedSubsystem(null);
    setSelectedSystemId(systemId);
    setFormSubsystemModalOpen(true);
  };

  const handleDeleteSubsystem = async (systemId, subsystem) => {
    if (!confirm(`Вы уверены, что хотите удалить подсистему "${subsystem.name}"?`)) {
      return;
    }

    try {
      await deleteSubsystem(systemId, subsystem.id);
      showToast('success', 'Успех', 'Подсистема успешно удалена');
      
      // Trigger subsystem refresh in SystemTable
      setSubsystemRefreshTrigger(prev => prev + 1);
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось удалить подсистему');
    }
  };

  const handleSaveSubsystem = async (subsystemData) => {
    try {
      if (selectedSubsystem) {
        // Update existing subsystem
        await updateSubsystem(selectedSystemId, selectedSubsystem.id, subsystemData);
        showToast('success', 'Успех', 'Подсистема успешно обновлена');
      } else {
        // Create new subsystem
        await createSubsystem(selectedSystemId, subsystemData);
        showToast('success', 'Успех', 'Подсистема успешно создана');
      }
      
      setFormSubsystemModalOpen(false);
      setSelectedSubsystem(null);
      setSelectedSystemId(null);
      
      // Trigger subsystem refresh in SystemTable
      setSubsystemRefreshTrigger(prev => prev + 1);
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось сохранить подсистему');
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

  const getInitials = (firstName, lastName) => {
    return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
  };

  const getRoleLabel = (role) => {
    const labels = {
      'REQUESTER': 'Инициатор',
      'EXECUTOR': 'Исполнитель',
      'CAB_MANAGER': 'Менеджер CAB',
      'ADMIN': 'Администратор'
    };
    return labels[role] || role;
  };

  return (
    <div className="container">
      <Sidebar currentPage="systems" />
      
      <main className="main-content">
        <div className="header">
          <h1>Управление системами</h1>
          <div className="header-right">
            {currentUser && (
              <div className="user-info">
                <div className="user-avatar">
                  {getInitials(currentUser.firstName, currentUser.lastName)}
                </div>
                <div className="user-details">
                  <div className="user-name">
                    {currentUser.firstName} {currentUser.lastName}
                  </div>
                  <div className="user-role">
                    <span className={`role-badge role-${currentUser.role.toLowerCase().replace('_', '-')}`}>
                      {getRoleLabel(currentUser.role)}
                    </span>
                  </div>
                </div>
              </div>
            )}
            <button className="btn btn-primary" onClick={handleCreateSystem}>
              ➕ Создать систему
            </button>
          </div>
        </div>

        {/* Filters */}
        <div className="filters">
          <div className="filters-grid">
            <div className="filter-group">
              <label htmlFor="searchName">Поиск по названию</label>
              <input
                type="text"
                id="searchName"
                value={searchName}
                onChange={(e) => handleSearch(e.target.value)}
                placeholder="Введите минимум 3 символа для поиска..."
              />
              {searchName && searchName.trim().length > 0 && searchName.trim().length < 3 && (
                <small style={{ color: '#f39c12', marginTop: '5px', display: 'block' }}>
                  Введите ещё {3 - searchName.trim().length} символ(а) для поиска
                </small>
              )}
            </div>
            <div className="filter-group">
              <button className="btn btn-primary" onClick={handleResetFilters}>
                🔄 Сбросить фильтры
              </button>
            </div>
          </div>
        </div>

        {/* Systems Table */}
        <div className="system-table-container">
          {loading ? (
            <div className="loading-state">Загрузка...</div>
          ) : (
            <>
              <SystemTable
                systems={systems}
                onView={handleViewSystem}
                onEdit={handleEditSystem}
                onDelete={handleDeleteSystem}
                onViewSubsystem={handleViewSubsystem}
                onEditSubsystem={handleEditSubsystem}
                onDeleteSubsystem={handleDeleteSubsystem}
                onAddSubsystem={handleAddSubsystem}
                subsystemRefreshTrigger={subsystemRefreshTrigger}
              />
              
              {systems.length > 0 && (
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
      <ViewSystemModal
        system={selectedSystem}
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
      />

      <SystemFormModal
        system={selectedSystem}
        isOpen={formModalOpen}
        onClose={() => {
          setFormModalOpen(false);
          setSelectedSystem(null);
        }}
        onSave={handleSaveSystem}
      />

      {/* Subsystem Modals */}
      <ViewSubsystemModal
        subsystem={selectedSubsystem}
        system={selectedSystemForView}
        isOpen={viewSubsystemModalOpen}
        onClose={() => {
          setViewSubsystemModalOpen(false);
          setSelectedSubsystem(null);
          setSelectedSystemId(null);
          setSelectedSystemForView(null);
        }}
      />

      <SubsystemFormModal
        subsystem={selectedSubsystem}
        systemId={selectedSystemId}
        isOpen={formSubsystemModalOpen}
        onClose={() => {
          setFormSubsystemModalOpen(false);
          setSelectedSubsystem(null);
          setSelectedSystemId(null);
        }}
        onSave={handleSaveSubsystem}
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


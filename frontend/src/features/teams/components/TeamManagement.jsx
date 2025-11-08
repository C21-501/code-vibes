/**
 * TeamManagement Component
 * Main component for team management page
 * Based on reference-team.html and OpenAPI Team spec
 */
import { useState, useEffect, useRef } from 'react';
import TeamTable from './TeamTable';
import Pagination from '../../../shared/components/Pagination';
import ViewTeamModal from './ViewTeamModal';
import TeamFormModal from './TeamFormModal';
import Toast from '../../../shared/components/Toast';
import { getTeams, createTeam, updateTeam, deleteTeam } from '../api/teamApi';
import { getCurrentUser, isAdmin } from '../../../utils/jwtUtils';
import './TeamManagement.css';

export default function TeamManagement() {
  // State for team list and pagination
  const [teams, setTeams] = useState([]);
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

  // State for modals
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [selectedTeam, setSelectedTeam] = useState(null);

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
        setCurrentPage(0); // Reset to first page when search is cleared
        return;
      }
      // If 1-2 characters, don't search yet
      return;
    }

    // Set timeout for search with 3+ characters
    debounceTimeout.current = setTimeout(() => {
      setDebouncedSearchName(searchName.trim());
      setCurrentPage(0); // Reset to first page on search
    }, 500);

    // Cleanup
    return () => {
      if (debounceTimeout.current) {
        clearTimeout(debounceTimeout.current);
      }
    };
  }, [searchName]);

  // Fetch teams on component mount and when filters change
  useEffect(() => {
    fetchTeams();
  }, [currentPage, pageSize, debouncedSearchName]);

  const fetchTeams = async () => {
    try {
      setLoading(true);

      // Создаем объект параметров без name, если он пустой
      const params = {
        page: currentPage,
        size: pageSize
      };

      // Добавляем name только если он не пустой
      if (debouncedSearchName && debouncedSearchName.trim().length > 0) {
        params.name = debouncedSearchName;
      }

      const response = await getTeams(params);

      setTeams(response.content);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      setFirst(response.first);
      setLast(response.last);
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось загрузить команды');
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
  const handleViewTeam = (team) => {
    setSelectedTeam(team);
    setViewModalOpen(true);
  };

  const handleEditTeam = (team) => {
    setSelectedTeam(team);
    setFormModalOpen(true);
  };

  const handleCreateTeam = () => {
    setSelectedTeam(null);
    setFormModalOpen(true);
  };

  const handleDeleteTeam = async (team) => {
    if (!confirm(`Вы уверены, что хотите удалить команду "${team.name}"?`)) {
      return;
    }

    try {
      await deleteTeam(team.id);
      showToast('success', 'Успех', 'Команда успешно удалена');
      fetchTeams(); // Refresh list
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось удалить команду');
    }
  };

  const handleSaveTeam = async (teamData) => {
    try {
      if (selectedTeam) {
        // Update existing team
        await updateTeam(selectedTeam.id, teamData);
        showToast('success', 'Успех', 'Команда успешно обновлена');
      } else {
        // Create new team
        await createTeam(teamData);
        showToast('success', 'Успех', 'Команда успешно создана');
      }

      setFormModalOpen(false);
      setSelectedTeam(null);
      fetchTeams(); // Refresh list
    } catch (error) {
      showToast('error', 'Ошибка', error.message || 'Не удалось сохранить команду');
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
    <div className="team-management">
      <div className="header">
        <h1>{userIsAdmin ? 'Управление командами' : 'Команды'}</h1>
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
          {userIsAdmin && (
            <button className="btn btn-primary" onClick={handleCreateTeam}>
              ➕ Создать команду
            </button>
          )}
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

      {/* Teams Table */}
      <div className="team-table-container">
        {loading ? (
          <div className="loading-state">Загрузка...</div>
        ) : (
          <>
            <TeamTable
              teams={teams}
              onView={handleViewTeam}
              onEdit={handleEditTeam}
              onDelete={handleDeleteTeam}
              isAdmin={userIsAdmin}
            />

            {teams.length > 0 && (
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
      <ViewTeamModal
        team={selectedTeam}
        isOpen={viewModalOpen}
        onClose={() => setViewModalOpen(false)}
      />

      <TeamFormModal
        team={selectedTeam}
        isOpen={formModalOpen}
        onClose={() => {
          setFormModalOpen(false);
          setSelectedTeam(null);
        }}
        onSave={handleSaveTeam}
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
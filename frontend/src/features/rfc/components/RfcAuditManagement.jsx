/**
 * RfcAuditManagement Component
 * Main component for RFC audit page
 */
import { useState, useEffect } from 'react';
import RfcAuditTable from './RfcAuditTable';
import Pagination from '../../../shared/components/Pagination';
import Toast from '../../../shared/components/Toast';
import UserHeader from '../../users/components/UserHeader';
import { getCurrentUser, isAdmin } from '../../../utils/jwtUtils';
import { rfcApi } from '../api/rfcApi';
import './RfcAuditManagement.css';

export default function RfcAuditManagement() {
  // State for history events and pagination
  const [historyEvents, setHistoryEvents] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [first, setFirst] = useState(true);
  const [last, setLast] = useState(true);

  // State for search
  const [rfcIdInput, setRfcIdInput] = useState('');
  const [currentRfcId, setCurrentRfcId] = useState(null);

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

  // Fetch history when rfcId and pagination change
  useEffect(() => {
    if (currentRfcId !== null) {
      fetchRfcHistory();
    }
  }, [currentPage, pageSize, currentRfcId]);

  const fetchRfcHistory = async () => {
    if (!currentRfcId) {
      return;
    }

    try {
      setLoading(true);

      const response = await rfcApi.getRfcHistory(currentRfcId, {
        page: currentPage,
        size: pageSize
      });

      setHistoryEvents(response.content || []);
      setTotalElements(response.totalElements || 0);
      setTotalPages(response.totalPages || 0);
      setFirst(response.first !== undefined ? response.first : true);
      setLast(response.last !== undefined ? response.last : true);
    } catch (error) {
      const errorMessage = error.response?.data?.errors?.[0]?.message || error.message || 'Не удалось загрузить историю изменений';
      showToast('error', 'Ошибка', errorMessage);
      // Clear history on error
      setHistoryEvents([]);
      setTotalElements(0);
      setTotalPages(0);
      setFirst(true);
      setLast(true);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    const id = rfcIdInput.trim();
    
    if (!id) {
      showToast('error', 'Ошибка', 'Введите ID RFC для поиска');
      return;
    }

    const numericId = parseInt(id, 10);
    if (isNaN(numericId) || numericId <= 0) {
      showToast('error', 'Ошибка', 'ID RFC должен быть положительным числом');
      return;
    }

    setCurrentRfcId(numericId);
    setCurrentPage(0); // Reset to first page on new search
  };

  const handleReset = () => {
    setRfcIdInput('');
    setCurrentRfcId(null);
    setHistoryEvents([]);
    setCurrentPage(0);
    setTotalElements(0);
    setTotalPages(0);
    setFirst(true);
    setLast(true);
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
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

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="rfc-audit-management">
      <div className="header">
        <h1>Аудит RFC</h1>
        <div className="header-right">
          {currentUser && <UserHeader user={currentUser} />}
        </div>
      </div>

      {/* Search Filters */}
      <div className="filters">
        <div className="filters-grid">
          <div className="filter-group">
            <label htmlFor="rfcId">ID RFC</label>
            <input
              type="text"
              id="rfcId"
              value={rfcIdInput}
              onChange={(e) => setRfcIdInput(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="Введите ID RFC..."
            />
          </div>
          <div className="filter-group filter-buttons">
            <button className="btn btn-primary" onClick={handleSearch}>
              🔍 Найти
            </button>
            <button className="btn btn-secondary" onClick={handleReset}>
              🔄 Сброс
            </button>
          </div>
        </div>
      </div>

      {/* History Table */}
      <div className="audit-table-container">
        {loading ? (
          <div className="loading-state">Загрузка...</div>
        ) : (
          <>
            {currentRfcId && (
              <div className="rfc-id-header">
                История изменений RFC #{currentRfcId}
              </div>
            )}
            <RfcAuditTable events={historyEvents} />

            {historyEvents.length > 0 && (
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


/**
 * UserSearchSelect Component
 * Search-based multi-select for team members
 * Loads users on-demand via search instead of loading all users
 */
import { useState, useEffect, useRef } from 'react';
import { getUsers } from '../../users/api/userApi';
import './UserSearchSelect.css';

export default function UserSearchSelect({ selectedMembers, onChange, error }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);
  const [debounceTimeout, setDebounceTimeout] = useState(null);
  
  const searchRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Debounced search
  useEffect(() => {
    // Clear previous timeout
    if (debounceTimeout) {
      clearTimeout(debounceTimeout);
    }

    // Don't search if query is empty or too short
    if (!searchQuery || searchQuery.trim().length < 2) {
      setSearchResults([]);
      setShowDropdown(false);
      return;
    }

    // Set new timeout for search
    const timeout = setTimeout(() => {
      performSearch(searchQuery.trim());
    }, 500); // 500ms debounce

    setDebounceTimeout(timeout);

    // Cleanup
    return () => {
      if (timeout) {
        clearTimeout(timeout);
      }
    };
  }, [searchQuery]);

  const performSearch = async (query) => {
    try {
      setIsSearching(true);
      const response = await getUsers({
        page: 0,
        size: 20, // Limit results
        searchString: query
      });

      // Filter out already selected members
      const selectedIds = selectedMembers.map(m => m.id);
      const filteredResults = response.content.filter(
        user => !selectedIds.includes(user.id)
      );

      setSearchResults(filteredResults);
      setShowDropdown(filteredResults.length > 0);
    } catch (error) {
      console.error('Failed to search users:', error);
      setSearchResults([]);
      setShowDropdown(false);
    } finally {
      setIsSearching(false);
    }
  };

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchQuery(value);
  };

  const handleSelectUser = (user) => {
    // Add user to selected members
    onChange([...selectedMembers, user]);
    
    // Clear search
    setSearchQuery('');
    setSearchResults([]);
    setShowDropdown(false);
  };

  const handleRemoveMember = (userId) => {
    onChange(selectedMembers.filter(m => m.id !== userId));
  };

  const getInitials = (firstName, lastName) => {
    return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
  };

  const getRoleLabel = (role) => {
    const labels = {
      'REQUESTER': 'Инициатор',
      'EXECUTOR': 'Исполнитель',
      'CAB_MANAGER': 'CAB Менеджер',
      'ADMIN': 'Администратор'
    };
    return labels[role] || role;
  };

  return (
    <div className="user-search-select" ref={searchRef}>
      {/* Search input */}
      <div className="search-input-wrapper">
        <input
          type="text"
          className={`search-input ${error ? 'error' : ''}`}
          placeholder="Поиск пользователей по ID, username, имени или фамилии..."
          value={searchQuery}
          onChange={handleSearchChange}
          onFocus={() => {
            if (searchResults.length > 0) {
              setShowDropdown(true);
            }
          }}
        />
        {isSearching && (
          <div className="search-spinner">🔄</div>
        )}
      </div>

      {/* Search results dropdown */}
      {showDropdown && searchResults.length > 0 && (
        <div className="search-dropdown">
          {searchResults.map(user => (
            <div
              key={user.id}
              className="search-result-item"
              onClick={() => handleSelectUser(user)}
            >
              <div className="user-avatar-small">
                {getInitials(user.firstName, user.lastName)}
              </div>
              <div className="user-info-search">
                <div className="user-name-search">
                  {user.firstName} {user.lastName}
                </div>
                <div className="user-details-search">
                  @{user.username} • ID: {user.id} • {getRoleLabel(user.role)}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Selected members */}
      <div className="selected-members-list">
        {selectedMembers.length === 0 ? (
          <div className="no-members-selected">
            <em>Нет выбранных участников. Используйте поиск выше для добавления.</em>
          </div>
        ) : (
          selectedMembers.map(member => (
            <div key={member.id} className="selected-member-card">
              <div className="member-avatar-selected">
                {getInitials(member.firstName, member.lastName)}
              </div>
              <div className="member-info-selected">
                <div className="member-name-selected">
                  {member.firstName} {member.lastName}
                </div>
                <div className="member-details-selected">
                  @{member.username} • {getRoleLabel(member.role)}
                </div>
              </div>
              <button
                type="button"
                className="remove-member-btn"
                onClick={() => handleRemoveMember(member.id)}
                title="Удалить из команды"
              >
                ✕
              </button>
            </div>
          ))
        )}
      </div>

      {/* Selected count */}
      <div className="selected-count-info">
        Выбрано участников: <strong>{selectedMembers.length}</strong>
      </div>

      {error && <div className="error-message">{error}</div>}
      <small className="help-text">
        Введите минимум 2 символа для поиска. Выберите минимум одного участника.
      </small>
    </div>
  );
}


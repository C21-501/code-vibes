import React, { useState, useEffect, useRef } from 'react';
import { getUsers } from '../../users/api/userApi';
import './SingleUserSearchSelect.css';

export default function SingleUserSearchSelect({
  value,
  onChange,
  error,
  placeholder = "Поиск пользователя по имени, фамилии или username...",
  disabled = false
}) {
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
    if (debounceTimeout) {
      clearTimeout(debounceTimeout);
    }

    if (!searchQuery || searchQuery.trim().length < 2) {
      setSearchResults([]);
      setShowDropdown(false);
      return;
    }

    const timeout = setTimeout(() => {
      performSearch(searchQuery.trim());
    }, 500);

    setDebounceTimeout(timeout);

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
        size: 20,
        searchString: query
      });

      setSearchResults(response.content);
      setShowDropdown(response.content.length > 0);
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
    onChange(user);
    setSearchQuery('');
    setSearchResults([]);
    setShowDropdown(false);
  };

  const handleClearSelection = () => {
    onChange(null);
    setSearchQuery('');
  };

  const getInitials = (firstName, lastName) => {
    return (firstName?.charAt(0) + lastName?.charAt(0)).toUpperCase();
  };

  const getRoleLabel = (role) => {
    const labels = {
      'USER': 'Пользователь',
      'RFC_APPROVER': 'Согласующий RFC',
      'CAB_MANAGER': 'CAB Менеджер',
      'ADMIN': 'Администратор'
    };
    return labels[role] || role;
  };

  const getDisplayName = (user) => {
    if (!user) return '';
    return `${user.firstName} ${user.lastName}`.trim();
  };

  const getDisplayMeta = (user) => {
    if (!user) return '';

    const parts = [];
    if (user.username) parts.push(`@${user.username}`);
    if (user.id) parts.push(`ID: ${user.id}`);
    if (user.role) parts.push(getRoleLabel(user.role));

    return parts.join(' • ');
  };

  return (
    <div className="single-user-search-select" ref={searchRef}>
      {/* Selected user display */}
      {value ? (
        <div className="selected-user-display">
          <div className="selected-user-info">
            <div className="user-avatar-small">
              {getInitials(value.firstName, value.lastName)}
            </div>
            <div className="user-details">
              <div className="user-name">
                {getDisplayName(value)}
              </div>
              <div className="user-meta">
                {getDisplayMeta(value)}
              </div>
            </div>
          </div>
          {!disabled && (
            <button
              type="button"
              className="clear-selection-btn"
              onClick={handleClearSelection}
              disabled={disabled}
            >
              ✕
            </button>
          )}
        </div>
      ) : (
        /* Search input */
        <div className="search-input-wrapper">
          <input
            type="text"
            className={`search-input ${error ? 'error' : ''}`}
            placeholder={placeholder}
            value={searchQuery}
            onChange={handleSearchChange}
            onFocus={() => {
              if (searchResults.length > 0) {
                setShowDropdown(true);
              }
            }}
            disabled={disabled}
          />
          {isSearching && (
            <div className="search-spinner">🔄</div>
          )}
        </div>
      )}

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
                  {getDisplayName(user)}
                </div>
                <div className="user-details-search">
                  {getDisplayMeta(user)}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {error && <div className="error-message">{error}</div>}
    </div>
  );
}
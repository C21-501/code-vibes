/**
 * TeamSearchSelect Component
 * Search-based select for team assignment
 * Loads teams on-demand via search instead of loading all teams
 */
import { useState, useEffect, useRef } from 'react';
import { getTeams } from '../../teams/api/teamApi';
import './TeamSearchSelect.css';

export default function TeamSearchSelect({ selectedTeamId, onChange, error }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);
  const [debounceTimeout, setDebounceTimeout] = useState(null);
  const [selectedTeam, setSelectedTeam] = useState(null);
  
  const searchRef = useRef(null);

  // Load selected team details if teamId is provided
  useEffect(() => {
    if (selectedTeamId && !selectedTeam) {
      loadTeamById(selectedTeamId);
    } else if (!selectedTeamId) {
      setSelectedTeam(null);
    }
  }, [selectedTeamId]);

  const loadTeamById = async (teamId) => {
    try {
      const response = await getTeams({ page: 0, size: 100 });
      const team = response.content.find(t => t.id === teamId);
      if (team) {
        setSelectedTeam(team);
      }
    } catch (error) {
      console.error('Failed to load team:', error);
    }
  };

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
    if (!searchQuery || searchQuery.trim().length < 3) {
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
      const response = await getTeams({
        page: 0,
        size: 20, // Limit results
        name: query
      });

      setSearchResults(response.content || []);
      setShowDropdown(response.content.length > 0);
    } catch (error) {
      console.error('Failed to search teams:', error);
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

  const handleSelectTeam = (team) => {
    // Set selected team
    setSelectedTeam(team);
    onChange(team.id);
    
    // Clear search
    setSearchQuery('');
    setSearchResults([]);
    setShowDropdown(false);
  };

  const handleClearSelection = () => {
    setSelectedTeam(null);
    onChange('');
  };

  // Check if input should show error (less than 3 characters and not empty)
  const showInputError = searchQuery.length > 0 && searchQuery.length < 3;

  return (
    <div className="team-search-select" ref={searchRef}>
      {/* Show selected team or search input */}
      {selectedTeam ? (
        <div className="selected-team-display">
          <div className="selected-team-info">
            <div className="team-icon">👥</div>
            <div className="team-details">
              <div className="team-name-display">{selectedTeam.name}</div>
              {selectedTeam.description && (
                <div className="team-description-display">{selectedTeam.description}</div>
              )}
            </div>
          </div>
          <button
            type="button"
            className="clear-team-btn"
            onClick={handleClearSelection}
            title="Очистить выбор"
          >
            ✕
          </button>
        </div>
      ) : (
        <>
          {/* Search input */}
          <div className="search-input-wrapper">
            <input
              type="text"
              className={`search-input ${error || showInputError ? 'error' : ''}`}
              placeholder="Поиск команд по названию..."
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
              {searchResults.map(team => (
                <div
                  key={team.id}
                  className="search-result-item"
                  onClick={() => handleSelectTeam(team)}
                >
                  <div className="team-icon">👥</div>
                  <div className="team-info-search">
                    <div className="team-name-search">{team.name}</div>
                    {team.description && (
                      <div className="team-description-search">{team.description}</div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}

      {error && <div className="error-message">{error}</div>}
      <small className="help-text">
        Введите минимум 3 символа для поиска команды
      </small>
    </div>
  );
}


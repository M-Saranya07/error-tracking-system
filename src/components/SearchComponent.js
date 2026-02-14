import React, { useState } from 'react';

const SearchComponent = ({ onSearch, placeholder = "Search errors..." }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchField, setSearchField] = useState('all');
  const [isSearching, setIsSearching] = useState(false);

  const handleSearch = () => {
    if (searchTerm.trim()) {
      setIsSearching(true);
      onSearch({
        term: searchTerm,
        field: searchField
      });
      setTimeout(() => setIsSearching(false), 500);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const clearSearch = () => {
    setSearchTerm('');
    onSearch({ term: '', field: 'all' });
  };

  return (
    <div className="search-component">
      <div className="search-input-group">
        <select 
          value={searchField} 
          onChange={(e) => setSearchField(e.target.value)}
          className="search-field-select"
        >
          <option value="all">All Fields</option>
          <option value="message">Message</option>
          <option value="applicationName">Application</option>
          <option value="apiName">API Name</option>
          <option value="statusCode">Status Code</option>
        </select>
        
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder={placeholder}
          className="search-input"
        />
        
        {searchTerm && (
          <button onClick={clearSearch} className="clear-search-btn">
            ✕
          </button>
        )}
      </div>
      
      <button 
        onClick={handleSearch} 
        disabled={isSearching}
        className="search-btn"
      >
        {isSearching ? 'Searching...' : '🔍 Search'}
      </button>
    </div>
  );
};

export default SearchComponent;

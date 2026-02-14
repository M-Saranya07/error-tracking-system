import React from 'react';

const FilterPanel = ({ filters, onFilterChange }) => {
  const handleInputChange = (field, value) => {
    // Convert empty string to undefined for cleaner API calls
    const cleanValue = value === '' ? undefined : value;
    onFilterChange(field, cleanValue);
  };

  return (
    <div className="filter-panel">
      <h3>Filters</h3>
      
      <select 
        value={filters.severity || ''} 
        onChange={(e) => handleInputChange('severity', e.target.value)}
      >
        <option value="">All Severities</option>
        <option value="CRITICAL">Critical</option>
        <option value="WARNING">Warning</option>
        <option value="INFO">Info</option>
      </select>
      
      <input 
        type="number" 
        placeholder="Status Code" 
        value={filters.statusCode || ''} 
        onChange={(e) => handleInputChange('statusCode', e.target.value)}
        min="100"
        max="599"
      />
      
      <input 
        type="text" 
        placeholder="Application Name" 
        value={filters.applicationName || ''} 
        onChange={(e) => handleInputChange('applicationName', e.target.value)}
      />
      
      <button 
        onClick={() => onFilterChange('clear', true)}
        style={{ marginLeft: '10px', padding: '8px 16px' }}
      >
        Clear Filters
      </button>
    </div>
  );
};

export default FilterPanel;

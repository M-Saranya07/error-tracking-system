import React, { useState } from 'react';

const EnhancedFilterPanel = ({ filters, onFilterChange }) => {
  const [localFilters, setLocalFilters] = useState(filters);

  const handleInputChange = (field, value) => {
    const newFilters = { ...localFilters, [field]: value };
    setLocalFilters(newFilters);
    onFilterChange(newFilters);
  };

  const handleDateChange = (field, value) => {
    const newFilters = { ...localFilters, [field]: value };
    setLocalFilters(newFilters);
    onFilterChange(newFilters);
  };

  const handleMultiSelectChange = (field, values) => {
    const newFilters = { ...localFilters, [field]: values };
    setLocalFilters(newFilters);
    onFilterChange(newFilters);
  };

  const clearAllFilters = () => {
    setLocalFilters({});
    onFilterChange('clear', {});
  };

  const getToday = () => {
    return new Date().toISOString().split('T')[0];
  };

  return (
    <div className="enhanced-filter-panel">
      <h3>Advanced Filters</h3>
      
      <div className="filter-section">
        <h4>Basic Filters</h4>
        <div className="filter-section">
          <h4>Severity</h4>
          <select 
            value={localFilters.severity || ''} 
            onChange={(e) => handleInputChange('severity', e.target.value)}
          >
            <option value="">All Severities</option>
            <option value="CRITICAL">Critical</option>
            <option value="WARNING">Warning</option>
            <option value="INFO">Info</option>
            <option value="LOW">Low</option>
          </select>
        </div>

        <div className="filter-row">
          <input
            type="number"
            placeholder="Status Code"
            value={localFilters.statusCode || ''}
            onChange={(e) => handleInputChange('statusCode', e.target.value)}
            min="100"
            max="599"
          />

          <input
            type="text"
            placeholder="Application Name"
            value={localFilters.applicationName || ''}
            onChange={(e) => handleInputChange('applicationName', e.target.value)}
          />
        </div>
      </div>

      <div className="filter-section">
        <h4>Date Range</h4>
        <div className="filter-row">
          <div className="date-input-group">
            <label>From:</label>
            <input
              type="date"
              value={localFilters.dateFrom || ''}
              onChange={(e) => handleDateChange('dateFrom', e.target.value)}
              max={getToday()}
            />
          </div>
          
          <div className="date-input-group">
            <label>To:</label>
            <input
              type="date"
              value={localFilters.dateTo || ''}
              onChange={(e) => handleDateChange('dateTo', e.target.value)}
              max={getToday()}
            />
          </div>
        </div>
      </div>

      <div className="filter-section">
        <h4>Applications</h4>
        <select 
          value={localFilters.applications || []} 
          onChange={(e) => handleMultiSelectChange('applications', Array.from(e.target.selectedOptions).map(opt => opt.value))}
          multiple
          size="4"
        >
          <option value="">All Applications</option>
          <option value="payment-service">Payment Service</option>
          <option value="user-service">User Service</option>
          <option value="api-service">API Service</option>
          <option value="notification-service">Notification Service</option>
        </select>
      </div>

      <div className="filter-actions">
        <button onClick={clearAllFilters} className="clear-btn">
          Clear All Filters
        </button>
        <button onClick={() => onFilterChange('apply', localFilters)} className="apply-btn">
          Apply Filters
        </button>
      </div>
    </div>
  );
};

export default EnhancedFilterPanel;

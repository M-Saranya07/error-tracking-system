import React from 'react';

const PaginatedErrorTable = ({ 
  errors = [], 
  currentPage, 
  totalPages, 
  onPageChange, 
  onSort 
}) => {
  const handleSort = (field) => {
    onSort(field);
  };

  const handlePageChange = (page) => {
    onPageChange(page);
  };

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  const getSeverityColor = (severity) => {
    switch (severity) {
      case 'CRITICAL': return '#dc3545';
      case 'WARNING': return '#ffc107';
      case 'INFO': return '#17a2b8';
      case 'LOW': return '#6c757d';
      default: return '#6c757d';
    }
  };

  return (
    <div className="error-table-container">
      <div className="table-controls">
        <div className="pagination-info">
          <span>Page {currentPage || 1} of {totalPages || 1}</span>
          <span>Total {errors?.length || 0} errors</span>
        </div>
        
        <div className="sort-controls">
          <label>Sort by:</label>
          <select onChange={(e) => handleSort(e.target.value)} defaultValue="timestamp">
            <option value="timestamp">Time</option>
            <option value="severity">Severity</option>
            <option value="statusCode">Status Code</option>
            <option value="applicationName">Application</option>
          </select>
        </div>
      </div>

      <div className="error-table">
        <table>
          <thead>
            <tr>
              <th onClick={() => handleSort('id')}>ID ↕</th>
              <th onClick={() => handleSort('applicationName')}>Application ↕</th>
              <th onClick={() => handleSort('apiName')}>API ↕</th>
              <th onClick={() => handleSort('statusCode')}>Status ↕</th>
              <th onClick={() => handleSort('severity')}>Severity ↕</th>
              <th onClick={() => handleSort('timestamp')}>Time ↕</th>
              <th>Message</th>
            </tr>
          </thead>
          <tbody>
            {(errors || []).map((error) => (
              <tr key={error.id}>
                <td>{error.id}</td>
                <td>{error.applicationName}</td>
                <td>{error.apiName}</td>
                <td>{error.statusCode}</td>
                <td style={{ color: getSeverityColor(error.severity), fontWeight: 'bold' }}>
                  {error.severity}
                </td>
                <td>{formatDate(error.timestamp)}</td>
                <td title={error.message}>
                  {error.message ? 
                    (error.message.length > 50 ? 
                      `${error.message.substring(0, 50)}...` : 
                      error.message
                    ) : 
                    'No message'
                  }
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="pagination-controls">
        <button 
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 1 || currentPage === undefined}
        >
          Previous
        </button>
        
        <span className="page-info">
          Page {currentPage || 1} of {totalPages || 1}
        </span>
        
        <button 
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage === totalPages || currentPage === undefined}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default PaginatedErrorTable;

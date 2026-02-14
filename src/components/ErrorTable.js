import React from 'react';

const ErrorTable = ({ errors }) => {
  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  if (!errors || errors.length === 0) {
    return (
      <div className="loading">
        <p>No errors found</p>
      </div>
    );
  }

  return (
    <div className="error-table-container">
      <table className="error-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Application</th>
            <th>API</th>
            <th>Status</th>
            <th>Severity</th>
            <th>Time</th>
            <th>Message</th>
          </tr>
        </thead>
        <tbody>
          {errors.map((error) => (
            <tr key={error.id}>
              <td>{error.id}</td>
              <td>{error.applicationName}</td>
              <td>{error.apiName}</td>
              <td>{error.statusCode}</td>
              <td className={`severity-${error.severity.toLowerCase()}`}>
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
  );
};

export default ErrorTable;

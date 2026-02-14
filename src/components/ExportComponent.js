import React from 'react';

const ExportComponent = ({ errors, filters }) => {
  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  const exportToCSV = () => {
    if (!errors || errors.length === 0) {
      alert('No data to export');
      return;
    }

    const headers = ['ID', 'Application', 'API', 'Status Code', 'Severity', 'Message', 'Timestamp'];
    
    const csvContent = [
      headers.join(','),
      ...errors.map(error => [
        error.id,
        `"${error.applicationName}"`,
        `"${error.apiName}"`,
        error.statusCode,
        `"${error.severity}"`,
        `"${error.message ? error.message.replace(/"/g, '""') : ''}"`,
        `"${formatDate(error.timestamp)}"`
      ].join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `errors-export-${new Date().toISOString().split('T')[0]}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const exportToJSON = () => {
    if (!errors || errors.length === 0) {
      alert('No data to export');
      return;
    }

    const exportData = {
      exportDate: new Date().toISOString(),
      filters: filters || {},
      totalErrors: errors.length,
      errors: errors
    };

    const jsonContent = JSON.stringify(exportData, null, 2);
    const blob = new Blob([jsonContent], { type: 'application/json;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `errors-export-${new Date().toISOString().split('T')[0]}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const printData = () => {
    if (!errors || errors.length === 0) {
      alert('No data to print');
      return;
    }

    const printWindow = window.open('', '_blank');
    const printContent = `
      <html>
        <head>
          <title>Error Report</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            table { border-collapse: collapse; width: 100%; margin-top: 20px; }
            th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
            th { background-color: #f2f2f2; }
            .critical { color: #dc3545; font-weight: bold; }
            .warning { color: #ffc107; font-weight: bold; }
            .info { color: #17a2b8; }
            .header { text-align: center; margin-bottom: 30px; }
            .summary { margin-bottom: 20px; }
          </style>
        </head>
        <body>
          <div class="header">
            <h1>Error Tracking Report</h1>
            <p>Generated on: ${formatDate(new Date())}</p>
          </div>
          
          <div class="summary">
            <h2>Summary</h2>
            <p>Total Errors: ${errors.length}</p>
            <p>Filters Applied: ${Object.keys(filters || {}).length > 0 ? JSON.stringify(filters) : 'None'}</p>
          </div>
          
          <table>
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
              ${errors.map(error => `
                <tr>
                  <td>${error.id}</td>
                  <td>${error.applicationName}</td>
                  <td>${error.apiName}</td>
                  <td>${error.statusCode}</td>
                  <td class="${error.severity.toLowerCase()}">${error.severity}</td>
                  <td>${formatDate(error.timestamp)}</td>
                  <td>${error.message || 'No message'}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </body>
      </html>
    `;
    
    printWindow.document.write(printContent);
    printWindow.document.close();
    printWindow.print();
    printWindow.close();
  };

  return (
    <div className="export-component">
      <h3>Export & Print Options</h3>
      
      <div className="export-buttons">
        <button onClick={exportToCSV} className="export-btn csv-btn">
          📊 Export to CSV
        </button>
        
        <button onClick={exportToJSON} className="export-btn json-btn">
          📄 Export to JSON
        </button>
        
        <button onClick={printData} className="export-btn print-btn">
          🖨️ Print Report
        </button>
      </div>
      
      <div className="export-info">
        <p><strong>Total Errors:</strong> {errors?.length || 0}</p>
        <p><strong>Export Format:</strong> CSV includes all fields, JSON includes full data with filters</p>
      </div>
    </div>
  );
};

export default ExportComponent;

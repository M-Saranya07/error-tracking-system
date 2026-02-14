import React from 'react';

const StatisticsPanel = ({ statistics }) => {
  if (!statistics) {
    return (
      <div className="alert-panel">
        <h3>Statistics</h3>
        <p>Loading statistics...</p>
      </div>
    );
  }

  return (
    <div className="alert-panel">
      <h3>Alert Statistics (Last 24 Hours)</h3>
      <div style={{ display: 'grid', gap: '15px' }}>
        <div style={{ textAlign: 'center' }}>
          <h4 style={{ margin: '0', color: '#dc3545' }}>
            {statistics.totalCount || 0}
          </h4>
          <p>Total Alerts</p>
        </div>
        
        <div style={{ textAlign: 'center' }}>
          <h4 style={{ margin: '0', color: '#dc3545' }}>
            {statistics.criticalCount || 0}
          </h4>
          <p>Critical Alerts</p>
        </div>
        
        <div style={{ textAlign: 'center' }}>
          <h4 style={{ margin: '0', color: '#ffc107' }}>
            {statistics.highFrequencyCount || 0}
          </h4>
          <p>High Frequency Alerts</p>
        </div>
      </div>
    </div>
  );
};

export default StatisticsPanel;

import React from 'react';

const AlertPanel = ({ alerts }) => {
  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  if (!alerts || alerts.length === 0) {
    return (
      <div className="alert-panel">
        <h3>Recent Alerts</h3>
        <p>No recent alerts - system is running smoothly!</p>
      </div>
    );
  }

  return (
    <div className="alert-panel">
      <h3>Recent Alerts ({alerts.length})</h3>
      {alerts.map((alert) => (
        <div 
          key={alert.id} 
          className={`alert-item alert-${alert.alertType.toLowerCase().replace('_', '-')}`}
        >
          <h4>
            {alert.alertType === 'CRITICAL' ? '🚨' : '⚠️'} {alert.alertType}
          </h4>
          <p><strong>To:</strong> {alert.recipientEmail}</p>
          <p><strong>Subject:</strong> {alert.subject}</p>
          <p><strong>Time:</strong> {formatDate(alert.sentAt)}</p>
          <p><strong>Status:</strong> {alert.status}</p>
        </div>
      ))}
    </div>
  );
};

export default AlertPanel;

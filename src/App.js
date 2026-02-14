import React, { useState, useEffect } from 'react';
import PaginatedErrorTable from './components/PaginatedErrorTable';
import EnhancedFilterPanel from './components/EnhancedFilterPanel';
import AlertPanel from './components/AlertPanel';
import StatisticsPanel from './components/StatisticsPanel';
import SearchComponent from './components/SearchComponent';
import ExportComponent from './components/ExportComponent';
import { getErrors, getAlerts, getAlertStatistics, testAlerts, logError } from './services/apiService';
import './App.css';

function App() {
  const [allErrors, setAllErrors] = useState([]);
  const [filteredErrors, setFilteredErrors] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [filters, setFilters] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchField, setSearchField] = useState('all');
  const [isSearching, setIsSearching] = useState(false);
  const [showExport, setShowExport] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [sortBy, setSortBy] = useState('timestamp');

  // Apply filters, search, and sorting on frontend
  const processErrors = (errors, filters, searchTerm, searchField, sortBy) => {
    let processed = [...errors];
    
    // Apply basic filters
    if (filters.statusCode) {
      processed = processed.filter(error => error.statusCode === parseInt(filters.statusCode));
    }
    if (filters.severity) {
      processed = processed.filter(error => error.severity === filters.severity);
    }
    if (filters.applicationName) {
      processed = processed.filter(error => error.applicationName === filters.applicationName);
    }
    
    // Apply search
    if (searchTerm) {
      processed = processed.filter(error => {
        if (searchField === 'all') {
          return Object.values(error).some(value => 
            value && value.toString().toLowerCase().includes(searchTerm.toLowerCase())
          );
        } else {
          return error[searchField] && 
            error[searchField].toString().toLowerCase().includes(searchTerm.toLowerCase());
        }
      });
    }
    
    // Apply sorting
    processed.sort((a, b) => {
      if (sortBy === 'timestamp') {
        return new Date(b.timestamp) - new Date(a.timestamp);
      } else if (sortBy === 'severity') {
        const severityOrder = { 'CRITICAL': 0, 'WARNING': 1, 'INFO': 2, 'LOW': 3 };
        return (severityOrder[a.severity] || 99) - (severityOrder[b.severity] || 99);
      } else {
        return a[sortBy] > b[sortBy] ? 1 : -1;
      }
    });
    
    return processed;
  };

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [errorsData, alertsData, statisticsData] = await Promise.all([
        getErrors(filters),
        testAlerts(),
        getAlertStatistics()
      ]);
      
      setAllErrors(errorsData);
      
      // Process filters, search, and sorting on frontend
      const processed = processErrors(errorsData, filters, searchTerm, searchField, sortBy);
      setFilteredErrors(processed);
      
      // Calculate pagination
      const errorsPerPage = 10;
      const total = processed.length;
      const pages = Math.ceil(total / errorsPerPage);
      setTotalPages(pages);
      setCurrentPage(1);
      
      setAlerts(alertsData);
      setStatistics(statisticsData);
      
    } catch (err) {
      console.error('Failed to load data:', err);
      setError('Failed to load data from backend. Please check if backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (field, value) => {
    console.log('Filter change:', field, value);
    if (field === 'clear') {
      setFilters({});
      setSearchTerm('');
      setFilteredErrors(allErrors);
    } else if (field === 'apply') {
      // Apply filters from the filter panel
      setFilters(value);
      
      // Re-process errors with new filters
      const processed = processErrors(allErrors, value, searchTerm, searchField, sortBy);
      console.log('Applied filters results:', processed.length);
      setFilteredErrors(processed);
      
      // Recalculate pagination
      const errorsPerPage = 10;
      const total = processed.length;
      const pages = Math.ceil(total / errorsPerPage);
      setTotalPages(pages);
      setCurrentPage(1);
    } else {
      const newFilters = { ...filters, [field]: value };
      setFilters(newFilters);
      
      // Re-process errors with new filters
      const processed = processErrors(allErrors, newFilters, searchTerm, searchField, sortBy);
      console.log('Filtered results:', processed.length);
      setFilteredErrors(processed);
      
      // Recalculate pagination
      const errorsPerPage = 10;
      const total = processed.length;
      const pages = Math.ceil(total / errorsPerPage);
      setTotalPages(pages);
      setCurrentPage(1);
    }
  };

  const handleSearch = (searchOptions) => {
    console.log('Search:', searchOptions);
    setSearchTerm(searchOptions.term);
    setSearchField(searchOptions.field);
    setIsSearching(true);
    
    // Re-process errors with new search
    const processed = processErrors(allErrors, filters, searchOptions.term, searchOptions.field, sortBy);
    console.log('Search results:', processed.length);
    setFilteredErrors(processed);
    
    // Recalculate pagination
    const errorsPerPage = 10;
    const total = processed.length;
    const pages = Math.ceil(total / errorsPerPage);
    setTotalPages(pages);
    setCurrentPage(1);
    
    setTimeout(() => setIsSearching(false), 500);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleSort = (field) => {
    console.log('Sort by:', field);
    setSortBy(field);
    
    // Re-process errors with new sort
    const processed = processErrors(allErrors, filters, searchTerm, searchField, field);
    console.log('Sorted results:', processed.length);
    setFilteredErrors(processed);
    
    // Recalculate pagination
    const errorsPerPage = 10;
    const total = processed.length;
    const pages = Math.ceil(total / errorsPerPage);
    setTotalPages(pages);
    setCurrentPage(1);
  };

  const toggleExport = () => {
    setShowExport(!showExport);
  };

  const refreshData = () => {
    loadData();
  };

  useEffect(() => {
    loadData();
    
    // Set up auto-refresh every 30 seconds
    const interval = setInterval(loadData, 30000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="App">
      <header className="app-header">
        <h1>Error Tracking Dashboard</h1>
        <button onClick={refreshData} disabled={loading} className="refresh-btn">
        >
          {loading ? 'Loading...' : '🔄 Refresh Data'}
        </button>
      </header>

      {error && (
        <div className="error">
          <strong>Error:</strong> {error}
        </div>
      )}

      <main className="dashboard">
        <div className="dashboard-left">
          <SearchComponent 
            onSearch={handleSearch}
            placeholder="Search errors..."
          />
          
          <EnhancedFilterPanel 
            filters={filters} 
            onFilterChange={handleFilterChange}
          />
          
          {showExport && (
            <ExportComponent 
              errors={filteredErrors} 
              filters={filters}
            />
          )}
        </div>

        <div className="dashboard-right">
          <PaginatedErrorTable 
            errors={filteredErrors}
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
            onSort={handleSort}
          />
          
          <div className="alerts-stats-container">
            <AlertPanel alerts={alerts} />
            <StatisticsPanel statistics={statistics} />
          </div>
        </div>
      </main>

      {error && (
        <div className="error">
          <strong>Error:</strong> {error}
        </div>
      )}
    </div>
  );
};

export default App;

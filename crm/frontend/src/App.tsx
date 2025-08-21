import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import en_US from 'antd/locale/en_US';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import AdminDashboard from './pages/AdminDashboard';
import Leads from './pages/Leads';
import Emails from './pages/Emails';
import Drafts from './pages/Drafts';
import Users from './pages/Users';
import Layout from './components/Layout';
import './index.css';

const PrivateRoute: React.FC<{ children: React.ReactNode; adminOnly?: boolean }> = ({ 
  children, 
  adminOnly = false 
}) => {
  const { isAuthenticated, user, isLoading, isInitialized } = useAuth();

  if (!isInitialized || isLoading) {
    return <div className="loading-spinner">Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (adminOnly && user?.role !== 'ADMIN') {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

const PublicRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
};

const PrivateRoutes: React.FC = () => {
  const { user } = useAuth();

  return (
    <Layout>
      <Routes>
        <Route 
          path="/" 
          element={
            user?.role === 'ADMIN' ? (
              <Navigate to="/admin" replace />
            ) : (
              <Navigate to="/leads" replace />
            )
          } 
        />
        <Route 
          path="/admin" 
          element={
            <PrivateRoute adminOnly>
              <AdminDashboard />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/users" 
          element={
            <PrivateRoute adminOnly>
              <Users />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/leads" 
          element={
            <PrivateRoute>
              <Leads />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/emails" 
          element={
            <PrivateRoute>
              <Emails />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/drafts" 
          element={
            <PrivateRoute>
              <Drafts />
            </PrivateRoute>
          } 
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
};

const AppRoutes: React.FC = () => {
  const { isAuthenticated, isInitialized, isLoading } = useAuth();

  // Don't render anything until initialization is complete
  if (!isInitialized || isLoading) {
    return <div className="loading-spinner">Loading...</div>;
  }

  // If not authenticated, show public routes (login/register)
  if (!isAuthenticated) {
    return <PublicRoutes />;
  }

  // If authenticated, show private routes
  return <PrivateRoutes />;
};

const App: React.FC = () => {
  return (
    <ConfigProvider locale={en_US}>
      <AuthProvider>
        <Router>
          <AppRoutes />
        </Router>
      </AuthProvider>
    </ConfigProvider>
  );
};

export default App; 
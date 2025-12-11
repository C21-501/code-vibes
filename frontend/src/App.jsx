import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './features/auth/context/AuthContext'
import LoginForm from './features/auth/components/LoginForm'
import UserManagement from './features/users/components/UserManagement'
import TeamManagement from './features/teams/components/TeamManagement'
import SystemManagement from './features/systems/components/SystemManagement'
import RfcManagement from './features/rfc/components/RfcManagement'
import RfcAuditManagement from './features/rfc/components/RfcAuditManagement'
import ProtectedRoute from './features/auth/components/ProtectedRoute'
import Layout from './shared/components/Layout'
import LoadingSpinner from './shared/components/LoadingSpinner'
import './App.css'

/**
 * Main App Component with AuthProvider
 */
function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}

/**
 * App Content with routing logic
 */
function AppContent() {
  const { isAuthenticated, isLoading } = useAuth()

  // Show loading spinner while checking authentication
  if (isLoading) {
    return (
      <div className="app-loading">
        <LoadingSpinner />
        <p>Загрузка...</p>
      </div>
    )
  }

  return (
    <Routes>
      {/* Root route - redirect based on auth status */}
      <Route
        path="/"
        element={
          isAuthenticated
            ? <Navigate to="/rfc" replace />
            : <Navigate to="/login" replace />
        }
      />

      {/* Login page - redirect if already authenticated */}
      <Route
        path="/login"
        element={
          isAuthenticated
            ? <Navigate to="/rfc" replace />
            : <LoginForm />
        }
      />

      {/* Protected routes with Layout */}
      <Route
        path="/rfc"
        element={
          <ProtectedRoute>
            <Layout>
              <RfcManagement />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/users"
        element={
          <ProtectedRoute>
            <Layout>
              <UserManagement />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/teams"
        element={
          <ProtectedRoute>
            <Layout>
              <TeamManagement />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/systems"
        element={
          <ProtectedRoute>
            <Layout>
              <SystemManagement />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/audit-rfc"
        element={
          <ProtectedRoute>
            <Layout>
              <RfcAuditManagement />
            </Layout>
          </ProtectedRoute>
        }
      />

      {/* Catch all - redirect to home */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default App
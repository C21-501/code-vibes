import { Routes, Route, Navigate } from 'react-router-dom'
import LoginForm from './features/auth/components/LoginForm'
import UserManagement from './features/users/components/UserManagement'
import TeamManagement from './features/teams/components/TeamManagement'
import ProtectedRoute from './features/auth/components/ProtectedRoute'
import { isCurrentTokenExpired } from './utils/jwtUtils'
import './App.css'

/**
 * Main App Component
 * 
 * Handles routing between login and user management pages using React Router
 */
function App() {
  // Check if user is authenticated
  const isAuthenticated = () => {
    const token = localStorage.getItem('accessToken');
    return token && !isCurrentTokenExpired();
  };

  return (
    <Routes>
      {/* Root route - redirect based on auth status */}
      <Route 
        path="/" 
        element={
          isAuthenticated() 
            ? <Navigate to="/users" replace /> 
            : <Navigate to="/login" replace />
        } 
      />
      
      {/* Login page */}
      <Route 
        path="/login" 
        element={
          <div className="app">
            <LoginForm />
          </div>
        } 
      />
      
      {/* Protected routes */}
      <Route 
        path="/users" 
        element={
          <ProtectedRoute>
            <div className="app-full-width">
              <UserManagement />
            </div>
          </ProtectedRoute>
        } 
      />
      
      <Route 
        path="/teams" 
        element={
          <ProtectedRoute>
            <div className="app-full-width">
              <TeamManagement />
            </div>
          </ProtectedRoute>
        } 
      />
      
      {/* Catch all - redirect to home */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default App


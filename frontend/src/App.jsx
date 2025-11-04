import { useState } from 'react'
import LoginForm from './components/LoginForm'
import UserManagement from './components/UserManagement'
import './App.css'

/**
 * Main App Component
 * 
 * Handles routing between login and user management pages
 */
function App() {
  // Simple state-based routing (in production, use React Router)
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  // For development/testing, you can set this to true to skip login
  // setIsAuthenticated(true)

  if (!isAuthenticated) {
    return (
      <div className="app">
        <LoginForm onLoginSuccess={() => setIsAuthenticated(true)} />
      </div>
    )
  }

  return <UserManagement />
}

export default App


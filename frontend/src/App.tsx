import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth, ProtectedRoute } from './auth'
import { Layout } from './components/layout'
import { Dashboard, MyRfcs } from './pages'
import './App.css'

function App() {
  const { isAuthenticated, login } = useAuth()

  // Показать страницу входа для неавторизованных пользователей
  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="max-w-md w-full bg-white rounded-lg shadow-md p-8">
          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-900 mb-4">
              Code Vibes
            </h1>
            <p className="text-gray-600 mb-8">
              Система управления RFC (Request for Comments)
            </p>
            <button
              onClick={login}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
            >
              Войти в систему
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <ProtectedRoute>
      <Layout>
        <Routes>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/my-rfcs" element={<MyRfcs />} />
          <Route path="/rfcs" element={<div className="p-6">Все RFC - в разработке</div>} />
          <Route path="/kanban" element={<div className="p-6">Канбан-доска - в разработке</div>} />
          <Route path="/references" element={<div className="p-6">Справочники - в разработке</div>} />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Layout>
    </ProtectedRoute>
  )
}

export default App

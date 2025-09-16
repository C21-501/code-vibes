import { useState } from 'react'
import { useAuth, ProtectedRoute } from './auth'
import { apiClient } from './api'
import './App.css'

function App() {
  const [count, setCount] = useState(0)
  const { isAuthenticated, user, login, logout } = useAuth()

  const handleApiTest = async () => {
    try {
      // Пример запроса к API с автоматическим добавлением токена
      const response = await apiClient.get('/api/test')
      console.log('API Response:', response.data)
    } catch (error) {
      console.error('API Error:', error)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <h1 className="text-3xl font-bold text-gray-900">
              Code Vibes App
            </h1>
            
            {isAuthenticated ? (
              <div className="flex items-center space-x-4">
                <span className="text-gray-700">
                  Привет, {user?.firstName || user?.username || 'Пользователь'}!
                </span>
                <button
                  onClick={logout}
                  className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md"
                >
                  Выйти
                </button>
              </div>
            ) : (
              <button
                onClick={login}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md"
              >
                Войти
              </button>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="border-4 border-dashed border-gray-200 rounded-lg p-8">
            
            {/* Public content */}
            <div className="mb-8">
              <h2 className="text-2xl font-bold mb-4">Публичный контент</h2>
              <p className="text-gray-600 mb-4">
                Этот контент доступен всем пользователям без аутентификации.
              </p>
              <div className="bg-white p-4 rounded-lg shadow">
                <button 
                  onClick={() => setCount((count) => count + 1)}
                  className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
                >
                  Счетчик: {count}
                </button>
              </div>
            </div>

            {/* Protected content */}
            <ProtectedRoute>
              <div className="border-t pt-8">
                <h2 className="text-2xl font-bold mb-4">Защищенный контент</h2>
                <p className="text-gray-600 mb-4">
                  Этот контент доступен только аутентифицированным пользователям.
                </p>
                
                <div className="bg-white p-4 rounded-lg shadow mb-4">
                  <h3 className="text-lg font-semibold mb-2">Информация о пользователе:</h3>
                  <pre className="bg-gray-100 p-3 rounded text-sm overflow-auto">
                    {JSON.stringify(user, null, 2)}
                  </pre>
                </div>

                <div className="bg-white p-4 rounded-lg shadow">
                  <h3 className="text-lg font-semibold mb-2">Тест API:</h3>
                  <button
                    onClick={handleApiTest}
                    className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
                  >
                    Отправить API запрос
                  </button>
                  <p className="text-sm text-gray-500 mt-2">
                    Проверьте консоль браузера для результата
                  </p>
                </div>
              </div>
            </ProtectedRoute>
          </div>
        </div>
      </main>
    </div>
  )
}

export default App

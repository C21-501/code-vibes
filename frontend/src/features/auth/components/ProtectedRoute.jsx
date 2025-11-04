import { Navigate } from 'react-router-dom';
import { isCurrentTokenExpired } from '../../../utils/jwtUtils';

/**
 * ProtectedRoute Component
 * 
 * Protects routes by checking if user has valid JWT token
 * Redirects to /login if token is missing or expired
 */
function ProtectedRoute({ children }) {
  const token = localStorage.getItem('accessToken');
  
  // Check if token exists and is not expired
  if (!token || isCurrentTokenExpired()) {
    // Clear invalid/expired token
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('expiresIn');
    localStorage.removeItem('tokenExpiration');
    localStorage.removeItem('refreshTokenExpiration');
    
    // Redirect to login page
    return <Navigate to="/login" replace />;
  }
  
  // Token is valid, render protected content
  return children;
}

export default ProtectedRoute;


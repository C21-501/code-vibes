import { Navigate, useLocation } from 'react-router-dom';
import { isCurrentTokenExpired } from '../../../utils/jwtUtils';
import { saveReturnUrl, clearAuthData } from '../../../utils/authContext';

/**
 * ProtectedRoute Component
 * 
 * Protects routes by checking if user has valid JWT token
 * Redirects to /login if token is missing or expired
 * Saves current location to return after successful login
 */
function ProtectedRoute({ children }) {
  const location = useLocation();
  const token = localStorage.getItem('accessToken');
  
  // Check if token exists and is not expired
  if (!token || isCurrentTokenExpired()) {
    // Save current location (pathname + search + hash) for return after login
    const returnUrl = location.pathname + location.search + location.hash;
    saveReturnUrl(returnUrl);
    
    // Clear invalid/expired token and auth data
    clearAuthData();
    
    // Redirect to login page
    return <Navigate to="/login" replace />;
  }
  
  // Token is valid, render protected content
  return children;
}

export default ProtectedRoute;


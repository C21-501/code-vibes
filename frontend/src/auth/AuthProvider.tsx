import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import keycloak from '../keycloak';
import type { KeycloakProfile, KeycloakTokenParsed } from 'keycloak-js';

interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  token: string | null;
  user: KeycloakProfile | null;
  login: () => void;
  logout: () => void;
  keycloakInstance: typeof keycloak;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<KeycloakProfile | null>(null);

  useEffect(() => {
    const initKeycloak = async () => {
      try {
        const authenticated = await keycloak.init({
          onLoad: 'check-sso',
          silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
          checkLoginIframe: false,
        });

        setIsAuthenticated(authenticated);
        
        if (authenticated) {
          setToken(keycloak.token || null);
          
          // Load user profile
          try {
            const profile = await keycloak.loadUserProfile();
            setUser(profile);
          } catch (error) {
            console.error('Failed to load user profile:', error);
          }

          // Setup token refresh
          keycloak.onTokenExpired = () => {
            keycloak.updateToken(30).then((refreshed) => {
              if (refreshed) {
                setToken(keycloak.token || null);
                console.log('Token refreshed');
              } else {
                console.log('Token not refreshed, valid for', Math.round(keycloak.tokenParsed?.exp! + keycloak.timeSkew! - new Date().getTime() / 1000) + ' seconds');
              }
            }).catch(() => {
              console.error('Failed to refresh token');
              logout();
            });
          };
        }
      } catch (error) {
        console.error('Failed to initialize Keycloak:', error);
      } finally {
        setIsLoading(false);
      }
    };

    initKeycloak();
  }, []);

  const login = () => {
    keycloak.login();
  };

  const logout = () => {
    setIsAuthenticated(false);
    setToken(null);
    setUser(null);
    keycloak.logout();
  };

  const value: AuthContextType = {
    isAuthenticated,
    isLoading,
    token,
    user,
    login,
    logout,
    keycloakInstance: keycloak,
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

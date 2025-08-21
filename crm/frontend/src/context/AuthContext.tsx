import React, { createContext, useContext, useState, useEffect, useRef, useCallback, ReactNode } from 'react';
import { User } from '../types';
import { authAPI } from '../services/api';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  isInitialized: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (userData: any) => Promise<void>;
  logout: () => void;
  updateUser: (user: User) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isInitialized, setIsInitialized] = useState(false);
  const hasCheckedAuthRef = useRef(false);

  useEffect(() => {
    // Check if user is already logged in by calling /auth/me only once
    const checkAuth = async () => {
      if (hasCheckedAuthRef.current) return; // Prevent multiple calls
      
      hasCheckedAuthRef.current = true; // Mark as checked immediately
      
      try {
        const response = await authAPI.me();
        setUser(response.data.data);
      } catch (error) {
        console.log('User not authenticated');
        setUser(null);
      } finally {
        setIsLoading(false);
        setIsInitialized(true);
      }
    };
    
    // Only check auth if we're not on login/register pages
    const currentPath = window.location.pathname;
    if (currentPath === '/login' || currentPath === '/register') {
      // Skip auth check on login/register pages
      setIsLoading(false);
      setIsInitialized(true);
      hasCheckedAuthRef.current = true;
    } else {
      checkAuth();
    }
  }, []); // Empty dependency array - only run once

  const login = useCallback(async (email: string, password: string) => {
    try {
      setIsLoading(true);
      const response = await authAPI.login({ email, password });
      const { user: userData } = response.data.data;
      
      setUser(userData);
      hasCheckedAuthRef.current = true; // Mark as checked after successful login
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const register = useCallback(async (userData: any) => {
    try {
      setIsLoading(true);
      const response = await authAPI.register(userData);
      const { user: newUser } = response.data.data;
      
      setUser(newUser);
      hasCheckedAuthRef.current = true; // Mark as checked after successful registration
    } catch (error) {
      console.error('Register error:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      setUser(null);
      hasCheckedAuthRef.current = false; // Reset auth check flag on logout
    }
  }, []);

  const updateUser = useCallback((updatedUser: User) => {
    setUser(updatedUser);
  }, []);

  const value: AuthContextType = {
    user,
    isAuthenticated: !!user,
    isLoading,
    isInitialized,
    login,
    register,
    logout,
    updateUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 
import React from 'react';
import { Sidebar } from './Sidebar';
// import { TopBar } from './TopBar';

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div style={{ height: '100vh', backgroundColor: '#f9fafb' }}>
      {/* Sidebar (fixed) */}
      <Sidebar />

      {/* Main content area shifted by sidebar width */}
      <div style={{ marginLeft: '200px', height: '100%', display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
        {/* Page content */}
        <main style={{ flex: 1, overflow: 'auto' }}>
          {children}
        </main>
      </div>
    </div>
  );
};

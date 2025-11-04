/**
 * Toast Component
 * Displays temporary notifications (success, error, etc.)
 */
import { useState, useEffect } from 'react';
import './Toast.css';

export default function Toast({ show, type = 'success', title, message, onClose }) {
  useEffect(() => {
    if (show) {
      const timer = setTimeout(() => {
        onClose && onClose();
      }, 3000);
      
      return () => clearTimeout(timer);
    }
  }, [show, onClose]);

  if (!show) return null;

  return (
    <div className={`toast show ${type}`}>
      <div className="toast-icon">
        {type === 'success' ? '✓' : '✕'}
      </div>
      <div className="toast-content">
        <div className="toast-title">{title}</div>
        <div className="toast-message">{message}</div>
      </div>
    </div>
  );
}


/**
 * Toast Component
 * Displays temporary notifications (success, error, etc.)
 */
import { useState, useEffect, useRef } from 'react';
import './Toast.css';

export default function Toast({ show, type = 'success', title, message, onClose }) {
  const [progress, setProgress] = useState(100);
  const timerRef = useRef(null);
  const progressRef = useRef(null);
  const duration = 3000; // 3 seconds

  useEffect(() => {
    if (show) {
      // Reset progress
      setProgress(100);
      
      // Start progress animation using interval for smoother updates
      const startTime = Date.now();
      const interval = setInterval(() => {
        const elapsed = Date.now() - startTime;
        const remaining = Math.max(0, duration - elapsed);
        const progressPercent = (remaining / duration) * 100;
        setProgress(progressPercent);
        
        if (remaining <= 0) {
          clearInterval(interval);
        }
      }, 16); // ~60fps
      
      progressRef.current = interval;
      
      // Auto close after duration
      timerRef.current = setTimeout(() => {
        onClose && onClose();
      }, duration);
      
      return () => {
        if (timerRef.current) {
          clearTimeout(timerRef.current);
        }
        if (progressRef.current) {
          clearInterval(progressRef.current);
        }
      };
    } else {
      setProgress(100);
    }
  }, [show, onClose]);

  const handleClose = () => {
    if (timerRef.current) {
      clearTimeout(timerRef.current);
    }
    if (progressRef.current) {
      clearInterval(progressRef.current);
    }
    onClose && onClose();
  };

  if (!show) return null;

  return (
    <div className={`toast show ${type}`}>
      <div className="toast-header">
        <div className="toast-icon">
          {type === 'success' ? '✓' : '✕'}
        </div>
        <div className="toast-content">
          <div className="toast-title">{title}</div>
          <div className="toast-message">{message}</div>
        </div>
        <button className="toast-close" onClick={handleClose} aria-label="Закрыть">
          ×
        </button>
      </div>
      <div className="toast-progress-container">
        <div 
          className="toast-progress-bar" 
          style={{ width: `${progress}%` }}
        />
      </div>
    </div>
  );
}


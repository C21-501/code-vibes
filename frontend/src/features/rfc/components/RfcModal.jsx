import React from 'react';
import './RfcModal.css';

const RfcModal = ({ isOpen, onClose, children }) => {
  if (!isOpen) return null;

  return (
    <div className={`modal ${isOpen ? 'active' : ''}`}>
      <div className="modal-content rfc-detail-modal">
        <div className="modal-header">
          <h2>Детали RFC</h2>
          <button className="close" onClick={onClose}>
            ×
          </button>
        </div>
        <div className="modal-body">
          {children}
        </div>
      </div>
    </div>
  );
};

export default RfcModal;
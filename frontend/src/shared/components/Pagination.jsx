/**
 * Pagination Component
 * Displays pagination controls based on PageResponse from API
 */
import './Pagination.css';

export default function Pagination({ 
  currentPage, 
  totalPages, 
  totalElements, 
  pageSize,
  first,
  last,
  onPageChange 
}) {
  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

  // Generate page number buttons (max 5 visible)
  const renderPageNumbers = () => {
    const maxButtons = 5;
    let startPage = Math.max(0, currentPage - Math.floor(maxButtons / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxButtons - 1);
    
    // Adjust if at the end
    if (endPage - startPage < maxButtons - 1) {
      startPage = Math.max(0, endPage - maxButtons + 1);
    }

    const pageButtons = [];

    // First page button
    if (startPage > 0) {
      pageButtons.push(
        <button
          key="first"
          className="pagination-btn"
          onClick={() => onPageChange(0)}
        >
          1
        </button>
      );
      
      if (startPage > 1) {
        pageButtons.push(
          <span key="dots-start" className="pagination-dots">...</span>
        );
      }
    }

    // Page number buttons
    for (let i = startPage; i <= endPage; i++) {
      pageButtons.push(
        <button
          key={i}
          className={`pagination-btn ${i === currentPage ? 'active' : ''}`}
          onClick={() => onPageChange(i)}
        >
          {i + 1}
        </button>
      );
    }

    // Last page button
    if (endPage < totalPages - 1) {
      if (endPage < totalPages - 2) {
        pageButtons.push(
          <span key="dots-end" className="pagination-dots">...</span>
        );
      }
      
      pageButtons.push(
        <button
          key="last"
          className="pagination-btn"
          onClick={() => onPageChange(totalPages - 1)}
        >
          {totalPages}
        </button>
      );
    }

    return pageButtons;
  };

  return (
    <div className="pagination">
      <div className="pagination-info">
        {totalElements === 0 
          ? 'Нет записей для отображения'
          : `Показано ${startItem}-${endItem} из ${totalElements} записей (страница ${currentPage + 1} из ${totalPages})`
        }
      </div>
      <div className="pagination-controls">
        <button
          className="pagination-btn"
          onClick={() => onPageChange(currentPage - 1)}
          disabled={first}
        >
          ← Предыдущая
        </button>
        <div className="page-numbers">
          {renderPageNumbers()}
        </div>
        <button
          className="pagination-btn"
          onClick={() => onPageChange(currentPage + 1)}
          disabled={last}
        >
          Следующая →
        </button>
      </div>
    </div>
  );
}


import React from 'react';
import { FileText, Plus } from 'lucide-react';

export const MyRfcs: React.FC = () => {
  return (
    <div style={{ padding: '20px' }}>
      {/* Header */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        marginBottom: '30px',
        paddingBottom: '15px',
        borderBottom: '1px solid #ddd'
      }}>
        <h1 style={{ 
          fontSize: '24px', 
          fontWeight: '600', 
          display: 'flex', 
          alignItems: 'center', 
          gap: '10px',
          color: '#333'
        }}>
          <FileText size={24} />
          <span>Мои RFC</span>
        </h1>
      </div>

      {/* Filters */}
      <div style={{ 
        display: 'flex', 
        gap: '15px', 
        marginBottom: '20px', 
        flexWrap: 'wrap',
        padding: '15px',
        backgroundColor: 'white',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.05)'
      }}>
        <div style={{ display: 'flex', flexDirection: 'column', minWidth: '200px' }}>
          <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500', fontSize: '14px' }}>Статус RFC</label>
          <select style={{ 
            width: '100%', 
            padding: '10px', 
            border: '1px solid #ddd', 
            borderRadius: '4px', 
            fontSize: '14px',
            backgroundColor: 'white'
          }}>
            <option value="">Все статусы</option>
            <option value="DRAFT">Черновик</option>
            <option value="SUBMITTED">Отправлен</option>
            <option value="UNDER_REVIEW">На рассмотрении</option>
            <option value="APPROVED">Одобрен</option>
            <option value="REJECTED">Отклонен</option>
            <option value="IMPLEMENTED">Внедрен</option>
            <option value="CANCELLED">Отменен</option>
          </select>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', minWidth: '200px' }}>
          <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500', fontSize: '14px' }}>Тип участия</label>
          <select style={{ 
            width: '100%', 
            padding: '10px', 
            border: '1px solid #ddd', 
            borderRadius: '4px', 
            fontSize: '14px',
            backgroundColor: 'white'
          }}>
            <option value="">Все</option>
            <option value="requester">Инициатор</option>
            <option value="executor">Исполнитель</option>
            <option value="reviewer">Согласующий</option>
          </select>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', minWidth: '200px' }}>
          <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500', fontSize: '14px' }}>Система</label>
          <select style={{ 
            width: '100%', 
            padding: '10px', 
            border: '1px solid #ddd', 
            borderRadius: '4px', 
            fontSize: '14px',
            backgroundColor: 'white'
          }}>
            <option value="">Все системы</option>
            <option value="crm">CRM система</option>
            <option value="erp">ERP система</option>
            <option value="db">База данных</option>
            <option value="servers">Серверы</option>
          </select>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', minWidth: '200px' }}>
          <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500', fontSize: '14px' }}>Приоритет</label>
          <select style={{ 
            width: '100%', 
            padding: '10px', 
            border: '1px solid #ddd', 
            borderRadius: '4px', 
            fontSize: '14px',
            backgroundColor: 'white'
          }}>
            <option value="">Все приоритеты</option>
            <option value="LOW">Низкий</option>
            <option value="MEDIUM">Средний</option>
            <option value="HIGH">Высокий</option>
            <option value="CRITICAL">Критический</option>
          </select>
        </div>
      </div>

      {/* Tabs and Table */}
      <div style={{ 
        backgroundColor: 'white', 
        borderRadius: '8px', 
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)', 
        marginBottom: '20px', 
        overflow: 'hidden' 
      }}>
        {/* Tabs */}
        <div style={{ 
          padding: '15px 20px', 
          borderBottom: '1px solid #eee', 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center' 
        }}>
          <div style={{ display: 'flex', gap: '5px', flexWrap: 'wrap' }}>
            <button style={{ 
              padding: '8px 16px', 
              background: '#3498db', 
              border: 'none', 
              borderRadius: '4px', 
              cursor: 'pointer', 
              fontSize: '14px',
              color: 'white',
              fontWeight: '500'
            }}>
              Все RFC
            </button>
            <button style={{ 
              padding: '8px 16px', 
              background: '#f8f9fa', 
              border: 'none', 
              borderRadius: '4px', 
              cursor: 'pointer', 
              fontSize: '14px',
              color: '#333'
            }}>
              Черновики
            </button>
            <button style={{ 
              padding: '8px 16px', 
              background: '#f8f9fa', 
              border: 'none', 
              borderRadius: '4px', 
              cursor: 'pointer', 
              fontSize: '14px',
              color: '#333'
            }}>
              Отправленные
            </button>
            <button style={{ 
              padding: '8px 16px', 
              background: '#f8f9fa', 
              border: 'none', 
              borderRadius: '4px', 
              cursor: 'pointer', 
              fontSize: '14px',
              color: '#333'
            }}>
              На рассмотрении
            </button>
            <button style={{ 
              padding: '8px 16px', 
              background: '#f8f9fa', 
              border: 'none', 
              borderRadius: '4px', 
              cursor: 'pointer', 
              fontSize: '14px',
              color: '#333'
            }}>
              Одобренные
            </button>
            <button style={{ 
              padding: '8px 16px', 
              background: '#f8f9fa', 
              border: 'none', 
              borderRadius: '4px', 
              cursor: 'pointer', 
              fontSize: '14px',
              color: '#333'
            }}>
              Внедренные
            </button>
          </div>
          <div style={{ display: 'flex', background: '#f8f9fa', borderRadius: '4px', overflow: 'hidden' }}>
            <button style={{ 
              padding: '8px 12px', 
              background: '#3498db', 
              border: 'none', 
              cursor: 'pointer', 
              display: 'flex', 
              alignItems: 'center', 
              justifyContent: 'center',
              color: 'white'
            }}>
              📊
            </button>
          </div>
        </div>

        {/* Table */}
        <div style={{ padding: '20px' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>ID</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Название</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Дата создания</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Статус RFC</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Приоритет</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Уровень риска</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Системы</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Исполнители</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Моя роль</th>
                <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Действия</th>
              </tr>
            </thead>
            <tbody>
              <tr style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '12px 15px' }}>RFC-0042</td>
                <td style={{ padding: '12px 15px' }}>Обновление БД до версии 12.2</td>
                <td style={{ padding: '12px 15px' }}>15.05.2023</td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '6px 12px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    display: 'inline-flex', 
                    alignItems: 'center', 
                    gap: '5px',
                    backgroundColor: '#fff8e1',
                    color: '#f57c00'
                  }}>
                    ⏰ На рассмотрении
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '4px 8px', 
                    borderRadius: '12px', 
                    fontSize: '11px', 
                    fontWeight: '500',
                    backgroundColor: '#ffe0b2',
                    color: '#ef6c00'
                  }}>
                    HIGH
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '4px 8px', 
                    borderRadius: '12px', 
                    fontSize: '11px', 
                    fontWeight: '500',
                    backgroundColor: '#fff8e1',
                    color: '#f57c00'
                  }}>
                    MEDIUM
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>CRM</span>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>База данных</span>
                  </div>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>
                      DBA <span style={{ 
                        padding: '2px 4px', 
                        borderRadius: '8px', 
                        fontSize: '10px', 
                        fontWeight: '500',
                        backgroundColor: '#e8f5e9',
                        color: '#2e7d32'
                      }}>✓</span>
                    </span>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>
                      DevOps <span style={{ 
                        padding: '2px 4px', 
                        borderRadius: '8px', 
                        fontSize: '10px', 
                        fontWeight: '500',
                        backgroundColor: '#fff8e1',
                        color: '#f57c00'
                      }}>?</span>
                    </span>
                  </div>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500',
                    backgroundColor: '#ecf0f1',
                    color: '#34495e'
                  }}>
                    Инициатор
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <button style={{ 
                    padding: '5px 10px', 
                    fontSize: '12px', 
                    backgroundColor: 'transparent', 
                    border: '1px solid #ddd', 
                    borderRadius: '4px', 
                    cursor: 'pointer' 
                  }}>
                    👁️ Просмотр
                  </button>
                </td>
              </tr>
              <tr style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '12px 15px' }}>RFC-0041</td>
                <td style={{ padding: '12px 15px' }}>Патчинг серверов VMware</td>
                <td style={{ padding: '12px 15px' }}>14.05.2023</td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '6px 12px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    display: 'inline-flex', 
                    alignItems: 'center', 
                    gap: '5px',
                    backgroundColor: '#e8f5e9',
                    color: '#2e7d32'
                  }}>
                    ✅ Одобрен
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '4px 8px', 
                    borderRadius: '12px', 
                    fontSize: '11px', 
                    fontWeight: '500',
                    backgroundColor: '#fff8e1',
                    color: '#f57c00'
                  }}>
                    MEDIUM
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '4px 8px', 
                    borderRadius: '12px', 
                    fontSize: '11px', 
                    fontWeight: '500',
                    backgroundColor: '#e8f5e9',
                    color: '#2e7d32'
                  }}>
                    LOW
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Серверы</span>
                  </div>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>
                      DevOps <span style={{ 
                        padding: '2px 4px', 
                        borderRadius: '8px', 
                        fontSize: '10px', 
                        fontWeight: '500',
                        backgroundColor: '#e8f5e9',
                        color: '#2e7d32'
                      }}>✓</span>
                    </span>
                  </div>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500',
                    backgroundColor: '#ecf0f1',
                    color: '#34495e'
                  }}>
                    Исполнитель
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <button style={{ 
                    padding: '5px 10px', 
                    fontSize: '12px', 
                    backgroundColor: 'transparent', 
                    border: '1px solid #ddd', 
                    borderRadius: '4px', 
                    cursor: 'pointer',
                    marginRight: '5px'
                  }}>
                    👁️ Просмотр
                  </button>
                  <button style={{ 
                    padding: '5px 10px', 
                    fontSize: '12px', 
                    backgroundColor: '#2ecc71', 
                    color: 'white',
                    border: 'none', 
                    borderRadius: '4px', 
                    cursor: 'pointer' 
                  }}>
                    ✅ Подтвердить
                  </button>
                </td>
              </tr>
              <tr>
                <td style={{ padding: '12px 15px' }}>RFC-0040</td>
                <td style={{ padding: '12px 15px' }}>Обновление сертификатов безопасности</td>
                <td style={{ padding: '12px 15px' }}>13.05.2023</td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '6px 12px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    display: 'inline-flex', 
                    alignItems: 'center', 
                    gap: '5px',
                    backgroundColor: '#e8f5e9',
                    color: '#2e7d32'
                  }}>
                    ✅ Внедрен
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '4px 8px', 
                    borderRadius: '12px', 
                    fontSize: '11px', 
                    fontWeight: '500',
                    backgroundColor: '#fff8e1',
                    color: '#f57c00'
                  }}>
                    MEDIUM
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '4px 8px', 
                    borderRadius: '12px', 
                    fontSize: '11px', 
                    fontWeight: '500',
                    backgroundColor: '#ffe0b2',
                    color: '#ef6c00'
                  }}>
                    HIGH
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Безопасность</span>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Серверы</span>
                  </div>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>
                      ИБ <span style={{ 
                        padding: '2px 4px', 
                        borderRadius: '8px', 
                        fontSize: '10px', 
                        fontWeight: '500',
                        backgroundColor: '#e8f5e9',
                        color: '#2e7d32'
                      }}>✓</span>
                    </span>
                    <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>
                      Сисадмины <span style={{ 
                        padding: '2px 4px', 
                        borderRadius: '8px', 
                        fontSize: '10px', 
                        fontWeight: '500',
                        backgroundColor: '#e8f5e9',
                        color: '#2e7d32'
                      }}>✓</span>
                    </span>
                  </div>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500',
                    backgroundColor: '#ecf0f1',
                    color: '#34495e'
                  }}>
                    Согласующий
                  </span>
                </td>
                <td style={{ padding: '12px 15px' }}>
                  <button style={{ 
                    padding: '5px 10px', 
                    fontSize: '12px', 
                    backgroundColor: 'transparent', 
                    border: '1px solid #ddd', 
                    borderRadius: '4px', 
                    cursor: 'pointer' 
                  }}>
                    👁️ Просмотр
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center', 
          marginTop: '20px',
          padding: '0 20px 20px'
        }}>
          <div style={{ fontSize: '13px', color: '#95a5a6' }}>Показано 3 из 23 записей</div>
          <div style={{ display: 'flex', gap: '5px' }}>
            <button style={{ 
              padding: '5px 10px', 
              fontSize: '12px', 
              backgroundColor: 'transparent', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              cursor: 'pointer' 
            }}>
              ◀️
            </button>
            <button style={{ 
              padding: '5px 10px', 
              fontSize: '12px', 
              backgroundColor: '#3498db', 
              color: 'white',
              border: 'none', 
              borderRadius: '4px', 
              cursor: 'pointer' 
            }}>
              1
            </button>
            <button style={{ 
              padding: '5px 10px', 
              fontSize: '12px', 
              backgroundColor: 'transparent', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              cursor: 'pointer' 
            }}>
              2
            </button>
            <button style={{ 
              padding: '5px 10px', 
              fontSize: '12px', 
              backgroundColor: 'transparent', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              cursor: 'pointer' 
            }}>
              3
            </button>
            <button style={{ 
              padding: '5px 10px', 
              fontSize: '12px', 
              backgroundColor: 'transparent', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              cursor: 'pointer' 
            }}>
              4
            </button>
            <button style={{ 
              padding: '5px 10px', 
              fontSize: '12px', 
              backgroundColor: 'transparent', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              cursor: 'pointer' 
            }}>
              5
            </button>
            <button style={{ 
              padding: '5px 10px', 
              fontSize: '12px', 
              backgroundColor: 'transparent', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              cursor: 'pointer' 
            }}>
              ▶️
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
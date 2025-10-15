import { Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from './components/layout'
import { Home } from 'lucide-react'
import { MyRfcs } from './pages/MyRfcs'
import './App.css'

function Dashboard() {
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
          <Home size={24} />
          <span>Дашборд</span>
        </h1>
      </div>

        {/* Stats Container */}
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', 
          gap: '20px', 
          marginBottom: '30px' 
        }}>
          <div style={{ 
            backgroundColor: 'white', 
            borderRadius: '8px', 
            padding: '20px', 
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            textAlign: 'center',
            borderLeft: '4px solid #3498db',
            cursor: 'pointer',
            transition: 'transform 0.3s ease'
          }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', marginBottom: '10px', color: '#333' }}>12</div>
            <div style={{ color: '#95a5a6', fontSize: '14px' }}>Новые</div>
          </div>
          <div style={{ 
            backgroundColor: 'white', 
            borderRadius: '8px', 
            padding: '20px', 
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            textAlign: 'center',
            borderLeft: '4px solid #f39c12',
            cursor: 'pointer',
            transition: 'transform 0.3s ease'
          }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', marginBottom: '10px', color: '#333' }}>8</div>
            <div style={{ color: '#95a5a6', fontSize: '14px' }}>На согласовании</div>
          </div>
          <div style={{ 
            backgroundColor: 'white', 
            borderRadius: '8px', 
            padding: '20px', 
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            textAlign: 'center',
            borderLeft: '4px solid #2ecc71',
            cursor: 'pointer',
            transition: 'transform 0.3s ease'
          }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', marginBottom: '10px', color: '#333' }}>5</div>
            <div style={{ color: '#95a5a6', fontSize: '14px' }}>Ожидают выполнения</div>
          </div>
          <div style={{ 
            backgroundColor: 'white', 
            borderRadius: '8px', 
            padding: '20px', 
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            textAlign: 'center',
            borderLeft: '4px solid #e74c3c',
            cursor: 'pointer',
            transition: 'transform 0.3s ease'
          }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', marginBottom: '10px', color: '#333' }}>3</div>
            <div style={{ color: '#95a5a6', fontSize: '14px' }}>Отклоненные</div>
          </div>
          <div style={{ 
            backgroundColor: 'white', 
            borderRadius: '8px', 
            padding: '20px', 
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            textAlign: 'center',
            borderLeft: '4px solid #9b59b6',
            cursor: 'pointer',
            transition: 'transform 0.3s ease'
          }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', marginBottom: '10px', color: '#333' }}>23</div>
            <div style={{ color: '#95a5a6', fontSize: '14px' }}>Завершённые</div>
          </div>
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
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500', fontSize: '14px' }}>Статус</label>
            <select style={{ 
              width: '100%', 
              padding: '10px', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              fontSize: '14px',
              backgroundColor: 'white'
            }}>
              <option>Все статусы</option>
              <option>Новые</option>
              <option>На согласовании</option>
              <option>Ожидают выполнения</option>
              <option>Отклоненные</option>
              <option>Завершённые</option>
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
              <option>Все системы</option>
              <option>CRM система</option>
              <option>ERP система</option>
              <option>База данных</option>
              <option>Серверы</option>
              <option>Сетевое оборудование</option>
            </select>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', minWidth: '200px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500', fontSize: '14px' }}>Дата создания (после)</label>
            <input type="date" style={{ 
              width: '100%', 
              padding: '10px', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              fontSize: '14px'
            }} />
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', minWidth: '200px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500', fontSize: '14px' }}>Дата создания (до)</label>
            <input type="date" style={{ 
              width: '100%', 
              padding: '10px', 
              border: '1px solid #ddd', 
              borderRadius: '4px', 
              fontSize: '14px'
            }} />
          </div>
        </div>

        {/* Recent RFCs Table */}
        <div style={{ 
          backgroundColor: 'white', 
          borderRadius: '8px', 
          boxShadow: '0 2px 10px rgba(0,0,0,0.1)', 
          marginBottom: '20px', 
          overflow: 'hidden' 
        }}>
          <div style={{ 
            padding: '15px 20px', 
            borderBottom: '1px solid #eee', 
            fontWeight: '600', 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center' 
          }}>
            <span>Последние RFC</span>
            <a href="#" style={{ color: '#3498db', textDecoration: 'none' }}>Показать все</a>
          </div>
          <div style={{ padding: '20px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>ID</th>
                  <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Название</th>
                  <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Дата создания</th>
                  <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Статус</th>
                  <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Системы</th>
                  <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Исполнители</th>
                  <th style={{ padding: '12px 15px', textAlign: 'left', borderBottom: '1px solid #eee', backgroundColor: '#f8f9fa', fontWeight: '600' }}>Ревьюеры</th>
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
                      ⏰ Ожидают выполнения
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
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>DBA</span>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>DevOps</span>
                    </div>
                  </td>
                  <td style={{ padding: '12px 15px' }}>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Архитекторы</span>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>ИБ</span>
                    </div>
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
                      ✅ На согласовании
                    </span>
                  </td>
                  <td style={{ padding: '12px 15px' }}>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Серверы</span>
                    </div>
                  </td>
                  <td style={{ padding: '12px 15px' }}>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>DevOps</span>
                    </div>
                  </td>
                  <td style={{ padding: '12px 15px' }}>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Архитекторы</span>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>ИБ</span>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Руководство</span>
                    </div>
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
                      ✅ Завершённые
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
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>ИБ</span>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Сисадмины</span>
                    </div>
                  </td>
                  <td style={{ padding: '12px 15px' }}>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>ИБ</span>
                      <span style={{ backgroundColor: '#f0f8ff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', color: '#3498db' }}>Руководство</span>
                    </div>
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
        </div>

        {/* Calendar Section */}
        <div style={{ 
          backgroundColor: 'white', 
          borderRadius: '8px', 
          boxShadow: '0 2px 10px rgba(0,0,0,0.1)', 
          marginBottom: '20px', 
          overflow: 'hidden' 
        }}>
          <div style={{ 
            padding: '15px 20px', 
            borderBottom: '1px solid #eee', 
            fontWeight: '600', 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center' 
          }}>
            <span>Ближайшие запланированные обновления</span>
            <span style={{ 
              padding: '3px 8px', 
              borderRadius: '20px', 
              fontSize: '12px', 
              fontWeight: '500', 
              backgroundColor: '#3498db', 
              color: 'white' 
            }}>
              18 мая - 22 мая
            </span>
          </div>
          <div style={{ padding: '20px' }}>
            <div style={{ 
              display: 'flex', 
              overflowX: 'auto', 
              gap: '15px', 
              padding: '10px 0' 
            }}>
              <div style={{ 
                minWidth: '200px', 
                backgroundColor: 'white', 
                borderRadius: '8px', 
                padding: '15px', 
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)', 
                borderLeft: '4px solid #3498db' 
              }}>
                <div style={{ fontWeight: 'bold', marginBottom: '10px', display: 'flex', justifyContent: 'space-between' }}>
                  <span>18 мая</span>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    backgroundColor: '#ecf0f1', 
                    color: '#34495e' 
                  }}>
                    3 RFC
                  </span>
                </div>
                <div style={{ fontSize: '13px', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '5px' }}>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    backgroundColor: '#3498db', 
                    color: 'white' 
                  }}>
                    RFC-0042
                  </span> Обновление БД
                </div>
                <div style={{ fontSize: '13px', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '5px' }}>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    backgroundColor: '#3498db', 
                    color: 'white' 
                  }}>
                    RFC-0038
                  </span> Обновление ОС
                </div>
                <div style={{ fontSize: '13px', display: 'flex', alignItems: 'center', gap: '5px' }}>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    backgroundColor: '#3498db', 
                    color: 'white' 
                  }}>
                    RFC-0035
                  </span> Замена сетевого оборудования
                </div>
              </div>
              <div style={{ 
                minWidth: '200px', 
                backgroundColor: 'white', 
                borderRadius: '8px', 
                padding: '15px', 
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)', 
                borderLeft: '4px solid #3498db' 
              }}>
                <div style={{ fontWeight: 'bold', marginBottom: '10px', display: 'flex', justifyContent: 'space-between' }}>
                  <span>19 мая</span>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    backgroundColor: '#ecf0f1', 
                    color: '#34495e' 
                  }}>
                    1 RFC
                  </span>
                </div>
                <div style={{ fontSize: '13px', display: 'flex', alignItems: 'center', gap: '5px' }}>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    backgroundColor: '#3498db', 
                    color: 'white' 
                  }}>
                    RFC-0035
                  </span> Замена сетевого оборудования
                </div>
              </div>
              <div style={{ 
                minWidth: '200px', 
                backgroundColor: 'white', 
                borderRadius: '8px', 
                padding: '15px', 
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)', 
                borderLeft: '4px solid #2ecc71' 
              }}>
                <div style={{ fontWeight: 'bold', marginBottom: '10px', display: 'flex', justifyContent: 'space-between' }}>
                  <span>22 мая</span>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    backgroundColor: '#ecf0f1', 
                    color: '#34495e' 
                  }}>
                    1 RFC
                  </span>
                </div>
                <div style={{ fontSize: '13px', display: 'flex', alignItems: 'center', gap: '5px' }}>
                  <span style={{ 
                    padding: '3px 8px', 
                    borderRadius: '20px', 
                    fontSize: '12px', 
                    fontWeight: '500', 
                    backgroundColor: '#3498db', 
                    color: 'white' 
                  }}>
                    RFC-0041
                  </span> Патчинг серверов
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
  );
}

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/my-rfcs" element={<MyRfcs />} />
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Layout>
  );
}

export default App

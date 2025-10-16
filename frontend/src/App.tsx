import { Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from './components/layout'
import { MyRfcs } from './pages/MyRfcs'
import './App.css'

function Dashboard() {
  return (
    <>
      {/* Header */}
      <div className="header">
        <h1 className="page-title">
          <i className="fas fa-th-large"></i>
          <span>Дашборд</span>
        </h1>
        <div className="user-info">
          <div className="user-role">Исполнитель</div>
          <div className="user-avatar">ИП</div>
          <div>Иван Петров</div>
          <button className="btn btn-primary" id="createRfcBtn">
            <i className="fas fa-plus"></i> Создать RFC
          </button>
        </div>
      </div>

      {/* Stats Container */}
      <div className="stats-container">
        <div className="stat-card" style={{borderLeft: '4px solid #3498db'}}>
          <div className="stat-value">12</div>
          <div className="stat-label">Новые</div>
        </div>
        <div className="stat-card" style={{borderLeft: '4px solid #f39c12'}}>
          <div className="stat-value">8</div>
          <div className="stat-label">На согласовании</div>
        </div>
        <div className="stat-card" style={{borderLeft: '4px solid #2ecc71'}}>
          <div className="stat-value">5</div>
          <div className="stat-label">Ожидают выполнения</div>
        </div>
        <div className="stat-card" style={{borderLeft: '4px solid #e74c3c'}}>
          <div className="stat-value">3</div>
          <div className="stat-label">Отклоненные</div>
        </div>
        <div className="stat-card" style={{borderLeft: '4px solid #9b59b6'}}>
          <div className="stat-value">23</div>
          <div className="stat-label">Завершённые</div>
        </div>
      </div>

      {/* Filters */}
      <div className="filters">
        <div className="filter-item">
          <label className="form-label">Статус</label>
          <select className="form-control">
            <option>Все статусы</option>
            <option>Новые</option>
            <option>На согласовании</option>
            <option>Ожидают выполнения</option>
            <option>Отклоненные</option>
            <option>Завершённые</option>
          </select>
        </div>
        <div className="filter-item">
          <label className="form-label">Система</label>
          <select className="form-control">
            <option>Все системы</option>
            <option>CRM система</option>
            <option>ERP система</option>
            <option>База данных</option>
            <option>Серверы</option>
            <option>Сетевое оборудование</option>
          </select>
        </div>
        <div className="filter-item">
          <label className="form-label">Дата создания (после)</label>
          <input type="date" className="form-control" />
        </div>
        <div className="filter-item">
          <label className="form-label">Дата создания (до)</label>
          <input type="date" className="form-control" />
        </div>
      </div>

      {/* Recent RFCs Table */}
      <div className="card">
        <div className="card-header">
          <span>Последние RFC</span>
          <a href="#" style={{ color: '#3498db', textDecoration: 'none' }}>Показать все</a>
        </div>
        <div className="card-body">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Название</th>
                <th>Дата создания</th>
                <th>Статус</th>
                <th>Системы</th>
                <th>Исполнители</th>
                <th>Ревьюеры</th>
                <th>Действия</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>RFC-0042</td>
                <td>Обновление БД до версии 12.2</td>
                <td>15.05.2023</td>
                <td><span className="status status-waiting"><i className="fas fa-clock"></i> Ожидают выполнения</span></td>
                <td>
                  <div className="related-items">
                    <span className="related-item">CRM</span>
                    <span className="related-item">База данных</span>
                  </div>
                </td>
                <td>
                  <div className="related-items">
                    <span className="related-item">DBA</span>
                    <span className="related-item">DevOps</span>
                  </div>
                </td>
                <td>
                  <div className="related-items">
                    <span className="related-item">Архитекторы</span>
                    <span className="related-item">ИБ</span>
                  </div>
                </td>
                <td>
                  <button className="btn" style={{padding: '5px 10px', fontSize: '12px'}}>
                    <i className="fas fa-eye"></i> Просмотр
                  </button>
                </td>
              </tr>
              <tr>
                <td>RFC-0041</td>
                <td>Патчинг серверов VMware</td>
                <td>14.05.2023</td>
                <td><span className="status status-approved"><i className="fas fa-check-circle"></i> На согласовании</span></td>
                <td>
                  <div className="related-items">
                    <span className="related-item">Серверы</span>
                  </div>
                </td>
                <td>
                  <div className="related-items">
                    <span className="related-item">DevOps</span>
                  </div>
                </td>
                <td>
                  <div className="related-items">
                    <span className="related-item">Архитекторы</span>
                    <span className="related-item">ИБ</span>
                    <span className="related-item">Руководство</span>
                  </div>
                </td>
                <td>
                  <button className="btn" style={{padding: '5px 10px', fontSize: '12px'}}>
                    <i className="fas fa-eye"></i> Просмотр
                  </button>
                </td>
              </tr>
              <tr>
                <td>RFC-0040</td>
                <td>Обновление сертификатов безопасности</td>
                <td>13.05.2023</td>
                <td><span className="status status-done"><i className="fas fa-check-double"></i> Завершённые</span></td>
                <td>
                  <div className="related-items">
                    <span className="related-item">Безопасность</span>
                    <span className="related-item">Серверы</span>
                  </div>
                </td>
                <td>
                  <div className="related-items">
                    <span className="related-item">ИБ</span>
                    <span className="related-item">Сисадмины</span>
                  </div>
                </td>
                <td>
                  <div className="related-items">
                    <span className="related-item">ИБ</span>
                    <span className="related-item">Руководство</span>
                  </div>
                </td>
                <td>
                  <button className="btn" style={{padding: '5px 10px', fontSize: '12px'}}>
                    <i className="fas fa-eye"></i> Просмотр
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* Calendar Section */}
      <div className="card">
        <div className="card-header">
          <span>Ближайшие запланированные обновления</span>
          <span className="badge badge-primary">18 мая - 22 мая</span>
        </div>
        <div className="card-body">
          <div className="calendar-section">
            <div className="calendar-day">
              <div className="calendar-day-header">
                <span>18 мая</span>
                <span className="badge badge-light">3 RFC</span>
              </div>
              <div className="calendar-event">
                <span className="badge badge-primary">RFC-0042</span> Обновление БД
              </div>
              <div className="calendar-event">
                <span className="badge badge-primary">RFC-0038</span> Обновление ОС
              </div>
              <div className="calendar-event">
                <span className="badge badge-primary">RFC-0035</span> Замена сетевого оборудования
              </div>
            </div>
            <div className="calendar-day">
              <div className="calendar-day-header">
                <span>19 мая</span>
                <span className="badge badge-light">1 RFC</span>
              </div>
              <div className="calendar-event">
                <span className="badge badge-primary">RFC-0035</span> Замена сетевого оборудования
              </div>
            </div>
            <div className="calendar-day today">
              <div className="calendar-day-header">
                <span>22 мая</span>
                <span className="badge badge-light">1 RFC</span>
              </div>
              <div className="calendar-event">
                <span className="badge badge-primary">RFC-0041</span> Патчинг серверов
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
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
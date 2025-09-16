import { useState } from 'react';
import { SystemsTab } from '@/components/references/SystemsTab';
import { TeamsTab } from '@/components/references/TeamsTab';
import { UsersTab } from '@/components/references/UsersTab';

type TabType = 'systems' | 'teams' | 'users';

export function References() {
  const [activeTab, setActiveTab] = useState<TabType>('systems');

  const tabs = [
    { id: 'systems' as const, label: 'Системы', icon: '🔧' },
    { id: 'teams' as const, label: 'Команды', icon: '👥' },
    { id: 'users' as const, label: 'Пользователи', icon: '👤' },
  ];

  const renderTabContent = () => {
    switch (activeTab) {
      case 'systems':
        return <SystemsTab />;
      case 'teams':
        return <TeamsTab />;
      case 'users':
        return <UsersTab />;
      default:
        return null;
    }
  };

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Справочники</h1>
        <p className="text-gray-600 mt-1">Управление системами, командами и пользователями</p>
      </div>

      {/* Tab Navigation */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="-mb-px flex space-x-8">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`py-2 px-1 border-b-2 font-medium text-sm transition-colors ${
                activeTab === tab.id
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              <span className="mr-2">{tab.icon}</span>
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Tab Content */}
      <div className="min-h-[600px]">
        {renderTabContent()}
      </div>
    </div>
  );
}

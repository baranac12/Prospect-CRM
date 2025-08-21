import React, { useState } from 'react';
import { Layout as AntLayout, Menu, Button, Dropdown, Avatar, Space, Typography } from 'antd';
import { 
  MenuFoldOutlined, 
  MenuUnfoldOutlined, 
  DashboardOutlined,
  UserOutlined,
  TeamOutlined,
  MailOutlined,
  FileTextOutlined,
  SettingOutlined,
  LogoutOutlined,
  BellOutlined
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const { Header, Sider, Content } = AntLayout;
const { Text } = Typography;

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const [collapsed, setCollapsed] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleMenuClick = (key: string) => {
    navigate(key);
  };

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: 'Profile',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: 'Settings',
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Logout',
      onClick: handleLogout,
    },
  ];

  const getMenuItems = () => {
    const items = [];

    if (user?.role === 'ADMIN') {
      items.push({
        key: '/admin',
        icon: <DashboardOutlined />,
        label: 'Admin Dashboard',
      });
      items.push({
        key: '/users',
        icon: <TeamOutlined />,
        label: 'Users',
      });
    }

    items.push(
      {
        key: '/leads',
        icon: <UserOutlined />,
        label: 'Leads',
      },
      {
        key: '/emails',
        icon: <MailOutlined />,
        label: 'Emails',
      },
      {
        key: '/drafts',
        icon: <FileTextOutlined />,
        label: 'Drafts',
      }
    );

    return items;
  };

  const selectedKey = location.pathname;

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed} theme="dark">
        <div className="p-4 text-center">
          <h1 className={`text-white font-bold ${collapsed ? 'text-sm' : 'text-lg'}`}>
            {collapsed ? 'CRM' : 'Prospect CRM'}
          </h1>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={getMenuItems()}
          onClick={({ key }) => handleMenuClick(key)}
        />
      </Sider>
      <AntLayout>
        <Header className="flex items-center justify-between px-6 bg-white shadow-sm">
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            className="text-lg"
          />
          <div className="flex items-center space-x-4">
            <Button
              type="text"
              icon={<BellOutlined />}
              className="text-lg"
            />
            <Dropdown
              menu={{ items: userMenuItems }}
              placement="bottomRight"
              arrow
              trigger={['click']}
            >
              <Space className="cursor-pointer">
                <Avatar icon={<UserOutlined />} />
                <div className="hidden md:block">
                  <Text strong>{user?.name} {user?.surname}</Text>
                  <br />
                  <Text type="secondary" className="text-xs">
                    {user?.role === 'ADMIN' ? 'Admin' : 'User'}
                  </Text>
                </div>
              </Space>
            </Dropdown>
          </div>
        </Header>
        <Content className="bg-gray-50">
          {children}
        </Content>
      </AntLayout>
    </AntLayout>
  );
};

export default Layout; 
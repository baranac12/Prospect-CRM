import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Table, Tag, Space, Button, message, Spin } from 'antd';
import { 
  UserOutlined, 
  MailOutlined, 
  TeamOutlined, 
  WarningOutlined,
  RobotOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined
} from '@ant-design/icons';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { adminAPI } from '../services/api';
import { DashboardData, SystemLog, Robot } from '../types';

const AdminDashboard: React.FC = () => {
  const [dashboardData, setDashboardData] = useState<DashboardData | null>(null);
  const [warningLogs, setWarningLogs] = useState<SystemLog[]>([]);
  const [robots, setRobots] = useState<Robot[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
    fetchWarningLogs();
    fetchRobots();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const response = await adminAPI.getDashboard();
      setDashboardData(response.data.data);
    } catch (error) {
      message.error('Failed to load dashboard data!');
    }
  };

  const fetchWarningLogs = async () => {
    try {
      const response = await adminAPI.getLogs(0, 10, 'WARN');
      setWarningLogs(response.data.data.content);
    } catch (error) {
      message.error('Failed to load warning logs!');
    }
  };

  const fetchRobots = async () => {
    try {
      // Mock robot data - gerçek API'de robot endpoint'i olacak
      const mockRobots: Robot[] = [
        {
          id: 1,
          name: 'Email Robot',
          type: 'EMAIL_PROCESSOR',
          isActive: true,
          lastRunAt: '2024-01-01T10:30:00',
          status: 'RUNNING',
          config: {}
        },
        {
          id: 2,
          name: 'Lead Robot',
          type: 'LEAD_PROCESSOR',
          isActive: false,
          lastRunAt: '2024-01-01T09:15:00',
          status: 'STOPPED',
          config: {}
        },
        {
          id: 3,
          name: 'Bounce Robot',
          type: 'BOUNCE_PROCESSOR',
          isActive: true,
          lastRunAt: '2024-01-01T11:00:00',
          status: 'RUNNING',
          config: {}
        }
      ];
      setRobots(mockRobots);
    } catch (error) {
      message.error('Failed to load robot data!');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'RUNNING':
        return 'green';
      case 'STOPPED':
        return 'red';
      case 'ERROR':
        return 'orange';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'RUNNING':
        return <CheckCircleOutlined />;
      case 'STOPPED':
        return <CloseCircleOutlined />;
      case 'ERROR':
        return <WarningOutlined />;
      default:
        return <ClockCircleOutlined />;
    }
  };

  const warningLogsColumns = [
    {
      title: 'Date',
      dataIndex: 'timestamp',
      key: 'timestamp',
      render: (timestamp: string) => new Date(timestamp).toLocaleString(),
    },
    {
      title: 'Message',
      dataIndex: 'message',
      key: 'message',
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => (
        <Tag color={type === 'SECURITY' ? 'red' : type === 'BUSINESS' ? 'blue' : 'orange'}>
          {type}
        </Tag>
      ),
    },
    {
      title: 'IP Address',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
    },
  ];

  const robotsColumns = [
    {
      title: 'Robot Name',
      dataIndex: 'name',
      key: 'name',
      render: (name: string) => (
        <Space>
          <RobotOutlined />
          {name}
        </Space>
      ),
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <Tag color={getStatusColor(status)} icon={getStatusIcon(status)}>
          {status}
        </Tag>
      ),
    },
    {
      title: 'Active',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (isActive: boolean) => (
        <Tag color={isActive ? 'green' : 'red'}>
          {isActive ? 'Active' : 'Inactive'}
        </Tag>
      ),
    },
    {
      title: 'Last Run',
      dataIndex: 'lastRunAt',
      key: 'lastRunAt',
      render: (lastRunAt: string) => 
        lastRunAt ? new Date(lastRunAt).toLocaleString() : 'Never run',
    },
  ];

  // Chart data
  const chartData = [
    { name: 'January', users: 120, leads: 200, emails: 1500 },
    { name: 'February', users: 150, leads: 250, emails: 1800 },
    { name: 'March', users: 180, leads: 300, emails: 2200 },
    { name: 'April', users: 220, leads: 350, emails: 2500 },
    { name: 'May', users: 250, leads: 400, emails: 2800 },
  ];

  const pieData = [
    { name: 'Active Users', value: dashboardData?.totalUsers || 0, color: '#52c41a' },
    { name: 'Active Subscriptions', value: dashboardData?.activeSubscriptions || 0, color: '#1890ff' },
    { name: 'Total Leads', value: dashboardData?.totalLeads || 0, color: '#722ed1' },
  ];

  if (loading) {
    return (
      <div className="loading-spinner">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6">Admin Dashboard</h1>

      {/* Statistics Cards */}
      <Row gutter={[16, 16]} className="mb-6">
        <Col xs={24} sm={12} lg={6}>
          <Card className="dashboard-card">
            <Statistic
              title="Total Users"
              value={dashboardData?.totalUsers || 0}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="dashboard-card">
            <Statistic
              title="Active Subscriptions"
              value={dashboardData?.activeSubscriptions || 0}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="dashboard-card">
            <Statistic
              title="Total Leads"
              value={dashboardData?.totalLeads || 0}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="dashboard-card">
            <Statistic
              title="Emails Sent Today"
              value={dashboardData?.emailsSentToday || 0}
              prefix={<MailOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
          </Card>
        </Col>
      </Row>

      {/* Charts */}
      <Row gutter={[16, 16]} className="mb-6">
        <Col xs={24} lg={16}>
          <Card title="Monthly Statistics" className="dashboard-card">
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="users" stroke="#52c41a" name="Users" />
                <Line type="monotone" dataKey="leads" stroke="#1890ff" name="Leads" />
                <Line type="monotone" dataKey="emails" stroke="#fa8c16" name="Emails" />
              </LineChart>
            </ResponsiveContainer>
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card title="General Distribution" className="dashboard-card">
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </Col>
      </Row>

      {/* Warning Logs */}
      <Card title="Recent Warnings" className="dashboard-card mb-6">
        <Table
          columns={warningLogsColumns}
          dataSource={warningLogs}
          rowKey="id"
          pagination={false}
          size="small"
        />
      </Card>

      {/* Robots */}
      <Card title="Robot Status" className="dashboard-card">
        <Table
          columns={robotsColumns}
          dataSource={robots}
          rowKey="id"
          pagination={false}
        />
      </Card>
    </div>
  );
};

export default AdminDashboard; 
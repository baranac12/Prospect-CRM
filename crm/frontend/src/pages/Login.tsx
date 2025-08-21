import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Divider, Space } from 'antd';
import { UserOutlined, LockOutlined, GoogleOutlined, WindowsOutlined } from '@ant-design/icons';
import { useAuth } from '../context/AuthContext';
import { oauthAPI } from '../services/api';

const Login: React.FC = () => {
  const [form] = Form.useForm();
  const { login } = useAuth();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: { email: string; password: string }) => {
    try {
      setLoading(true);
      await login(values.email, values.password);
      message.success('Login successful!');
    } catch (error: any) {
      message.error(error.response?.data?.message || 'Login failed!');
    } finally {
      setLoading(false);
    }
  };

  const handleOAuthLogin = async (provider: 'GOOGLE' | 'MICROSOFT') => {
    try {
      setLoading(true);
      const redirectUri = `${window.location.origin}/oauth-callback`;
      const response = await oauthAPI.login(provider, redirectUri);
      const { authorizationUrl } = response.data.data;
      window.location.href = authorizationUrl;
    } catch (error: any) {
      message.error(`${provider} login failed!`);
      setLoading(false);
    }
  };

  return (
    <div className="login-container flex items-center justify-center min-h-screen p-4">
      <Card className="w-full max-w-md shadow-lg">
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-gray-800 mb-2">Prospect CRM</h1>
          <p className="text-gray-600">Sign in to your account</p>
        </div>

        <Form
          form={form}
          name="login"
          onFinish={onFinish}
          layout="vertical"
          size="large"
        >
          <Form.Item
            name="email"
            rules={[
              { required: true, message: 'Please enter your email!' },
            ]}
          >
            <Input
              prefix={<UserOutlined className="text-gray-400" />}
              placeholder="Email"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: 'Please enter your password!' },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined className="text-gray-400" />}
              placeholder="Password"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              className="w-full h-12 text-base"
            >
              Sign In
            </Button>
          </Form.Item>
        </Form>

        <Divider>or</Divider>

        <Space direction="vertical" className="w-full">
          <Button
            icon={<GoogleOutlined />}
            size="large"
            className="w-full h-12"
            onClick={() => handleOAuthLogin('GOOGLE')}
            loading={loading}
          >
            Sign in with Google
          </Button>

          <Button
            icon={<WindowsOutlined />}
            size="large"
            className="w-full h-12"
            onClick={() => handleOAuthLogin('MICROSOFT')}
            loading={loading}
          >
            Sign in with Microsoft
          </Button>
        </Space>

        <div className="text-center mt-6">
          <p className="text-gray-600">
            Don't have an account?{' '}
            <a href="/register" className="text-blue-600 hover:text-blue-800">
              Sign up
            </a>
          </p>
        </div>
      </Card>
    </div>
  );
};

export default Login; 
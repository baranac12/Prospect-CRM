import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Divider, Space } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined, GoogleOutlined, WindowsOutlined } from '@ant-design/icons';
import { useAuth } from '../context/AuthContext';
import { oauthAPI } from '../services/api';

const Register: React.FC = () => {
  const [form] = Form.useForm();
  const { register } = useAuth();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: any) => {
    try {
      setLoading(true);
      await register(values);
      message.success('Registration successful!');
    } catch (error: any) {
      message.error(error.response?.data?.message || 'Registration failed!');
    } finally {
      setLoading(false);
    }
  };

  const handleOAuthRegister = async (provider: 'GOOGLE' | 'MICROSOFT') => {
    try {
      setLoading(true);
      const redirectUri = `${window.location.origin}/oauth-callback`;
      const response = await oauthAPI.login(provider, redirectUri);
      const { authorizationUrl } = response.data.data;
      window.location.href = authorizationUrl;
    } catch (error: any) {
      message.error(`${provider} registration failed!`);
      setLoading(false);
    }
  };

  return (
    <div className="login-container flex items-center justify-center min-h-screen p-4">
      <Card className="w-full max-w-md shadow-lg">
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-gray-800 mb-2">Prospect CRM</h1>
          <p className="text-gray-600">Create a new account</p>
        </div>

        <Form
          form={form}
          name="register"
          onFinish={onFinish}
          layout="vertical"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: 'Please enter your username!' },
              { min: 3, message: 'Username must be at least 3 characters!' },
            ]}
          >
            <Input
              prefix={<UserOutlined className="text-gray-400" />}
              placeholder="Username"
            />
          </Form.Item>

          <Form.Item
            name="email"
            rules={[
              { required: true, message: 'Please enter your email!' },
              { type: 'email', message: 'Please enter a valid email!' },
            ]}
          >
            <Input
              prefix={<MailOutlined className="text-gray-400" />}
              placeholder="Email"
            />
          </Form.Item>

          <Form.Item
            name="name"
            rules={[
              { required: true, message: 'Please enter your name!' },
            ]}
          >
            <Input
              prefix={<UserOutlined className="text-gray-400" />}
              placeholder="Name"
            />
          </Form.Item>

          <Form.Item
            name="surname"
            rules={[
              { required: true, message: 'Please enter your surname!' },
            ]}
          >
            <Input
              prefix={<UserOutlined className="text-gray-400" />}
              placeholder="Surname"
            />
          </Form.Item>

          <Form.Item
            name="phone"
            rules={[
              { pattern: /^[0-9]{10,11}$/, message: 'Please enter a valid phone number!' },
            ]}
          >
            <Input
              prefix={<PhoneOutlined className="text-gray-400" />}
              placeholder="Phone number (optional)"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: 'Please enter your password!' },
              { min: 6, message: 'Password must be at least 6 characters!' },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined className="text-gray-400" />}
              placeholder="Password"
            />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: 'Please confirm your password!' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Passwords do not match!'));
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<LockOutlined className="text-gray-400" />}
              placeholder="Confirm password"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              className="w-full h-12 text-base"
            >
              Sign Up
            </Button>
          </Form.Item>
        </Form>

        <Divider>or</Divider>

        <Space direction="vertical" className="w-full">
          <Button
            icon={<GoogleOutlined />}
            size="large"
            className="w-full h-12"
            onClick={() => handleOAuthRegister('GOOGLE')}
            loading={loading}
          >
            Sign up with Google
          </Button>

          <Button
            icon={<WindowsOutlined />}
            size="large"
            className="w-full h-12"
            onClick={() => handleOAuthRegister('MICROSOFT')}
            loading={loading}
          >
            Sign up with Microsoft
          </Button>
        </Space>

        <div className="text-center mt-6">
          <p className="text-gray-600">
            Already have an account?{' '}
            <a href="/login" className="text-blue-600 hover:text-blue-800">
              Sign in
            </a>
          </p>
        </div>
      </Card>
    </div>
  );
};

export default Register; 
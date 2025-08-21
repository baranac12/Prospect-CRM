import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, message, Card, Typography } from 'antd';
import { EyeOutlined, EditOutlined, DeleteOutlined, SendOutlined } from '@ant-design/icons';
import { emailDraftAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

const { Title } = Typography;

interface EmailDraft {
  id: number;
  subject: string;
  body: string;
  cc: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

const Drafts: React.FC = () => {
  const [drafts, setDrafts] = useState<EmailDraft[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedDraft, setSelectedDraft] = useState<EmailDraft | null>(null);
  const [previewVisible, setPreviewVisible] = useState(false);
  const { user } = useAuth();

  useEffect(() => {
    fetchDrafts();
  }, []);

  const fetchDrafts = async () => {
    try {
      setLoading(true);
      const response = await emailDraftAPI.getAllDrafts(user?.id || 0);
      setDrafts(response.data.data || []);
    } catch (error) {
      message.error('Failed to fetch drafts');
    } finally {
      setLoading(false);
    }
  };

  const handlePreview = (draft: EmailDraft) => {
    setSelectedDraft(draft);
    setPreviewVisible(true);
  };

  const handleSend = async (draftId: number) => {
    try {
      await emailDraftAPI.send(draftId, user?.id || 0);
      message.success('Email sent successfully');
      fetchDrafts();
    } catch (error) {
      message.error('Failed to send email');
    }
  };

  const handleDelete = async (draftId: number) => {
    try {
      await emailDraftAPI.delete(draftId);
      message.success('Draft deleted successfully');
      fetchDrafts();
    } catch (error) {
      message.error('Failed to delete draft');
    }
  };

  const columns = [
    {
      title: 'Subject',
      dataIndex: 'subject',
      key: 'subject',
      ellipsis: true,
    },
    {
      title: 'CC',
      dataIndex: 'cc',
      key: 'cc',
      ellipsis: true,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <Tag color={status === 'APPROVED' ? 'green' : 'orange'}>
          {status}
        </Tag>
      ),
    },
    {
      title: 'Created',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record: EmailDraft) => (
        <Space>
          <Button
            type="text"
            icon={<EyeOutlined />}
            onClick={() => handlePreview(record)}
          />
          <Button
            type="text"
            icon={<SendOutlined />}
            onClick={() => handleSend(record.id)}
            disabled={record.status !== 'APPROVED'}
          />
          <Button
            type="text"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record.id)}
          />
        </Space>
      ),
    },
  ];

  return (
    <div className="p-6">
      <Card>
        <Title level={2}>Email Drafts</Title>
        
        <Table
          columns={columns}
          dataSource={drafts}
          loading={loading}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
          }}
        />
      </Card>

      <Modal
        title="Draft Preview"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={[
          <Button key="close" onClick={() => setPreviewVisible(false)}>
            Close
          </Button>,
          selectedDraft && (
            <Button
              key="send"
              type="primary"
              icon={<SendOutlined />}
              onClick={() => {
                handleSend(selectedDraft.id);
                setPreviewVisible(false);
              }}
              disabled={selectedDraft.status !== 'APPROVED'}
            >
              Send
            </Button>
          ),
        ]}
        width={800}
      >
        {selectedDraft && (
          <div>
            <p><strong>Subject:</strong> {selectedDraft.subject}</p>
            <p><strong>CC:</strong> {selectedDraft.cc}</p>
            <p><strong>Status:</strong> {selectedDraft.status}</p>
            <div style={{ marginTop: 16 }}>
              <strong>Body:</strong>
              <div
                style={{
                  border: '1px solid #d9d9d9',
                  padding: 16,
                  marginTop: 8,
                  borderRadius: 6,
                  backgroundColor: '#fafafa',
                }}
                dangerouslySetInnerHTML={{ __html: selectedDraft.body }}
              />
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default Drafts; 
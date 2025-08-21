import React, { useState, useEffect } from 'react';
import { Tabs, Card, Button, Input, Select, Space, Tag, Modal, message, Spin, Tooltip, List, Avatar } from 'antd';
import { 
  PlusOutlined, 
  SearchOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  SendOutlined,
  InboxOutlined,
  MailOutlined,
  FileTextOutlined,
  StarOutlined,
  StarFilled
} from '@ant-design/icons';
import { emailAPI, emailDraftAPI } from '../services/api';
import { Email, EmailDraft } from '../types';
import EmailDetail from '../components/EmailDetail';
import EmailForm from '../components/EmailForm';
import EmailDraftForm from '../components/EmailDraftForm';

const { Search } = Input;
const { Option } = Select;
const { TabPane } = Tabs;

const Emails: React.FC = () => {
  const [activeTab, setActiveTab] = useState('inbox');
  const [emails, setEmails] = useState<Email[]>([]);
  const [drafts, setDrafts] = useState<EmailDraft[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [selectedEmail, setSelectedEmail] = useState<Email | null>(null);
  const [selectedDraft, setSelectedDraft] = useState<EmailDraft | null>(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [draftFormModalVisible, setDraftFormModalVisible] = useState(false);

  useEffect(() => {
    if (activeTab === 'inbox' || activeTab === 'sent') {
      fetchEmails();
    } else if (activeTab === 'drafts') {
      fetchDrafts();
    }
  }, [activeTab, currentPage, pageSize]);

  const fetchEmails = async () => {
    try {
      setLoading(true);
      const label = activeTab === 'inbox' ? 'INBOX' : 'SENT';
      const response = await emailAPI.getAll(currentPage - 1, pageSize, label);
      setEmails(response.data.data.content);
      setTotal(response.data.data.totalElements);
    } catch (error) {
      message.error('Emailler yüklenemedi!');
    } finally {
      setLoading(false);
    }
  };

  const fetchDrafts = async () => {
    try {
      setLoading(true);
      const response = await emailDraftAPI.getAll(currentPage - 1, pageSize, 'DRAFT');
      setDrafts(response.data.data.content);
      setTotal(response.data.data.totalElements);
    } catch (error) {
      message.error('Taslaklar yüklenemedi!');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (value: string) => {
    try {
      setLoading(true);
      if (activeTab === 'drafts') {
        // Draft search logic
        const filteredDrafts = drafts.filter(draft => 
          draft.subject.toLowerCase().includes(value.toLowerCase()) ||
          draft.toEmails.some(email => email.toLowerCase().includes(value.toLowerCase()))
        );
        setDrafts(filteredDrafts);
      } else {
        // Email search logic
        const filteredEmails = emails.filter(email => 
          email.subject.toLowerCase().includes(value.toLowerCase()) ||
          email.from.toLowerCase().includes(value.toLowerCase())
        );
        setEmails(filteredEmails);
      }
      setSearchQuery(value);
    } catch (error) {
      message.error('Arama yapılamadı!');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteEmail = async (id: string) => {
    Modal.confirm({
      title: 'Email Sil',
      content: 'Bu email\'i silmek istediğinizden emin misiniz?',
      okText: 'Evet',
      cancelText: 'Hayır',
      onOk: async () => {
        try {
          await emailAPI.delete(id);
          message.success('Email başarıyla silindi!');
          fetchEmails();
        } catch (error) {
          message.error('Email silinemedi!');
        }
      },
    });
  };

  const handleDeleteDraft = async (id: number) => {
    Modal.confirm({
      title: 'Taslak Sil',
      content: 'Bu taslağı silmek istediğinizden emin misiniz?',
      okText: 'Evet',
      cancelText: 'Hayır',
      onOk: async () => {
        try {
          await emailDraftAPI.delete(id);
          message.success('Taslak başarıyla silindi!');
          fetchDrafts();
        } catch (error) {
          message.error('Taslak silinemedi!');
        }
      },
    });
  };

  const handleViewEmail = (email: Email) => {
    setSelectedEmail(email);
    setDetailModalVisible(true);
  };

  const handleViewDraft = (draft: EmailDraft) => {
    setSelectedDraft(draft);
    setDetailModalVisible(true);
  };

  const handleSendDraft = async (id: number) => {
    try {
      // Mock user ID - gerçek uygulamada context'ten alınacak
      const userId = 1;
      await emailDraftAPI.send(id, userId);
      message.success('Taslak başarıyla gönderildi!');
      fetchDrafts();
    } catch (error) {
      message.error('Taslak gönderilemedi!');
    }
  };

  const handleFormSubmit = async (values: any) => {
    try {
      await emailAPI.send(values);
      message.success('Email başarıyla gönderildi!');
      setFormModalVisible(false);
      fetchEmails();
    } catch (error) {
      message.error('Email gönderilemedi!');
    }
  };

  const handleDraftSubmit = async (values: any) => {
    try {
      await emailDraftAPI.create(values);
      message.success('Taslak başarıyla oluşturuldu!');
      setDraftFormModalVisible(false);
      fetchDrafts();
    } catch (error) {
      message.error('Taslak oluşturulamadı!');
    }
  };

  const renderEmailItem = (email: Email) => (
    <List.Item
      className="email-item p-4 border-b border-gray-100 cursor-pointer"
      onClick={() => handleViewEmail(email)}
    >
      <List.Item.Meta
        avatar={
          <Avatar icon={<MailOutlined />} className="bg-blue-500" />
        }
        title={
          <div className="flex items-center justify-between">
            <span className="font-medium">{email.subject}</span>
            <Space>
              {!email.isRead && <div className="w-2 h-2 bg-blue-500 rounded-full"></div>}
              <span className="text-sm text-gray-500">
                {new Date(email.date).toLocaleDateString('tr-TR')}
              </span>
            </Space>
          </div>
        }
        description={
          <div>
            <div className="text-sm text-gray-600 mb-1">
              <strong>Kimden:</strong> {email.from}
            </div>
            <div className="text-sm text-gray-500">
              {email.snippet}
            </div>
          </div>
        }
      />
      <Space>
        <Tooltip title="Sil">
          <Button
            type="text"
            icon={<DeleteOutlined />}
            onClick={(e) => {
              e.stopPropagation();
              handleDeleteEmail(email.id);
            }}
            className="text-red-600 hover:text-red-800"
          />
        </Tooltip>
      </Space>
    </List.Item>
  );

  const renderDraftItem = (draft: EmailDraft) => (
    <List.Item
      className="email-item p-4 border-b border-gray-100 cursor-pointer"
      onClick={() => handleViewDraft(draft)}
    >
      <List.Item.Meta
        avatar={
          <Avatar icon={<FileTextOutlined />} className="bg-orange-500" />
        }
        title={
          <div className="flex items-center justify-between">
            <span className="font-medium">{draft.subject}</span>
            <Space>
              {draft.createdByRobot && (
                <Tag color="purple" icon={<FileTextOutlined />}>
                  Robot
                </Tag>
              )}
              <span className="text-sm text-gray-500">
                {new Date(draft.createdAt).toLocaleDateString('tr-TR')}
              </span>
            </Space>
          </div>
        }
        description={
          <div>
            <div className="text-sm text-gray-600 mb-1">
              <strong>Kime:</strong> {draft.toEmails.join(', ')}
            </div>
            <div className="text-sm text-gray-500">
              {draft.body.substring(0, 100)}...
            </div>
          </div>
        }
      />
      <Space>
        <Tooltip title="Gönder">
          <Button
            type="text"
            icon={<SendOutlined />}
            onClick={(e) => {
              e.stopPropagation();
              handleSendDraft(draft.id);
            }}
            className="text-green-600 hover:text-green-800"
          />
        </Tooltip>
        <Tooltip title="Sil">
          <Button
            type="text"
            icon={<DeleteOutlined />}
            onClick={(e) => {
              e.stopPropagation();
              handleDeleteDraft(draft.id);
            }}
            className="text-red-600 hover:text-red-800"
          />
        </Tooltip>
      </Space>
    </List.Item>
  );

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Email Yönetimi</h1>
        <Space>
          <Button
            icon={<PlusOutlined />}
            onClick={() => setDraftFormModalVisible(true)}
            size="large"
          >
            Yeni Taslak
          </Button>
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={() => setFormModalVisible(true)}
            size="large"
          >
            Email Gönder
          </Button>
        </Space>
      </div>

      <Card>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane
            tab={
              <span>
                <InboxOutlined />
                Gelen Kutusu
              </span>
            }
            key="inbox"
          >
            <div className="mb-4">
              <Search
                placeholder="Email ara..."
                allowClear
                enterButton={<SearchOutlined />}
                size="large"
                onSearch={handleSearch}
                className="max-w-md"
              />
            </div>
            <List
              loading={loading}
              dataSource={emails}
              renderItem={renderEmailItem}
              pagination={{
                current: currentPage,
                pageSize: pageSize,
                total: total,
                onChange: (page, size) => {
                  setCurrentPage(page);
                  setPageSize(size || 10);
                },
              }}
            />
          </TabPane>

          <TabPane
            tab={
              <span>
                <MailOutlined />
                Giden Kutusu
              </span>
            }
            key="sent"
          >
            <div className="mb-4">
              <Search
                placeholder="Email ara..."
                allowClear
                enterButton={<SearchOutlined />}
                size="large"
                onSearch={handleSearch}
                className="max-w-md"
              />
            </div>
            <List
              loading={loading}
              dataSource={emails}
              renderItem={renderEmailItem}
              pagination={{
                current: currentPage,
                pageSize: pageSize,
                total: total,
                onChange: (page, size) => {
                  setCurrentPage(page);
                  setPageSize(size || 10);
                },
              }}
            />
          </TabPane>

          <TabPane
            tab={
              <span>
                <FileTextOutlined />
                Taslaklar
              </span>
            }
            key="drafts"
          >
            <div className="mb-4">
              <Search
                placeholder="Taslak ara..."
                allowClear
                enterButton={<SearchOutlined />}
                size="large"
                onSearch={handleSearch}
                className="max-w-md"
              />
            </div>
            <List
              loading={loading}
              dataSource={drafts}
              renderItem={renderDraftItem}
              pagination={{
                current: currentPage,
                pageSize: pageSize,
                total: total,
                onChange: (page, size) => {
                  setCurrentPage(page);
                  setPageSize(size || 10);
                },
              }}
            />
          </TabPane>
        </Tabs>
      </Card>

      {/* Email Detail Modal */}
      <Modal
        title="Email Detayları"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={800}
      >
        {selectedEmail && <EmailDetail email={selectedEmail} />}
        {selectedDraft && <EmailDetail draft={selectedDraft} />}
      </Modal>

      {/* Email Form Modal */}
      <Modal
        title="Email Gönder"
        open={formModalVisible}
        onCancel={() => setFormModalVisible(false)}
        footer={null}
        width={800}
      >
        <EmailForm
          onSubmit={handleFormSubmit}
          onCancel={() => setFormModalVisible(false)}
        />
      </Modal>

      {/* Draft Form Modal */}
      <Modal
        title="Yeni Taslak Oluştur"
        open={draftFormModalVisible}
        onCancel={() => setDraftFormModalVisible(false)}
        footer={null}
        width={800}
      >
        <EmailDraftForm
          onSubmit={handleDraftSubmit}
          onCancel={() => setDraftFormModalVisible(false)}
        />
      </Modal>
    </div>
  );
};

export default Emails; 
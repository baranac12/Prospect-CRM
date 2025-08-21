import React from 'react';
import { Descriptions, Tag, Card, Space, Divider, Typography } from 'antd';
import { MailOutlined, UserOutlined, CalendarOutlined, PaperClipOutlined } from '@ant-design/icons';
import { Email, EmailDraft } from '../types';

const { Text, Paragraph } = Typography;

interface EmailDetailProps {
  email?: Email;
  draft?: EmailDraft;
}

const EmailDetail: React.FC<EmailDetailProps> = ({ email, draft }) => {
  const isDraft = !!draft;
  const data = isDraft ? draft : email;

  if (!data) {
    return <div>Veri bulunamadı</div>;
  }

  const renderEmailContent = () => {
    if (isDraft) {
      return (
        <div>
          <div className="mb-4">
            <Text strong>Konu:</Text>
            <div className="text-lg font-medium mt-1">{draft.subject}</div>
          </div>
          
          <div className="mb-4">
            <Text strong>Kime:</Text>
            <div className="mt-1">
              {draft.toEmails.map((email, index) => (
                <Tag key={index} color="blue" className="mb-1">
                  {email}
                </Tag>
              ))}
            </div>
          </div>

          {draft.ccEmails && draft.ccEmails.length > 0 && (
            <div className="mb-4">
              <Text strong>CC:</Text>
              <div className="mt-1">
                {draft.ccEmails.map((email, index) => (
                  <Tag key={index} color="green" className="mb-1">
                    {email}
                  </Tag>
                ))}
              </div>
            </div>
          )}

          {draft.bccEmails && draft.bccEmails.length > 0 && (
            <div className="mb-4">
              <Text strong>BCC:</Text>
              <div className="mt-1">
                {draft.bccEmails.map((email, index) => (
                  <Tag key={index} color="orange" className="mb-1">
                    {email}
                  </Tag>
                ))}
              </div>
            </div>
          )}

          <Divider />

          <div className="mb-4">
            <Text strong>İçerik:</Text>
            <div className="mt-2 p-4 bg-gray-50 rounded-lg">
              <Paragraph className="whitespace-pre-wrap">
                {draft.body}
              </Paragraph>
            </div>
          </div>

          {draft.attachments && draft.attachments.length > 0 && (
            <div className="mb-4">
              <Text strong>Ekler:</Text>
              <div className="mt-2">
                {draft.attachments.map((attachment, index) => (
                  <div key={index} className="flex items-center space-x-2 p-2 bg-gray-50 rounded mb-2">
                    <PaperClipOutlined />
                    <Text>{attachment.filename}</Text>
                    <Text type="secondary">({attachment.size} bytes)</Text>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      );
    }

    return (
      <div>
        <div className="mb-4">
          <Text strong>Konu:</Text>
          <div className="text-lg font-medium mt-1">{email.subject}</div>
        </div>

        <div className="mb-4">
          <Text strong>Kimden:</Text>
          <div className="mt-1">
            <Tag color="blue">{email.from}</Tag>
          </div>
        </div>

        <div className="mb-4">
          <Text strong>Kime:</Text>
          <div className="mt-1">
            {email.to.map((recipient, index) => (
              <Tag key={index} color="blue" className="mb-1">
                {recipient}
              </Tag>
            ))}
          </div>
        </div>

        {email.cc && email.cc.length > 0 && (
          <div className="mb-4">
            <Text strong>CC:</Text>
            <div className="mt-1">
              {email.cc.map((recipient, index) => (
                <Tag key={index} color="green" className="mb-1">
                  {recipient}
                </Tag>
              ))}
            </div>
          </div>
        )}

        {email.bcc && email.bcc.length > 0 && (
          <div className="mb-4">
            <Text strong>BCC:</Text>
            <div className="mt-1">
              {email.bcc.map((recipient, index) => (
                <Tag key={index} color="orange" className="mb-1">
                  {recipient}
                </Tag>
              ))}
            </div>
          </div>
        )}

        <Divider />

        <div className="mb-4">
          <Text strong>İçerik:</Text>
          <div className="mt-2 p-4 bg-gray-50 rounded-lg">
            <Paragraph className="whitespace-pre-wrap">
              {email.body}
            </Paragraph>
          </div>
        </div>

        {email.attachments && email.attachments.length > 0 && (
          <div className="mb-4">
            <Text strong>Ekler:</Text>
            <div className="mt-2">
              {email.attachments.map((attachment, index) => (
                <div key={index} className="flex items-center space-x-2 p-2 bg-gray-50 rounded mb-2">
                  <PaperClipOutlined />
                  <Text>{attachment.filename}</Text>
                  <Text type="secondary">({attachment.size} bytes)</Text>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-bold">
          {isDraft ? 'Email Taslağı' : 'Email Detayları'}
        </h2>
        <Space>
          {isDraft && draft.createdByRobot && (
            <Tag color="purple" icon={<MailOutlined />}>
              Robot Oluşturdu
            </Tag>
          )}
          <Tag color={isDraft ? 'orange' : 'blue'}>
            {isDraft ? 'Taslak' : 'Email'}
          </Tag>
        </Space>
      </div>

      <Card title="Email Bilgileri" className="mb-4">
        <Descriptions column={1} size="small">
          <Descriptions.Item label="Tarih">
            <Space>
              <CalendarOutlined />
              {new Date(isDraft ? draft.createdAt : email.date).toLocaleString('tr-TR')}
            </Space>
          </Descriptions.Item>
          {!isDraft && (
            <Descriptions.Item label="Okundu">
              <Tag color={email.isRead ? 'green' : 'red'}>
                {email.isRead ? 'Okundu' : 'Okunmadı'}
              </Tag>
            </Descriptions.Item>
          )}
          {isDraft && (
            <Descriptions.Item label="Durum">
              <Tag color={draft.status === 'DRAFT' ? 'orange' : 'green'}>
                {draft.status === 'DRAFT' ? 'Taslak' : 'Gönderildi'}
              </Tag>
            </Descriptions.Item>
          )}
        </Descriptions>
      </Card>

      <Card title="İçerik">
        {renderEmailContent()}
      </Card>

      {isDraft && draft.templateName && (
        <Card title="Şablon Bilgileri" className="mb-4">
          <Descriptions column={1} size="small">
            <Descriptions.Item label="Şablon Adı">
              {draft.templateName}
            </Descriptions.Item>
            {draft.templateData && (
              <Descriptions.Item label="Şablon Verileri">
                <pre className="text-xs bg-gray-100 p-2 rounded">
                  {JSON.stringify(draft.templateData, null, 2)}
                </pre>
              </Descriptions.Item>
            )}
          </Descriptions>
        </Card>
      )}
    </div>
  );
};

export default EmailDetail; 
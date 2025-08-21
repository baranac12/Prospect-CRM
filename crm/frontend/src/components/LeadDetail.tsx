import React from 'react';
import { Descriptions, Tag, Card, Space, Button } from 'antd';
import { EditOutlined, MailOutlined, PhoneOutlined, GlobalOutlined } from '@ant-design/icons';
import { Lead } from '../types';

interface LeadDetailProps {
  lead: Lead;
  onEdit?: () => void;
}

const LeadDetail: React.FC<LeadDetailProps> = ({ lead, onEdit }) => {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'green';
      case 'CONTACTED':
        return 'blue';
      case 'CONVERTED':
        return 'purple';
      case 'LOST':
        return 'red';
      default:
        return 'default';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'Aktif';
      case 'CONTACTED':
        return 'İletişim Kuruldu';
      case 'CONVERTED':
        return 'Dönüştürüldü';
      case 'LOST':
        return 'Kaybedildi';
      default:
        return status;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-bold">{lead.companyName}</h2>
        <Space>
          <Tag color={getStatusColor(lead.status)} size="large">
            {getStatusText(lead.status)}
          </Tag>
          {onEdit && (
            <Button
              type="primary"
              icon={<EditOutlined />}
              onClick={onEdit}
            >
              Düzenle
            </Button>
          )}
        </Space>
      </div>

      <Card title="İletişim Bilgileri" className="mb-4">
        <Descriptions column={1} size="small">
          <Descriptions.Item label="İletişim Kişisi">
            <Space>
              <UserOutlined />
              {lead.contactName}
            </Space>
          </Descriptions.Item>
          <Descriptions.Item label="Email">
            <Space>
              <MailOutlined />
              <a href={`mailto:${lead.email}`} className="text-blue-600 hover:text-blue-800">
                {lead.email}
              </a>
            </Space>
          </Descriptions.Item>
          <Descriptions.Item label="Telefon">
            <Space>
              <PhoneOutlined />
              <a href={`tel:${lead.phone}`} className="text-blue-600 hover:text-blue-800">
                {lead.phone}
              </a>
            </Space>
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="Şirket Bilgileri" className="mb-4">
        <Descriptions column={1} size="small">
          <Descriptions.Item label="Şirket Adı">
            <Space>
              <GlobalOutlined />
              {lead.companyName}
            </Space>
          </Descriptions.Item>
          <Descriptions.Item label="Durum">
            <Tag color={getStatusColor(lead.status)}>
              {getStatusText(lead.status)}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Oluşturulma Tarihi">
            {new Date(lead.createdAt).toLocaleDateString('tr-TR')}
          </Descriptions.Item>
          <Descriptions.Item label="Güncellenme Tarihi">
            {new Date(lead.updatedAt).toLocaleDateString('tr-TR')}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {lead.notes && (
        <Card title="Notlar" className="mb-4">
          <div className="whitespace-pre-wrap text-gray-700">
            {lead.notes}
          </div>
        </Card>
      )}

      <Card title="İstatistikler" className="mb-4">
        <div className="grid grid-cols-2 gap-4">
          <div className="text-center">
            <div className="text-2xl font-bold text-blue-600">0</div>
            <div className="text-sm text-gray-600">Gönderilen Email</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-green-600">0</div>
            <div className="text-sm text-gray-600">Açılan Email</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-purple-600">0</div>
            <div className="text-sm text-gray-600">Tıklanan Link</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-orange-600">0</div>
            <div className="text-sm text-gray-600">Toplam Görüntüleme</div>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default LeadDetail; 
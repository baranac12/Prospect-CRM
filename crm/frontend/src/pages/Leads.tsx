import React, { useState, useEffect } from 'react';
import { Table, Card, Button, Input, Select, Space, Tag, Modal, message, Spin, Tooltip } from 'antd';
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import { leadAPI } from '../services/api';
import { Lead } from '../types';
import LeadDetail from '../components/LeadDetail';
import LeadForm from '../components/LeadForm';

const { Search } = Input;
const { Option } = Select;

const Leads: React.FC = () => {
  const [leads, setLeads] = useState<Lead[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [selectedLead, setSelectedLead] = useState<Lead | null>(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingLead, setEditingLead] = useState<Lead | null>(null);

  useEffect(() => {
    fetchLeads();
  }, [currentPage, pageSize, statusFilter]);

  const fetchLeads = async () => {
    try {
      setLoading(true);
      const response = await leadAPI.getAll(currentPage - 1, pageSize, statusFilter);
      setLeads(response.data.data.content);
      setTotal(response.data.data.totalElements);
    } catch (error) {
      message.error('Leadler yüklenemedi!');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (value: string) => {
    try {
      setLoading(true);
      const response = await leadAPI.search(value, statusFilter);
      setLeads(response.data.data.content);
      setTotal(response.data.data.totalElements);
      setSearchQuery(value);
    } catch (error) {
      message.error('Arama yapılamadı!');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusFilter = (value: string) => {
    setStatusFilter(value);
    setCurrentPage(1);
  };

  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: 'Lead Sil',
      content: 'Bu lead\'i silmek istediğinizden emin misiniz?',
      okText: 'Evet',
      cancelText: 'Hayır',
      onOk: async () => {
        try {
          await leadAPI.delete(id);
          message.success('Lead başarıyla silindi!');
          fetchLeads();
        } catch (error) {
          message.error('Lead silinemedi!');
        }
      },
    });
  };

  const handleEdit = (lead: Lead) => {
    setEditingLead(lead);
    setFormModalVisible(true);
  };

  const handleView = (lead: Lead) => {
    setSelectedLead(lead);
    setDetailModalVisible(true);
  };

  const handleFormSubmit = async (values: any) => {
    try {
      if (editingLead) {
        await leadAPI.update(editingLead.id, values);
        message.success('Lead başarıyla güncellendi!');
      } else {
        await leadAPI.create(values);
        message.success('Lead başarıyla oluşturuldu!');
      }
      setFormModalVisible(false);
      setEditingLead(null);
      fetchLeads();
    } catch (error) {
      message.error('İşlem başarısız!');
    }
  };

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

  const columns = [
    {
      title: 'Şirket Adı',
      dataIndex: 'companyName',
      key: 'companyName',
      render: (text: string) => <span className="font-medium">{text}</span>,
    },
    {
      title: 'İletişim Kişisi',
      dataIndex: 'contactName',
      key: 'contactName',
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      render: (email: string) => (
        <a href={`mailto:${email}`} className="text-blue-600 hover:text-blue-800">
          {email}
        </a>
      ),
    },
    {
      title: 'Telefon',
      dataIndex: 'phone',
      key: 'phone',
      render: (phone: string) => (
        <a href={`tel:${phone}`} className="text-blue-600 hover:text-blue-800">
          {phone}
        </a>
      ),
    },
    {
      title: 'Durum',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <Tag color={getStatusColor(status)}>
          {getStatusText(status)}
        </Tag>
      ),
    },
    {
      title: 'Oluşturulma Tarihi',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => new Date(date).toLocaleDateString('tr-TR'),
    },
    {
      title: 'İşlemler',
      key: 'actions',
      render: (_: any, record: Lead) => (
        <Space>
          <Tooltip title="Detayları Görüntüle">
            <Button
              type="text"
              icon={<EyeOutlined />}
              onClick={() => handleView(record)}
              className="text-blue-600 hover:text-blue-800"
            />
          </Tooltip>
          <Tooltip title="Düzenle">
            <Button
              type="text"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
              className="text-green-600 hover:text-green-800"
            />
          </Tooltip>
          <Tooltip title="Sil">
            <Button
              type="text"
              icon={<DeleteOutlined />}
              onClick={() => handleDelete(record.id)}
              className="text-red-600 hover:text-red-800"
            />
          </Tooltip>
        </Space>
      ),
    },
  ];

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Leadler</h1>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setFormModalVisible(true)}
          size="large"
        >
          Yeni Lead Ekle
        </Button>
      </div>

      <Card className="mb-6">
        <div className="flex flex-wrap gap-4 items-center">
          <Search
            placeholder="Lead ara..."
            allowClear
            enterButton={<SearchOutlined />}
            size="large"
            onSearch={handleSearch}
            className="flex-1 min-w-64"
          />
          <Select
            placeholder="Durum Filtrele"
            allowClear
            size="large"
            onChange={handleStatusFilter}
            className="min-w-48"
          >
            <Option value="ACTIVE">Aktif</Option>
            <Option value="CONTACTED">İletişim Kuruldu</Option>
            <Option value="CONVERTED">Dönüştürüldü</Option>
            <Option value="LOST">Kaybedildi</Option>
          </Select>
        </div>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={leads}
          rowKey="id"
          loading={loading}
          pagination={{
            current: currentPage,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              `${range[0]}-${range[1]} / ${total} kayıt`,
            onChange: (page, size) => {
              setCurrentPage(page);
              setPageSize(size || 10);
            },
          }}
          scroll={{ x: 1200 }}
        />
      </Card>

      {/* Lead Detail Modal */}
      <Modal
        title="Lead Detayları"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={800}
      >
        {selectedLead && <LeadDetail lead={selectedLead} />}
      </Modal>

      {/* Lead Form Modal */}
      <Modal
        title={editingLead ? 'Lead Düzenle' : 'Yeni Lead Ekle'}
        open={formModalVisible}
        onCancel={() => {
          setFormModalVisible(false);
          setEditingLead(null);
        }}
        footer={null}
        width={600}
      >
        <LeadForm
          initialValues={editingLead}
          onSubmit={handleFormSubmit}
          onCancel={() => {
            setFormModalVisible(false);
            setEditingLead(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default Leads; 
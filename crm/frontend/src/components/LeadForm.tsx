import React from 'react';
import { Form, Input, Select, Button, Space } from 'antd';
import { Lead } from '../types';

const { Option } = Select;
const { TextArea } = Input;

interface LeadFormProps {
  initialValues?: Lead | null;
  onSubmit: (values: any) => void;
  onCancel: () => void;
  loading?: boolean;
}

const LeadForm: React.FC<LeadFormProps> = ({
  initialValues,
  onSubmit,
  onCancel,
  loading = false,
}) => {
  const [form] = Form.useForm();

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onSubmit(values);
    } catch (error) {
      console.error('Form validation failed:', error);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={initialValues || {}}
      onFinish={handleSubmit}
    >
      <Form.Item
        name="companyName"
        label="Şirket Adı"
        rules={[
          { required: true, message: 'Lütfen şirket adını girin!' },
          { min: 2, message: 'Şirket adı en az 2 karakter olmalıdır!' },
        ]}
      >
        <Input placeholder="Şirket adını girin" />
      </Form.Item>

      <Form.Item
        name="contactName"
        label="İletişim Kişisi"
        rules={[
          { required: true, message: 'Lütfen iletişim kişisini girin!' },
        ]}
      >
        <Input placeholder="İletişim kişisini girin" />
      </Form.Item>

      <Form.Item
        name="email"
        label="Email"
        rules={[
          { required: true, message: 'Lütfen email adresini girin!' },
          { type: 'email', message: 'Geçerli bir email adresi girin!' },
        ]}
      >
        <Input placeholder="Email adresini girin" />
      </Form.Item>

      <Form.Item
        name="phone"
        label="Telefon"
        rules={[
          { required: true, message: 'Lütfen telefon numarasını girin!' },
          { pattern: /^[0-9]{10,11}$/, message: 'Geçerli bir telefon numarası girin!' },
        ]}
      >
        <Input placeholder="Telefon numarasını girin" />
      </Form.Item>

      <Form.Item
        name="status"
        label="Durum"
        rules={[
          { required: true, message: 'Lütfen durum seçin!' },
        ]}
      >
        <Select placeholder="Durum seçin">
          <Option value="ACTIVE">Aktif</Option>
          <Option value="CONTACTED">İletişim Kuruldu</Option>
          <Option value="CONVERTED">Dönüştürüldü</Option>
          <Option value="LOST">Kaybedildi</Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="notes"
        label="Notlar"
      >
        <TextArea
          rows={4}
          placeholder="Lead hakkında notlar ekleyin..."
        />
      </Form.Item>

      <Form.Item className="mb-0">
        <Space className="w-full justify-end">
          <Button onClick={onCancel}>
            İptal
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            {initialValues ? 'Güncelle' : 'Oluştur'}
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default LeadForm; 
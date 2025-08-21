import React from 'react';
import { Form, Input, Select, Button, Space, Upload } from 'antd';
import { SaveOutlined, PaperClipOutlined } from '@ant-design/icons';

const { Option } = Select;
const { TextArea } = Input;

interface EmailDraftFormProps {
  onSubmit: (values: any) => void;
  onCancel: () => void;
  loading?: boolean;
}

const EmailDraftForm: React.FC<EmailDraftFormProps> = ({
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
      onFinish={handleSubmit}
    >
      <Form.Item
        name="leadId"
        label="Lead"
      >
        <Select placeholder="Lead seçin (opsiyonel)">
          <Option value={1}>Tech Corp - John Doe</Option>
          <Option value={2}>ABC Company - Jane Smith</Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="toEmails"
        label="Alıcılar"
        rules={[
          { required: true, message: 'Lütfen en az bir alıcı girin!' },
        ]}
      >
        <Select
          mode="tags"
          placeholder="Alıcı email adreslerini girin"
          style={{ width: '100%' }}
        />
      </Form.Item>

      <Form.Item
        name="ccEmails"
        label="CC"
      >
        <Select
          mode="tags"
          placeholder="CC email adreslerini girin"
          style={{ width: '100%' }}
        />
      </Form.Item>

      <Form.Item
        name="bccEmails"
        label="BCC"
      >
        <Select
          mode="tags"
          placeholder="BCC email adreslerini girin"
          style={{ width: '100%' }}
        />
      </Form.Item>

      <Form.Item
        name="subject"
        label="Konu"
        rules={[
          { required: true, message: 'Lütfen email konusunu girin!' },
        ]}
      >
        <Input placeholder="Email konusunu girin" />
      </Form.Item>

      <Form.Item
        name="contentType"
        label="İçerik Tipi"
        initialValue="text/plain"
      >
        <Select>
          <Option value="text/plain">Düz Metin</Option>
          <Option value="text/html">HTML</Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="body"
        label="İçerik"
        rules={[
          { required: true, message: 'Lütfen email içeriğini girin!' },
        ]}
      >
        <TextArea
          rows={8}
          placeholder="Email içeriğini girin..."
        />
      </Form.Item>

      <Form.Item
        name="templateName"
        label="Şablon Adı"
      >
        <Input placeholder="Şablon adı (opsiyonel)" />
      </Form.Item>

      <Form.Item
        name="attachments"
        label="Ekler"
      >
        <Upload
          multiple
          beforeUpload={() => false}
          fileList={[]}
        >
          <Button icon={<PaperClipOutlined />}>
            Dosya Ekle
          </Button>
        </Upload>
      </Form.Item>

      <Form.Item className="mb-0">
        <Space className="w-full justify-end">
          <Button onClick={onCancel}>
            İptal
          </Button>
          <Button 
            type="primary" 
            htmlType="submit" 
            loading={loading}
            icon={<SaveOutlined />}
          >
            Taslak Oluştur
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default EmailDraftForm; 
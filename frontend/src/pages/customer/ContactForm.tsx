import { useEffect } from 'react'
import { Modal, Form, Input, InputNumber, Switch, message } from 'antd'
import { customerContactApi, type CustomerContactVO, type CustomerContactDTO } from '../../api/customer'

interface ContactFormProps {
  open: boolean
  data?: CustomerContactVO
  customerId: number
  onSuccess: () => void
  onCancel: () => void
}

/**
 * 联系人新增/编辑表单弹窗
 */
export default function ContactForm({ open, data, customerId, onSuccess, onCancel }: ContactFormProps) {
  const [form] = Form.useForm<CustomerContactDTO>()
  const isEdit = !!data

  useEffect(() => {
    if (open) {
      if (data) {
        form.setFieldsValue({
          id: data.id,
          customerId: data.customerId,
          name: data.name,
          phone: data.phone,
          email: data.email,
          position: data.position,
          isPrimary: data.isPrimary || 0,
        })
      } else {
        form.resetFields()
        form.setFieldValue('customerId', customerId)
        form.setFieldValue('isPrimary', 0)
      }
    }
  }, [open, data, customerId, form])

  const handleOk = async () => {
    try {
      const values = await form.validateFields()
      if (isEdit) {
        await customerContactApi.update(values)
        message.success('更新成功')
      } else {
        await customerContactApi.create(values)
        message.success('创建成功')
      }
      onSuccess()
    } catch (err) {
      if (err && typeof err === 'object' && 'errorFields' in err) {
        return
      }
    }
  }

  return (
    <Modal
      title={isEdit ? '编辑联系人' : '新增联系人'}
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      width={480}
      destroyOnClose
    >
      <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
        <Form.Item name="id" hidden>
          <Input />
        </Form.Item>
        <Form.Item name="customerId" hidden>
          <InputNumber />
        </Form.Item>

        <Form.Item
          label="联系人姓名"
          name="name"
          rules={[{ required: true, message: '请输入联系人姓名' }]}
        >
          <Input placeholder="请输入姓名" maxLength={100} />
        </Form.Item>

        <Form.Item label="联系电话" name="phone">
          <Input placeholder="请输入联系电话" maxLength={20} />
        </Form.Item>

        <Form.Item label="邮箱" name="email">
          <Input placeholder="请输入邮箱地址" maxLength={100} type="email" />
        </Form.Item>

        <Form.Item label="职位" name="position">
          <Input placeholder="请输入职位" maxLength={100} />
        </Form.Item>

        <Form.Item label="是否主要联系人" name="isPrimary" valuePropName="checked" getValueFromEvent={(checked) => (checked ? 1 : 0)}>
          <Switch />
        </Form.Item>
      </Form>
    </Modal>
  )
}

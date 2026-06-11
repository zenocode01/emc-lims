import { useEffect } from 'react'
import { Modal, Form, Input, Select, message } from 'antd'
import { customerApi, type CustomerVO, type CustomerDTO } from '../../api/customer'

interface CustomerFormProps {
  open: boolean
  data?: CustomerVO
  onSuccess: () => void
  onCancel: () => void
}

/**
 * 客户新增/编辑表单弹窗
 */
export default function CustomerForm({ open, data, onSuccess, onCancel }: CustomerFormProps) {
  const [form] = Form.useForm<CustomerDTO>()
  const isEdit = !!data

  useEffect(() => {
    if (open) {
      if (data) {
        form.setFieldsValue({
          id: data.id,
          name: data.name,
          type: data.type || 1,
          industry: data.industry,
          address: data.address,
          phone: data.phone,
          email: data.email,
          contact: data.contact,
          status: data.status ?? 1,
          remark: data.remark,
        })
      } else {
        form.resetFields()
      }
    }
  }, [open, data, form])

  const handleOk = async () => {
    try {
      const values = await form.validateFields()
      if (isEdit) {
        await customerApi.update(values)
        message.success('更新成功')
      } else {
        await customerApi.create(values)
        message.success('创建成功')
      }
      onSuccess()
    } catch (err) {
      // validation or API error
      if (err && typeof err === 'object' && 'errorFields' in err) {
        return // validation error
      }
    }
  }

  return (
    <Modal
      title={isEdit ? '编辑客户' : '新增客户'}
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      width={640}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        style={{ marginTop: 16 }}
        initialValues={{ type: 1, status: 1 }}
      >
        <Form.Item name="id" hidden>
          <Input />
        </Form.Item>

        <Form.Item
          label="客户名称"
          name="name"
          rules={[{ required: true, message: '请输入客户名称' }]}
        >
          <Input placeholder="请输入客户名称" maxLength={200} />
        </Form.Item>

        <Form.Item label="客户类型" name="type">
          <Select>
            <Select.Option value={1}>企业</Select.Option>
            <Select.Option value={2}>个人</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="行业" name="industry">
          <Input placeholder="请输入所属行业" maxLength={100} />
        </Form.Item>

        <Form.Item label="联系人" name="contact">
          <Input placeholder="请输入联系人姓名" maxLength={50} />
        </Form.Item>

        <Form.Item label="联系电话" name="phone">
          <Input placeholder="请输入联系电话" maxLength={20} />
        </Form.Item>

        <Form.Item label="邮箱" name="email">
          <Input placeholder="请输入邮箱地址" maxLength={100} type="email" />
        </Form.Item>

        <Form.Item label="地址" name="address">
          <Input.TextArea placeholder="请输入地址" maxLength={500} rows={2} />
        </Form.Item>

        <Form.Item label="状态" name="status">
          <Select>
            <Select.Option value={1}>启用</Select.Option>
            <Select.Option value={0}>禁用</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="备注" name="remark">
          <Input.TextArea placeholder="请输入备注" maxLength={500} rows={2} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

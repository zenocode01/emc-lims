import { useEffect } from 'react'
import { Modal, Form, Input, Select, message } from 'antd'
import { standardApi, type StandardVO, type StandardDTO } from '../../api/standard'

interface StandardFormProps {
  open: boolean
  data?: StandardVO
  onSuccess: () => void
  onCancel: () => void
}

/** 标准类型选项 */
const typeOptions = [
  { label: '排放', value: 'emission' },
  { label: '抗扰度', value: 'immunity' },
]

/** 状态选项 */
const statusOptions = [
  { label: '已启用', value: 'enabled' },
  { label: '已停用', value: 'disabled' },
]

/**
 * 标准新增/编辑表单弹窗
 */
export default function StandardForm({ open, data, onSuccess, onCancel }: StandardFormProps) {
  const [form] = Form.useForm<StandardDTO>()
  const isEdit = !!data

  useEffect(() => {
    if (open) {
      if (data) {
        form.setFieldsValue({
          id: data.id,
          code: data.code,
          name: data.name,
          version: data.version,
          issuingOrg: data.issuingOrg,
          effectiveDate: data.effectiveDate,
          expiryDate: data.expiryDate,
          status: data.status || 'enabled',
          type: data.type,
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
        await standardApi.update(values)
        message.success('更新成功')
      } else {
        await standardApi.create(values)
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
      title={isEdit ? '编辑标准' : '新增标准'}
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
        initialValues={{ status: 'enabled', type: 'immunity' }}
      >
        <Form.Item name="id" hidden>
          <Input />
        </Form.Item>

        <Form.Item
          label="标准编号"
          name="code"
          rules={[{ required: true, message: '请输入标准编号' }]}
        >
          <Input placeholder="例如：GB/T 17626.2" maxLength={100} />
        </Form.Item>

        <Form.Item
          label="标准名称"
          name="name"
          rules={[{ required: true, message: '请输入标准名称' }]}
        >
          <Input placeholder="请输入标准名称" maxLength={200} />
        </Form.Item>

        <Form.Item label="版本号" name="version">
          <Input placeholder="例如：2006" maxLength={50} />
        </Form.Item>

        <Form.Item label="发布机构" name="issuingOrg">
          <Input placeholder="例如：国家标准化管理委员会" maxLength={200} />
        </Form.Item>

        <Form.Item label="生效日期" name="effectiveDate">
          <Input placeholder="YYYY-MM-DD" maxLength={10} />
        </Form.Item>

        <Form.Item label="失效日期" name="expiryDate">
          <Input placeholder="YYYY-MM-DD" maxLength={10} />
        </Form.Item>

        <Form.Item label="标准类型" name="type">
          <Select options={typeOptions} />
        </Form.Item>

        <Form.Item label="状态" name="status">
          <Select options={statusOptions} />
        </Form.Item>

        <Form.Item label="备注" name="remark">
          <Input.TextArea placeholder="请输入备注" maxLength={500} rows={3} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

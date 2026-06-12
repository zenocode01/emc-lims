import { useEffect } from 'react'
import { Modal, Form, Input, Select, DatePicker, message } from 'antd'
import { personnelApi, type PersonnelVO, type PersonnelDTO } from '../../api/personnel'

interface PersonnelFormProps {
  open: boolean
  data?: PersonnelVO
  onSuccess: () => void
  onCancel: () => void
}

/**
 * 人员新增/编辑表单弹窗
 */
export default function PersonnelForm({
  open,
  data,
  onSuccess,
  onCancel,
}: PersonnelFormProps) {
  const [form] = Form.useForm<PersonnelDTO>()
  const isEdit = !!data

  useEffect(() => {
    if (open) {
      if (data) {
        form.setFieldsValue({
          id: data.id,
          userId: data.userId,
          name: data.name,
          idCard: data.idCard,
          education: data.education,
          major: data.major,
          title: data.title,
          hireDate: data.hireDate ? data.hireDate : undefined,
          status: data.status ?? '1',
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
        await personnelApi.update(values)
        message.success('更新成功')
      } else {
        await personnelApi.create(values)
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
      title={isEdit ? '编辑人员' : '新增人员'}
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
        initialValues={{ status: '1' }}
      >
        <Form.Item name="id" hidden>
          <Input />
        </Form.Item>

        <Form.Item
          label="工号"
          name="userId"
          rules={[{ required: true, message: '请输入工号' }]}
        >
          <Input placeholder="请输入工号" maxLength={50} />
        </Form.Item>

        <Form.Item
          label="姓名"
          name="name"
          rules={[{ required: true, message: '请输入姓名' }]}
        >
          <Input placeholder="请输入姓名" maxLength={50} />
        </Form.Item>

        <Form.Item label="身份证号" name="idCard">
          <Input placeholder="请输入身份证号" maxLength={18} />
        </Form.Item>

        <Form.Item label="学历" name="education">
          <Select placeholder="请选择学历">
            <Select.Option value="doctoral">博士</Select.Option>
            <Select.Option value="master">硕士</Select.Option>
            <Select.Option value="bachelor">本科</Select.Option>
            <Select.Option value="college">大专</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="专业" name="major">
          <Input placeholder="请输入专业" maxLength={100} />
        </Form.Item>

        <Form.Item label="职称" name="title">
          <Select placeholder="请选择职称">
            <Select.Option value="senior">高级职称</Select.Option>
            <Select.Option value="intermediate">中级职称</Select.Option>
            <Select.Option value="junior">初级职称</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="入职日期" name="hireDate">
          <DatePicker style={{ width: '100%' }} placeholder="请选择入职日期" />
        </Form.Item>

        <Form.Item label="状态" name="status">
          <Select>
            <Select.Option value="1">启用</Select.Option>
            <Select.Option value="0">停用</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="备注" name="remark">
          <Input.TextArea placeholder="请输入备注" maxLength={500} rows={2} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

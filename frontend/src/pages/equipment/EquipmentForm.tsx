import { useEffect } from 'react'
import { Modal, Form, Input, Select, DatePicker, message } from 'antd'
import { equipmentApi, type EquipmentVO, type EquipmentDTO } from '../../api/equipment'

/** 设备状态选项 */
const STATUS_OPTIONS = [
  { label: '正常', value: 'normal' },
  { label: '维修中', value: 'maintenance' },
  { label: '校准中', value: 'calibration' },
  { label: '报废', value: 'scrap' },
]

interface EquipmentFormProps {
  open: boolean
  data?: EquipmentVO
  onSuccess: () => void
  onCancel: () => void
}

/**
 * 设备新增/编辑表单弹窗
 */
export default function EquipmentForm({ open, data, onSuccess, onCancel }: EquipmentFormProps) {
  const [form] = Form.useForm<EquipmentDTO>()
  const isEdit = !!data

  useEffect(() => {
    if (open) {
      if (data) {
        form.setFieldsValue({
          id: data.id,
          name: data.name,
          model: data.model,
          manufacturer: data.manufacturer,
          serialNo: data.serialNo,
          location: data.location,
          status: data.status || 'normal',
          calibrationDue: data.calibrationDue ? data.calibrationDue : undefined,
          lastCalibration: data.lastCalibration ? data.lastCalibration : undefined,
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
        await equipmentApi.update(values)
        message.success('更新成功')
      } else {
        await equipmentApi.create(values)
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
      title={isEdit ? '编辑设备' : '新增设备'}
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
        initialValues={{ status: 'normal' }}
      >
        <Form.Item name="id" hidden>
          <Input />
        </Form.Item>

        <Form.Item
          label="设备名称"
          name="name"
          rules={[{ required: true, message: '请输入设备名称' }]}
        >
          <Input placeholder="请输入设备名称" maxLength={200} />
        </Form.Item>

        <Form.Item
          label="型号"
          name="model"
          rules={[{ required: true, message: '请输入型号' }]}
        >
          <Input placeholder="请输入设备型号" maxLength={200} />
        </Form.Item>

        <Form.Item label="制造商" name="manufacturer">
          <Input placeholder="请输入生产厂家" maxLength={200} />
        </Form.Item>

        <Form.Item label="序列号" name="serialNo">
          <Input placeholder="请输入序列号" maxLength={200} />
        </Form.Item>

        <Form.Item label="存放位置" name="location">
          <Input placeholder="请输入存放位置" maxLength={200} />
        </Form.Item>

        <Form.Item label="状态" name="status">
          <Select options={STATUS_OPTIONS} />
        </Form.Item>

        <Form.Item label="上次校准日期" name="lastCalibration">
          <DatePicker style={{ width: '100%' }} placeholder="请选择上次校准日期" />
        </Form.Item>

        <Form.Item label="下次校准日期" name="calibrationDue">
          <DatePicker style={{ width: '100%' }} placeholder="请选择下次校准日期" />
        </Form.Item>

        <Form.Item label="备注" name="remark">
          <Input.TextArea placeholder="请输入备注" maxLength={500} rows={3} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

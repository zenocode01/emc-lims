import { useEffect, useState } from 'react'
import { Modal, Form, Input, DatePicker, Select, InputNumber, message } from 'antd'
import type { FormInstance } from 'antd'
import dayjs from 'dayjs'
import {
  sampleApi,
  type SampleDTO,
  type SampleVO,
} from '../../api/sample'
import { customerApi } from '../../api/customer'
import type { CustomerVO } from '../../api/customer'

interface SampleFormProps {
  open: boolean
  data: SampleVO | undefined
  onSuccess: () => void
  onCancel: () => void
}

/**
 * 样品新增/编辑表单
 */
export default function SampleForm({ open, data, onSuccess, onCancel }: SampleFormProps) {
  const [form] = Form.useForm<SampleDTO>()
  const [submitting, setSubmitting] = useState(false)
  const [customerList, setCustomerList] = useState<CustomerVO[]>([])
  const [isLoadingCustomers, setIsLoadingCustomers] = useState(false)

  /** 加载客户列表 */
  const loadCustomers = async () => {
    setIsLoadingCustomers(true)
    try {
      const res = await customerApi.page({
        pageNum: 1,
        pageSize: 1000,
      })
      setCustomerList(res.records)
    } catch {
      // error handled by interceptor
    } finally {
      setIsLoadingCustomers(false)
    }
  }

  useEffect(() => {
    loadCustomers()
  }, [])

  /** 表单重置 */
  useEffect(() => {
    if (open && data) {
      form.setFieldsValue({
        id: data.id,
        customerId: data.customerId,
        contractId: data.contractId,
        productName: data.productName,
        model: data.model,
        manufacturer: data.manufacturer,
        batchNo: data.batchNo,
        receiveDate: data.receiveDate ? dayjs(data.receiveDate) : null,
        sampleCount: data.sampleCount,
        testStandards: data.testStandards,
        testRequirements: data.testRequirements,
        testerId: data.testerId,
        remark: data.remark,
      })
    } else if (open) {
      form.resetFields()
      // 设置默认收样日期为今天
      form.setFieldValue('receiveDate', dayjs())
    }
  }, [open, data, form])

  /** 提交 */
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setSubmitting(true)

      const dto: SampleDTO = {
        ...values,
        id: data?.id,
        customerId: values.customerId!,
        receiveDate: values.receiveDate ? values.receiveDate.format('YYYY-MM-DD') : '',
      }

      if (data?.id) {
        await sampleApi.update(dto)
        message.success('更新成功')
      } else {
        await sampleApi.create(dto)
        message.success('收样登记成功')
      }

      onSuccess()
    } catch {
      // error handled by interceptor
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal
      title={data?.id ? '编辑样品' : '新增样品（收样登记）'}
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={submitting}
      width={700}
      destroyOnClose
    >
      <Form form={form} layout="vertical" initialValues={{ sampleCount: 1 }}>
        <Form.Item
          label="客户"
          name="customerId"
          rules={[{ required: true, message: '请选择客户' }]}
        >
          <Select
            placeholder="请选择客户"
            loading={isLoadingCustomers}
            showSearch
            optionFilterProp="label"
            filterOption={(input, option) =>
              ((option?.label as string) ?? '').toLowerCase().includes(input.toLowerCase())
            }
            options={customerList.map((c) => ({
              label: c.name,
              value: c.id,
            }))}
          />
        </Form.Item>

        <Form.Item label="合同ID" name="contractId">
          <InputNumber
            placeholder="请输入合同ID（可选）"
            style={{ width: '100%' }}
            min={1}
          />
        </Form.Item>

        <Form.Item
          label="产品名称"
          name="productName"
          rules={[{ required: true, message: '请输入产品名称' }]}
        >
          <Input placeholder="请输入产品名称" maxLength={100} />
        </Form.Item>

        <Form.Item label="型号" name="model">
          <Input placeholder="请输入型号" maxLength={100} />
        </Form.Item>

        <Form.Item label="生产厂家" name="manufacturer">
          <Input placeholder="请输入生产厂家" maxLength={200} />
        </Form.Item>

        <Form.Item label="批号/序列号" name="batchNo">
          <Input placeholder="请输入批号或序列号" maxLength={100} />
        </Form.Item>

        <Form.Item
          label="收样日期"
          name="receiveDate"
          rules={[{ required: true, message: '请选择收样日期' }]}
        >
          <DatePicker style={{ width: '100%' }} disabledDate={(current) => current && current > dayjs().endOf('day')} />
        </Form.Item>

        <Form.Item label="样品数量" name="sampleCount">
          <InputNumber
            min={1}
            style={{ width: '100%' }}
            formatter={(value) => `${value} 件`}
            parser={(value) => Number(value?.replace(/ 件/g, '')) || 1}
          />
        </Form.Item>

        <Form.Item label="测试标准" name="testStandards">
          <Input.TextArea
            placeholder="请输入测试标准，如：IEC 61000-4-2, IEC 61000-4-3"
            rows={2}
            maxLength={500}
          />
        </Form.Item>

        <Form.Item label="测试要求" name="testRequirements">
          <Input.TextArea
            placeholder="请输入特殊测试要求"
            rows={3}
            maxLength={500}
          />
        </Form.Item>

        <Form.Item label="测试工程师" name="testerId">
          <Select
            placeholder="请选择测试工程师（可选）"
            allowClear
            showSearch
            optionFilterProp="label"
            filterOption={(input, option) =>
              ((option?.label as string) ?? '').toLowerCase().includes(input.toLowerCase())
            }
            options={[
              { label: '工程师A', value: 1 },
              { label: '工程师B', value: 2 },
              { label: '工程师C', value: 3 },
            ]}
          />
        </Form.Item>

        <Form.Item label="备注" name="remark">
          <Input.TextArea placeholder="请输入备注" rows={2} maxLength={500} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

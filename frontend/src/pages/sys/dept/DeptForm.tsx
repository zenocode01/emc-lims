import { useEffect, useState, useMemo } from 'react'
import { Form, Input, Modal, Select, InputNumber, message, TreeSelect } from 'antd'
import { sysDeptApi, type SysDeptVO } from '../../../api/sys'

interface DeptFormProps {
  open: boolean
  data?: SysDeptVO
  onSuccess: () => void
  onCancel: () => void
}

export default function DeptForm({
  open,
  data,
  onSuccess,
  onCancel,
}: DeptFormProps) {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [deptTree, setDeptTree] = useState<SysDeptVO[]>([])

  useEffect(() => {
    if (!open) return
    const load = async () => {
      try {
        const tree = await sysDeptApi.tree()
        setDeptTree(tree)
        if (data) {
          form.setFieldsValue({
            name: data.name,
            code: data.code,
            deptType: data.deptType,
            parentId: data.parentId,
            leader: data.leader,
            phone: data.phone,
            email: data.email,
            status: data.status,
            sort: data.sort,
          })
        } else {
          form.resetFields()
          form.setFieldsValue({
            parentId: 0,
            status: 1,
            sort: 0,
            deptType: 2,
          })
        }
      } catch {
        // error handled
      }
    }
    load()
  }, [open, data, form])

  const deptOptions = useMemo(() => {
    if (deptTree.length === 0) return [{ label: '顶级部门', value: 0 }]
    const flat = flattenTree(deptTree)
    return [{ label: '顶级部门', value: 0 }, ...flat]
  }, [deptTree])

  const flattenTree = (nodes: SysDeptVO[], prefix = ''): { label: string; value: number }[] => {
    let result: { label: string; value: number }[] = []
    for (const node of nodes) {
      result.push({
        label: `${prefix}${node.name}`,
        value: node.id!,
      })
      if (node.children && node.children.length > 0) {
        result = result.concat(flattenTree(node.children, `${prefix}${node.name} / `))
      }
    }
    return result
  }

  const handleSubmit = async () => {
    const values = await form.validateFields()
    setLoading(true)
    try {
      const dto = {
        ...values,
        id: data?.id,
      }
      if (data?.id) {
        await sysDeptApi.update(dto)
        message.success('更新成功')
      } else {
        await sysDeptApi.create(dto)
        message.success('创建成功')
      }
      onSuccess()
    } catch {
      // error handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      title={data ? '编辑部门' : '新增部门'}
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      width={600}
    >
      <Form form={form} layout="vertical">
        <Form.Item name="parentId" label="上级部门">
          <TreeSelect
            treeData={deptOptions}
            placeholder="请选择上级部门"
            allowClear
            treeDefaultExpandAll
          />
        </Form.Item>

        <Form.Item
          name="name"
          label="部门名称"
          rules={[{ required: true, message: '请输入部门名称' }]}
        >
          <Input placeholder="请输入部门名称" />
        </Form.Item>

        <Form.Item
          name="code"
          label="部门编码"
          rules={[{ required: true, message: '请输入部门编码' }]}
        >
          <Input placeholder="请输入部门编码" />
        </Form.Item>

        <Form.Item name="deptType" label="部门类型" initialValue={2}>
          <Select>
            <Select.Option value={1}>公司</Select.Option>
            <Select.Option value={2}>部门</Select.Option>
            <Select.Option value={3}>小组</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item name="leader" label="负责人">
          <Input placeholder="请输入负责人" />
        </Form.Item>

        <Form.Item name="phone" label="联系电话">
          <Input placeholder="请输入联系电话" />
        </Form.Item>

        <Form.Item name="email" label="邮箱">
          <Input placeholder="请输入邮箱" />
        </Form.Item>

        <Form.Item name="sort" label="排序" initialValue={0}>
          <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入排序值" />
        </Form.Item>

        <Form.Item name="status" label="状态" initialValue={1}>
          <Select>
            <Select.Option value={1}>启用</Select.Option>
            <Select.Option value={0}>禁用</Select.Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  )
}

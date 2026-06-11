import { useEffect, useState } from 'react'
import { Form, Input, InputNumber, Modal, Select, message, TreeSelect } from 'antd'
import { sysRoleApi, sysMenuApi, type SysRoleVO, type SysMenuVO } from '../../../api/sys'

interface RoleFormProps {
  open: boolean
  data?: SysRoleVO
  onSuccess: () => void
  onCancel: () => void
}

export default function RoleForm({
  open,
  data,
  onSuccess,
  onCancel,
}: RoleFormProps) {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [menuTree, setMenuTree] = useState<SysMenuVO[]>([])
  const [checkedKeys, setCheckedKeys] = useState<number[]>([])

  useEffect(() => {
    if (!open) return
    const load = async () => {
      try {
        const tree = await sysMenuApi.tree()
        setMenuTree(tree)
        if (data) {
          // 获取角色已授权的菜单ID
          const menuIds = await sysRoleApi.getMenuIds(data.id!)
          setCheckedKeys(menuIds)
          form.setFieldsValue({
            roleName: data.roleName,
            roleCode: data.roleCode,
            roleDesc: data.roleDesc,
            dataScope: data.dataScope,
            sort: data.sort,
            status: data.status,
          })
        } else {
          form.resetFields()
          form.setFieldsValue({
            status: 1,
            sort: 0,
            dataScope: 2,
          })
        }
      } catch {
        // error handled
      }
    }
    load()
  }, [open, data, form])

  const handleSubmit = async () => {
    const values = await form.validateFields()
    setLoading(true)
    try {
      const dto = {
        ...values,
        id: data?.id,
      }
      if (data?.id) {
        await sysRoleApi.update(dto)
        message.success('更新成功')
      } else {
        await sysRoleApi.create(dto)
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
      title={data ? '编辑角色' : '新增角色'}
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      width={600}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="roleName"
          label="角色名称"
          rules={[{ required: true, message: '请输入角色名称' }]}
        >
          <Input placeholder="请输入角色名称" />
        </Form.Item>

        <Form.Item
          name="roleCode"
          label="角色编码"
          rules={[
            { required: true, message: '请输入角色编码' },
            { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线' },
          ]}
        >
          <Input placeholder="请输入角色编码（如：admin）" />
        </Form.Item>

        <Form.Item name="roleDesc" label="描述">
          <Input.TextArea placeholder="请输入角色描述" rows={3} />
        </Form.Item>

        <Form.Item name="dataScope" label="数据权限" initialValue={2}>
          <Select>
            <Select.Option value={1}>全部数据</Select.Option>
            <Select.Option value={2}>本部门数据</Select.Option>
            <Select.Option value={3}>本部门及子部门</Select.Option>
            <Select.Option value={4}>仅本人数据</Select.Option>
          </Select>
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

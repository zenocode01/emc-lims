import { useEffect, useState } from 'react'
import { Form, Input, Modal, Select, message, TreeSelect } from 'antd'
import { sysMenuApi, type SysMenuVO } from '../../../api/sys'

interface MenuFormProps {
  open: boolean
  data?: SysMenuVO
  onSuccess: () => void
  onCancel: () => void
}

export default function MenuForm({
  open,
  data,
  onSuccess,
  onCancel,
}: MenuFormProps) {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [menuList, setMenuList] = useState<SysMenuVO[]>([])

  useEffect(() => {
    if (!open) return
    const load = async () => {
      try {
        const list = await sysMenuApi.tree()
        setMenuList(list)
        if (data) {
          form.setFieldsValue({
            menuName: data.menuName,
            menuType: data.menuType,
            path: data.path,
            component: data.component,
            permission: data.permission,
            parentId: data.parentId,
            sort: data.sort,
            icon: data.icon,
            isHidden: data.isHidden,
            status: data.status,
          })
        } else {
          form.resetFields()
          form.setFieldsValue({
            menuType: 1,
            status: 1,
            sort: 0,
            isHidden: 0,
            parentId: 0,
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
        await sysMenuApi.update(dto)
        message.success('更新成功')
      } else {
        await sysMenuApi.create(dto)
        message.success('创建成功')
      }
      onSuccess()
    } catch {
      // error handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  const menuOptions = [{ id: 0, title: '根目录', children: menuList }]

  return (
    <Modal
      title={data ? '编辑菜单' : '新增菜单'}
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      width={600}
    >
      <Form form={form} layout="vertical">
        <Form.Item name="parentId" label="上级菜单">
          <TreeSelect
            treeData={menuOptions}
            placeholder="请选择上级菜单"
            allowClear
            treeDefaultExpandAll
          />
        </Form.Item>

        <Form.Item
          name="menuName"
          label="菜单名称"
          rules={[{ required: true, message: '请输入菜单名称' }]}
        >
          <Input placeholder="请输入菜单名称" />
        </Form.Item>

        <Form.Item name="menuType" label="类型" initialValue={1}>
          <Select>
            <Select.Option value={1}>目录</Select.Option>
            <Select.Option value={2}>菜单</Select.Option>
            <Select.Option value={3}>按钮</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="path"
          label="路由路径"
          rules={[{ required: true, message: '请输入路由路径' }]}
        >
          <Input placeholder="例如: /sys/user" />
        </Form.Item>

        <Form.Item name="component" label="组件路径">
          <Input placeholder="例如: sys/user" />
        </Form.Item>

        <Form.Item name="permission" label="权限标识">
          <Input placeholder="例如: sys:user:query" />
        </Form.Item>

        <Form.Item name="icon" label="图标">
          <Input placeholder="Ant Design 图标名，如：UserOutlined" />
        </Form.Item>

        <Form.Item name="isHidden" label="是否隐藏" initialValue={0}>
          <Select>
            <Select.Option value={0}>显示</Select.Option>
            <Select.Option value={1}>隐藏</Select.Option>
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
